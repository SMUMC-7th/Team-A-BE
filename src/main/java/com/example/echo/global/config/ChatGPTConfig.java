package com.example.echo.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ChatGPTConfig {

    @Value("${openai.secret-key}")
    private String secretKey;

    @Getter
    @Value("${openai.api.url}")
    private String apiUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + secretKey)
                .build();
    }
}