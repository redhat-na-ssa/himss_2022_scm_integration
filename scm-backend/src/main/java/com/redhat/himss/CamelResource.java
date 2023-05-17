package com.redhat.himss;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.runtime.StartupEvent;
import org.apache.camel.CamelContext;

import jakarta.annotation.PostConstruct;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CamelResource {

    private static Logger log = Logger.getLogger(CamelResource.class);

    @Inject
    @DataSource("camel-ds")
    AgroalDataSource dataSource;

    @ConfigProperty(name="himss.scm.flush.db.at.startup")
    boolean flushDbAtStartup = false;

    void startup(@Observes StartupEvent event, CamelContext context) throws Exception {
        context.getRouteController().startAllRoutes();
    }

    @PostConstruct
    void postConstruct() throws Exception {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(true);
            if(flushDbAtStartup){
                String flushTable = "TRUNCATE "+Util.AM3X+", "+Util.DETM+";";
                Statement statement = con.createStatement();
                statement.execute(flushTable);
                log.warn("postConstruct() just flushed all tables");
            }else{
                Statement statement = con.createStatement();
                statement.execute("select count(id) from "+Util.AM3X+";");
                ResultSet rSet = statement.getResultSet();
                rSet.next();
                int count = rSet.getInt(1);
                log.infov("# of records in {0} = {1}", Util.AM3X, count);
            }
        }
    }
}
