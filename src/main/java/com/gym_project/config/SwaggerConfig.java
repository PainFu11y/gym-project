package com.gym_project.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI gymProjectOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Project API")
                        .description("REST API documentation")
                        .version("1.0.0"))
           .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}