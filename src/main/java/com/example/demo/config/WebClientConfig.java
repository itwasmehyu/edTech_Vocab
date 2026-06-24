package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

    @Value("${rapidapi.key}")
    private String apiKey;

    @Bean
    public WebClient wordsApiClient() {
        return WebClient.builder()
                .baseUrl("https://wordsapiv1.p.rapidapi.com")
                .defaultHeader("X-RapidAPI-Key", apiKey)
                .defaultHeader("X-RapidAPI-Host", "wordsapiv1.p.rapidapi.com")
                .defaultHeader("Accept", "application/json")
                .build();
    }
}