package com.redhat.himss;

import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class Route  extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        from("kafka:{{scm_topic_name}}?brokers={{kafka.bootstrap.servers}}&groupId=scm-camel-quarkus&autoOffsetReset=earliest")
        //should probably change the offset
            .to("direct:echo");

        from("direct:echo")
         .log("received: ${body}");
    }
}