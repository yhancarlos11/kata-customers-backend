package com.kata.customers.common;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
public class EnvironmentController {

    @Value("${spring.application.name}")
    private String appName;

    @Value("${server.port}")
    private String serverPort;

    @Value("${app.environment.message}")
    private String environmentMessage;

    private final Environment environment;

    public EnvironmentController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/environment")
    public Map<String, String> environmentInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("application", appName);
        response.put("port", serverPort);
        response.put("message", environmentMessage);

        String[] profiles = environment.getActiveProfiles();
        response.put("activeProfile", profiles.length > 0 ? profiles[0] : "default");
        return response;
    }
}
