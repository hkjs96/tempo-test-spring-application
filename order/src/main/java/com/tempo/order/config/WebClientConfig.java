package com.tempo.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class WebClientConfig {
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081")
                .filter((request, next) -> {
                    log.info("Outgoing Request: {} {}", request.method(), request.url());
                    return next.exchange(request);
                })
                .build();
    }
}