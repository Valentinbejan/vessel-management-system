// src/main/java/com/logbook/vessel_management_system/controller/HomeController.java
package com.logbook.vessel_management_system.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@Hidden // Exclude from Swagger documentation
public class HomeController {

    @GetMapping("/")
    public RedirectView home() {
        // Redirect root path to Swagger UI
        return new RedirectView("/swagger-ui.html");
    }

    @GetMapping("/docs")
    public RedirectView docs() {
        // Alternative endpoint for documentation
        return new RedirectView("/swagger-ui.html");
    }
}