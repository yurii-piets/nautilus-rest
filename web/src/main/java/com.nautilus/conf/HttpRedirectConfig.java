package com.nautilus.conf;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpRedirectConfig {

    private static final String USER_CONSTRAINT = "CONFIDENTIAL";

    private static final int HTTP_PORT = 8080;
    private static final int REDIRECT_PORT = 8443;
    private static final String BASIC_SCHEME = "http";

    private static final String PROTOCOL_PACKAGE = "org.apache.coyote.http11.Http11NioProtocol";
    private static final String SECURITY_CONNECTION_PATTERN = "/*";

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint(USER_CONSTRAINT);
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern(SECURITY_CONNECTION_PATTERN);
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };

        tomcat.addAdditionalTomcatConnectors(initiateHttpConnector());
        return tomcat;
    }

    private Connector initiateHttpConnector() {
        Connector connector = new Connector(PROTOCOL_PACKAGE);
        connector.setScheme(BASIC_SCHEME);
        connector.setPort(HTTP_PORT);
        connector.setSecure(false);
        connector.setRedirectPort(REDIRECT_PORT);

        return connector;
    }
}
