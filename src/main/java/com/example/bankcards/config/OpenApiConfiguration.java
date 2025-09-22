package com.example.bankcards.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
    @Bean
    public OpenAPI monitorSensorsOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("Bank rest")
                        .description("Application for cards transactions and transfers")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("README for application:")
                        .url(""));


    }
}
