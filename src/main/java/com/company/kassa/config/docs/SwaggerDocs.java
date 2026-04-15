package com.company.kassa.config.docs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerDocs {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Kassa System App")
                        .version("1.0")
                        .termsOfService("https://spring.io/terms")
                        .contact(new Contact()
                                .name("Bobur")
                                .email("bobur.work003@gmail.com")))
                .externalDocs(new ExternalDocumentation()
                        .description("Kassa System App API Documentation"))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Server")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public List<GroupedOpenApi> apis() {
        return List.of(
                GroupedOpenApi.builder()
                        .group("system-admin")
                        .pathsToMatch("/api/system-admin/**")
                        .build(),

                GroupedOpenApi.builder()
                        .group("yatt-admin")
                        .pathsToMatch("/api/yatt-admin/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("big-seller")
                        .pathsToMatch("/api/big-seller/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("small-seller")
                        .pathsToMatch("/api/small-seller/**")
                        .build(),
                GroupedOpenApi.builder()
                        .group("auth")
                        .pathsToMatch("/api/auth/**")
                        .build()
        );
    }
}
