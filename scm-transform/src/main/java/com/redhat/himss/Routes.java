package com.redhat.himss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;
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

    private static Logger log = Logger.getLogger(Routes.class);  
    
    @Inject
    CSVPayloadProcessor csvPayLoadProcessor;
    
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
          .setHeader(Util.RESPONSE_HEADER, constant("ALL FILES PERSISTED"))
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
                .setHeader(Util.RESPONSE_HEADER, exceptionMessage())
                .log(LoggingLevel.ERROR, exceptionMessage().toString())
          .doFinally()
                .setBody().header(Util.RESPONSE_HEADER)
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
                      e.getIn().setHeader(Util.FILE_NAME_HEADER, parsedFileName);
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
                .process(e -> {
                    csvPayLoadProcessor.process(e);
                })
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
                  throw new ValidationException("000001 Invalid file suffix: "+aName);
            }
        }
    }



    class CSVPayloadValidator implements Processor {

        @Override
        public void process(Exchange exchange) throws ValidationException {

            Object fileNameHeaderObj = exchange.getIn().getHeader(Util.FILE_NAME_HEADER);
            if(fileNameHeaderObj == null)
              throw new ValidationException("000002 Must pass kafka header of: "+Util.FILE_NAME_HEADER);

            String fHeader = new String((byte[])fileNameHeaderObj);
            if(!fHeader.endsWith(Util.TXT))
              throw new ValidationException("000003 Invalid file suffix: "+fHeader);
            

            Object bObj = exchange.getIn().getBody();
            if(!bObj.getClass().getName().equals(String.class.getName()))
              throw new ValidationException("000004 Payload not of type String : "+bObj.getClass().getName());

        }
    }
}
