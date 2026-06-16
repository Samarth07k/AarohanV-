package com.artistlink.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI artistLinkOpenApi() {
        return new OpenAPI().info(new Info()
                .title("ArtistLink API")
                .version("0.1.0")
                .description("Discovery and booking platform for performers and venues"));
    }
}
