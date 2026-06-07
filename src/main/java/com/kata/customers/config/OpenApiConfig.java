package com.kata.customers.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Customers API",
        version = "v1",
        description = "API para autenticacion JWT y gestion de clientes en ambientes DEV/PROD.",
        contact = @Contact(name = "Kata Team", email = "devnull@example.com"),
        license = @License(name = "Uso interno de practica")
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Backend DEV"),
        @Server(url = "http://localhost:9090", description = "Backend PROD simulado")
    },
    tags = {
        @Tag(name = "Autenticacion", description = "Registro, login, logout y validacion de sesion"),
        @Tag(name = "Clientes", description = "CRUD de clientes"),
        @Tag(name = "Productos", description = "CRUD de productos asociados a un cliente"),
        @Tag(name = "Ambiente", description = "Informacion de perfil y health checks")
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
