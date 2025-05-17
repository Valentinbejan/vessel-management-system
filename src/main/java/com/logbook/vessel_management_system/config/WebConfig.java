// src/main/java/com/logbook/vessel_management_system/config/WebConfig.java
package com.logbook.vessel_management_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class.
 * 
 * Strategy Pattern: By implementing WebMvcConfigurer, this class plugs into Spring's
 * MVC configuration strategy system, allowing customization of specific aspects of
 * the web framework.
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Strategy Pattern: Implements the resource handling strategy from the
     * WebMvcConfigurer interface.
     */

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Handle favicon.ico requests
        registry.addResourceHandler("/favicon.ico")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(86400); // Cache for 1 day
    }

    /**
     * Strategy Pattern: Implements content negotiation strategy from the
     * WebMvcConfigurer interface. Configures how responses are generated
     * based on request properties like Accept headers.
     */

    @Override
    public void configureContentNegotiation(@NonNull ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }
}