// src/main/java/com/logbook/vessel_management_system/config/SwaggerConfig.java
package com.logbook.vessel_management_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vessel Management System API")
                        .description("RESTful API for managing maritime vessels and their owners. " +
                                "This API provides endpoints for CRUD operations on ships and owners, " +
                                "including managing many-to-many relationships between ships and owners.")
                        .version("1.0.0"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.vesselmanagementsystem.com")
                                .description("Production Server")
                ));
    }
}