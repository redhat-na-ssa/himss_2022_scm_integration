// camel-k: language=java
//kamel run ScmEchoService.java --dev --name scm-echo -d camel-rest --trait knative-service.enabled=true --trait knative-service.autoscaling-target=1 --trait knative-service.max-scale=10


import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;


public class ScmEchoService  extends RouteBuilder{

    @Override
    public void configure() throws Exception {

        rest("/")
         .post().to("direct:echo");

        from("direct:echo")
         .log("received: ${body}");
    }
}