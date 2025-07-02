package com.enigma.lastbite.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class SwaggerUiLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${springdoc.swagger-ui.path:/swagger-ui.html}")
    private String swaggerPath; // reflects custom path if you override it

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        WebServerApplicationContext ctx = (WebServerApplicationContext) event.getApplicationContext();
        int port = ctx.getWebServer().getPort();

        String url = "http://localhost:" + port + contextPath + swaggerPath;

        log.info("\n----------------------------------------------------------\n"
                + "  Swagger UI available at {}\n"
                + "----------------------------------------------------------", url);
    }
}

