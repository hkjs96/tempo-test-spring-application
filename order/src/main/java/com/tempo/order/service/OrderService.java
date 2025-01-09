package com.tempo.order.service;

import com.tempo.order.client.ProductClient;
import com.tempo.order.domain.Order;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.dto.ProductResponse;
import com.tempo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public OrderResponse createOrder(OrderRequest request) {
        // 상품 존재 확인 및 재고 체크
        ProductResponse product = productClient.getProduct(request.getProductId());
        if (product.getStock() < request.getQuantity()) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }

        // 주문 생성
        Order order = Order.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .build();

        // 재고 업데이트
        productClient.updateStock(request.getProductId(), request.getQuantity());

        Order savedOrder = orderRepository.save(order);
        return new OrderResponse(savedOrder);
    }
}