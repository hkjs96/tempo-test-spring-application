package com.tempo.order.client;

import com.tempo.order.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class ProductClient {
    private final WebClient webClient;

    public ProductResponse getProduct(Long id) {
        return webClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .block();
    }

    public void updateStock(Long id, Integer quantity) {
        webClient.put()
                .uri("/api/products/{id}/stock?quantity={quantity}", id, quantity)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }
}