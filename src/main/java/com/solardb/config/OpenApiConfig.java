package com.solardb.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI solardbOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("SolarDB API")
                        .version("0.0.1")
                        .description("""
                                Ingest inverter readings into Postgres.

                                Auth: POST /api/v1/auth/token with body {"password":"..."} returns a JWT.
                                Use header Authorization: Bearer <token> on POST /api/v1/readings.

                                Set SOLARDB_AUTH_PASSWORD and SOLARDB_AUTH_JWT_SECRET in production.
                                """))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT from POST /api/v1/auth/token (scope readings:write).")));
    }
}
