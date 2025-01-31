package com.tempo.order.client;

import com.tempo.order.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductClient {
    private final WebClient webClient;

    public Mono<ProductResponse> getProduct(Long id) {
        log.info("상품 조회 요청 - productId: {}", id);
        return webClient.get()
                .uri("/api/products/{id}", id)
                .retrieve()
                .bodyToMono(ProductResponse.class)
                .doOnSuccess(response -> log.info("상품 조회 완료 - productId: {}, name: {}", id, response.getName()))
                .doOnError(error -> log.error("상품 조회 실패 - productId: {}, error: {}", id, error.getMessage()));
    }

    public Mono<Void> updateStock(Long id, Integer quantity) {
        log.info("재고 업데이트 요청 - productId: {}, quantity: {}", id, quantity);
        return webClient.put()
                .uri("/api/products/{id}/stock?quantity={quantity}", id, quantity)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("재고 업데이트 완료 - productId: {}, quantity: {}", id, quantity))
                .doOnError(error -> log.error("재고 업데이트 실패 - productId: {}, quantity: {}, error: {}",
                        id, quantity, error.getMessage()));
    }
}