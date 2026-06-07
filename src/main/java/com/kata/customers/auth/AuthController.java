package com.kata.customers.auth;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Endpoints para registro, inicio y cierre de sesion")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crea un usuario y retorna token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesion", description = "Valida credenciales y retorna token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Cerrar sesion",
        description = "Endpoint explicito de cierre de sesion para clientes JWT stateless"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sesion cerrada"),
        @ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
    })
    public ResponseEntity<LogoutResponse> logout(
        @RequestHeader("Authorization") String authorizationHeader
    ) {
        String token = extractBearerToken(authorizationHeader);
        return ResponseEntity.ok(authService.logout(token));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
        summary = "Consultar usuario autenticado",
        description = "Valida el JWT y retorna la informacion basica del usuario autenticado"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sesion valida"),
        @ApiResponse(responseCode = "401", description = "Token ausente, invalido o expirado")
    })
    public AuthMeResponse me(Authentication authentication) {
        return authService.me(authentication.getName());
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Encabezado Authorization invalido");
        }
        return authorizationHeader.substring(7);
    }
}
