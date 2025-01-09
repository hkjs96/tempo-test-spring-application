package com.tempo.order.service;

import com.tempo.order.client.ProductClient;
import com.tempo.order.domain.Order;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public Mono<OrderResponse> createOrder(OrderRequest request) {
        return productClient.getProduct(request.getProductId())
                .flatMap(product -> {
                    if (product.getStock() < request.getQuantity()) {
                        return Mono.error(new IllegalArgumentException("재고가 부족합니다."));
                    }

                    Order order = Order.builder()
                            .productId(request.getProductId())
                            .quantity(request.getQuantity())
                            .build();

                    return Mono.just(orderRepository.save(order))
                            .flatMap(savedOrder ->
                                    productClient.updateStock(request.getProductId(), request.getQuantity())
                                            .thenReturn(new OrderResponse(savedOrder))
                            );
                });
    }
}