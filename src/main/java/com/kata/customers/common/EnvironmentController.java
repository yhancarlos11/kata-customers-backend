package com.kata.customers.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
@Tag(name = "Ambiente", description = "Informacion de perfil y configuracion activa")
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
    @Operation(
        summary = "Consultar ambiente activo",
        description = "Retorna nombre de aplicacion, puerto, mensaje y perfil activo"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Informacion de ambiente obtenida")
    })
    public Map<String, String> environmentInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("application", appName);
        response.put("port", serverPort);
        response.put("message", environmentMessage);

        String[] profiles = environment.getActiveProfiles();
        response.put("activeProfile", profiles.length > 0 ? profiles[0] : "default");
        return response;
    }

    @GetMapping("/health")
    @Operation(
        summary = "Health check de la API",
        description = "Retorna el estado de salud para monitoreo de infraestructura"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Servicio disponible")
    })
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        return response;
    }
}
