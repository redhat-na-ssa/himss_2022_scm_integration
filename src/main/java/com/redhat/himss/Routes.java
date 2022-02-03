package com.redhat.himss;

import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.config.Configuration;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.tarfile.TarSplitter;
import org.apache.camel.model.rest.RestBindingMode;

/**
 * Camel route definitions.
 */
@ApplicationScoped
public class Routes extends RouteBuilder {

    private static final String RESPONSE_HEADER="RESPONSE_HEADER";
    private static final String FILE_NAME_HEADER="FILE_NAME_HEADER";
    private static Logger log = Logger.getLogger(Routes.class);    
    
    @Inject
    @DataSource("camel-ds")
    AgroalDataSource dataSource;

    public Routes() {
    }

    @Override
    public void configure() throws Exception {

        restConfiguration().bindingMode(RestBindingMode.json);

        /*****                Consume from HTTP           *************/
        rest("/sanityCheck")
                .get()
                .route()
                .setBody().constant("Good To Go!")
                .endRest();

        // curl -v -X POST -F "data=@src/test/himss/nogood/AM3X-365115-1002-1-2021285.txt" localhost:8180/gzippedFiles
        // curl -v -X POST -F "data=@src/test/himss/good/AM3X-365115-2021285.tgz" localhost:8180/gzippedFiles
        // curl -v -X POST -F "data=@src/test/himss/good/AM3X-365115-2021285.tgz" -F "data=@src/test/himss/good/DDAS-365115-2021285.tgz" localhost:8180/gzippedFiles
        rest("/gzippedFiles")
          .description("Consume Gzipped Files")
          .consumes("multipart/form-data")
          .post()
          .to("direct:verifyPayload");
        
        from("direct:verifyPayload")
          .routeId("verifyPayload")
          .setHeader(RESPONSE_HEADER, constant("ALL FILES PERSISTED"))
          .doTry()
                .process(new GzipPayloadValidator())
                .process(e -> {
                    Map<String, DataHandler> attachments = e.getIn(AttachmentMessage.class).getAttachments();
                    Collection<byte[]> aList = new ArrayList<byte[]>();
                    for(Entry<String, DataHandler> attachment  : attachments.entrySet()) {
                        byte[] bytes = attachment.getValue().getInputStream().readAllBytes();
                        aList.add(bytes);
                    }
                    e.getIn().setBody(aList);
                })
                .to("seda:writeVerifiedPayloadToFilesystem")
            
          .doCatch(ValidationException.class)
                .setHeader("CamelHttpResponseCode").constant("415")
                .setHeader(RESPONSE_HEADER, exceptionMessage())
                .log(LoggingLevel.ERROR, exceptionMessage().toString())
          .doFinally()
                .setBody().header(RESPONSE_HEADER)
          .end();
            
        from("seda:writeVerifiedPayloadToFilesystem")
            .routeId("direct:writeVerifiedPayloadToFilesystem")
            .split(body())
            .to("file:{{himss.scm.gzip.location}}");




    
        /*****                Consume from filesystem           *************/
        from("file:{{himss.scm.gzip.location}}?initialDelay=0&delay=1000&autoCreate=true&delete=true")
            .routeId("direct:unpackGzip")
                .unmarshal()
                .gzipDeflater()
                //.log("file = ${header.CamelFileName}}")
                .split(new TarSplitter())
                  .streaming()
                  .process(e -> {
                      String originalFileName = (String)e.getIn().getHeader("CamelFileName");
                      int lastDirIndex = originalFileName.lastIndexOf("/");
                      String parsedFileName = originalFileName.substring(lastDirIndex+1);
                      log.debug("prepKafkaProducer()  originalFileName = "+originalFileName+" : parsedFileName = "+parsedFileName);
                      e.getIn().setHeader(FILE_NAME_HEADER, parsedFileName);
                  })
                  //.to("direct:processTextFile")
                  .to("kafka:{{scm_topic_name}}?brokers={{kafka.bootstrap.servers}}")
                .end();

        from("direct:processTextFile")
            .routeId("direct:processTextFile")
            .log("file = ${header.CamelFileName}}")
            .end();

        
        /************               Consume from Kafka          *****************/
        from("kafka:{{scm_topic_name}}?brokers={{kafka.bootstrap.servers}}&groupId=scm&autoOffsetReset=earliest")
            .doTry()
                .process(new CSVPayloadValidator())
                .process(new CSVPayloadProcessor())
            .doCatch(ValidationException.class)
                .log(LoggingLevel.ERROR, exceptionMessage().toString())
            .end();


    }

    class GzipPayloadValidator implements Processor {

        @Override
        public void process(Exchange exchange) throws ValidationException {

            AttachmentMessage attMsg = exchange.getIn(AttachmentMessage.class);
            Set<String> attachmentNames = attMsg.getAttachmentNames();
            for(String aName : attachmentNames) {
                log.info("GzipPayloadValidator.process()   fileName = "+aName);
                if(!aName.endsWith("tgz") && !aName.endsWith("tar.gz"))
                  throw new ValidationException("Invalid file suffix: "+aName);
            }
        }
    }



    class CSVPayloadValidator implements Processor {

        @Override
        public void process(Exchange exchange) throws ValidationException {

            Object fileNameHeaderObj = exchange.getIn().getHeader(FILE_NAME_HEADER);
            if(fileNameHeaderObj == null)
              throw new ValidationException("Must pass kafka header of: "+FILE_NAME_HEADER);

            Object bObj = exchange.getIn().getBody();
            if(!bObj.getClass().getName().equals(String.class.getName()))
              throw new ValidationException("Payload not of type String : "+bObj.getClass().getName());

        }
    }

    class CSVPayloadProcessor implements Processor {

        @Override
        public void process(Exchange exchange){
            try{
                byte[] fileNameHeaderBytes = (byte[])exchange.getIn().getHeader(FILE_NAME_HEADER);
                String fHeader = new String(fileNameHeaderBytes);


                // 1) Build appropriate prepared statement based on file type
                StringBuilder sBuilder = new StringBuilder();
                if(fHeader.startsWith(Util.AM3X)){
                    sBuilder.append("insert into "+Util.AM3X+"(column1, column2) VALUES (?,?)");
                }else if(fHeader.startsWith(Util.DDAS)) {
                    sBuilder.append("insert into "+Util.DDAS+"(column1, column2) VALUES (?,?)");
                }else if(fHeader.startsWith(Util.DETM)) {
                    sBuilder.append("insert into "+Util.DETM+"(column1, column2) VALUES (?,?)");
                }else{
                    throw new ValidationException("wrong file type: "+fHeader);
                }
                Connection con = dataSource.getConnection();
                PreparedStatement pStatement = con.prepareStatement(sBuilder.toString());


                // 2) Iterate through rows of body and add batch
                String body = (String)exchange.getIn().getBody();
                String[] rows = body.split("\n");
                log.info("CSVPayloadProcessor.process() "+fHeader+" :   # of rows = "+rows.length);
                for(String row : rows){
                    String[] fields = row.split("\\|");
                    //log.info("# of fields = "+fields.length);
                    pStatement.setString(1, "CHANGE ME");
                    pStatement.setInt(2, 49);
                    pStatement.addBatch();
                }
                int[] results = pStatement.executeBatch();
            }catch(Throwable x){
                x.printStackTrace();
            }

        }
    }

    class ValidationException extends Exception {
        private static final long serialVersionUID = 1L;

        public ValidationException() {
            super();
        }

        public ValidationException(String message){
            super(message);
        }

        public ValidationException(String message, Throwable cause){
            super(message, cause);
        }
    }
}
