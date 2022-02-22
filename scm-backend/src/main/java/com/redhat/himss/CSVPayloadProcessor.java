package com.redhat.himss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.camel.Exchange;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@ApplicationScoped
public class CSVPayloadProcessor {

    private static Logger log = Logger.getLogger(Routes.class);

    @Inject
    @DataSource("camel-ds")
    AgroalDataSource dataSource;

    @ConfigProperty(name="himss.scm.delay.db.persist.millis", defaultValue = "0")
    int delayDBPersistMillis = 0;

    @Counted(name = "csvProcessed", description = "How many csv payloads have been processed.")
    @Timed(name = "csvProcessingTimer", description = "A measure of how long it takes to process a CSV file.", unit = MetricUnits.MILLISECONDS)
    public void process(Exchange exchange){
        Connection con = null;
        PreparedStatement pStatement = null;
        try{
            byte[] fileNameHeaderBytes = (byte[])exchange.getIn().getHeader(Util.FILE_NAME_HEADER);
            String fHeader = new String(fileNameHeaderBytes);


            // 1) Build appropriate prepared statement based on file type
            StringBuilder sBuilder = new StringBuilder();
            if(fHeader.startsWith(Util.AM3X)){
                sBuilder.append(Util.AM3X_INSERT_SQL_HEADER);
            }else if(fHeader.startsWith(Util.DETM)) {
                sBuilder.append("insert into "+Util.DETM+"(device_code, centrally_managed_indicator, retired_indicator, device_description,risk_level_type_code, device_class_code, accountable_equipment_code, maintenance_required_indicator, life_expectancy,delete_indicator,specialty_code, federal_supply_class_fsc) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
            }else{
                throw new ValidationException("000005 wrong file type: "+fHeader);
            }
            con = dataSource.getConnection();
            pStatement = con.prepareStatement(sBuilder.toString());


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
            if(this.delayDBPersistMillis > 0){
                log.warnv("will intentionally delay persist by the following millis: {0}", this.delayDBPersistMillis);
                Thread.sleep(this.delayDBPersistMillis);
            }
            int[] results = pStatement.executeBatch();
        }catch(Throwable x){
            x.printStackTrace();
        }finally {
            if(con != null)
                try {
                    pStatement.close();
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }

    }
}
