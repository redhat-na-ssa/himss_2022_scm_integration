package com.redhat.himss;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.camel.Exchange;

import io.agroal.api.AgroalDataSource;
import io.netty.util.internal.StringUtil;
import io.quarkus.agroal.DataSource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import org.springframework.util.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;

@ApplicationScoped
public class CSVPayloadProcessor {

    private static Logger log = Logger.getLogger(Routes.class);
    private static DateFormat dfObj = new SimpleDateFormat();
    private static long zeroLong = 0L;

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
                sBuilder.append(Util.DETM_INSERT_SQL_HEADER);
            }else{
                throw new ValidationException("000005 wrong file type: "+fHeader);
            }
            con = dataSource.getConnection();
            pStatement = con.prepareStatement(sBuilder.toString());


            // 2) Iterate through rows of body and add batch
            String body = (String)exchange.getIn().getBody();
            String[] rows = body.split("\n");
            log.info("CSVPayloadProcessor.process() "+fHeader+" :   # of rows = "+rows.length);
            if(fHeader.startsWith(Util.AM3X)){
                prepareAM3X(rows, pStatement);
            }else if(fHeader.startsWith(Util.DETM)) {
                prepareDETM(rows, pStatement);
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

    private void prepareAM3X(String[] rows, PreparedStatement pStatement) {
        for(String row : rows){
            String[] fields = row.split("\\|");
            try {
                //log.info("# of fields = "+fields.length);
                pStatement.setString(1, fields[0]); // item_id
                pStatement.setString(2, fields[1]); // sub_organization_identifier_sub_org_id
                pStatement.setString(3, fields[2]); // assemblage_identifier
                pStatement.setString(4, fields[3]); // assemblage_increment
                pStatement.setString(5, fields[4]); // assemblage_sub_assemblage
                pStatement.setInt(6, StringUtil.isNullOrEmpty(fields[5]) ? 0 : Integer.parseInt(fields[5])); // assemblage_instance_number
                pStatement.setString(7, fields[6]); // stratification_state
                pStatement.setInt(8, StringUtil.isNullOrEmpty(fields[7]) ? 0 : Integer.parseInt(fields[7])); // on_hand_quantity
                pStatement.setInt(9, StringUtil.isNullOrEmpty(fields[8]) ? 0 : Integer.parseInt(fields[8])); // equipment_control_number 
                pStatement.setString(10, fields[9]);// location 
                pStatement.setString(11, fields[10]); // incomplete_flag_indicator
                pStatement.setString(12, fields[11]); // manufacturer_common_name 
                pStatement.setInt(13, StringUtil.isNullOrEmpty(fields[12]) ? 0 : Integer.parseInt(fields[12])); // excess_quantity_remaining

                long dl = StringUtil.isNullOrEmpty(fields[13]) ? zeroLong :   Long.parseLong(fields[13]);
                pStatement.setDate(14, new java.sql.Date(dl)); // expiration_date

                pStatement.setString(15, fields[14]); // lot_number

                dl = StringUtil.isNullOrEmpty(fields[15]) ? zeroLong :   Long.parseLong(fields[15]);
                pStatement.setDate(16, new java.sql.Date(dl)); // expiration_extension_date

                pStatement.setString(17, fields[16]); // manufacturer_name
                pStatement.setInt(18, StringUtil.isNullOrEmpty(fields[17]) ? 0 : Integer.parseInt(fields[17])); // product_number
                pStatement.addBatch();

            }catch(Exception x){
                log.error("Problem parsing the following row: "+row);
                x.printStackTrace();
            }
        }
    }

    private void prepareDETM(String[] rows, PreparedStatement pStatement) {
        for(String row : rows){
            String[] fields = row.split("\\|");
            try{
                //log.info("# of fields = "+fields.length);
                pStatement.setString(1, fields[0]); // device_code
                pStatement.setString(2, fields[1]); //centrally_managed_indicator
                pStatement.setString(3, fields[2]); // retired_indicator
                pStatement.setString(4, fields[3]); // device_description
                pStatement.setInt(5, StringUtil.isNullOrEmpty(fields[4]) ? 0 : Integer.parseInt(fields[4])); // risk_level_type_code
                pStatement.setString(6, fields[5]); // device_class_code
                pStatement.setString(7, fields[6]); // accountable_equipment_code
                pStatement.setString(8, fields[7]); // maintenance_required_indicator
                pStatement.setInt(9, StringUtil.isNullOrEmpty(fields[8]) ? 0 : Integer.parseInt(fields[8])); //  life_expectancy
                pStatement.setString(10, fields[9]); // delete_indicator
                pStatement.setString(11, fields[10]); // specialty_code
                pStatement.setInt(12, StringUtil.isNullOrEmpty(fields[11]) ? 0 : Integer.parseInt(fields[11])); //  federal_supply_class_fsc
                pStatement.addBatch();
            }catch(Exception x){
                log.error("Problem parsing the following row: "+row);
                x.printStackTrace();
            }
        }
    }
}
