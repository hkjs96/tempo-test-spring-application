package com.tempo.order.service;

import com.tempo.order.client.ProductClient;
import com.tempo.order.domain.Order;
import com.tempo.order.domain.OrderHistory;
import com.tempo.order.domain.OrderStatus;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.repository.OrderHistoryRepository;
import com.tempo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository historyRepository;
    private final ProductClient productClient;

    @Transactional
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

                    Order savedOrder = orderRepository.save(order);
                    OrderHistory history = savedOrder.updateStatus(OrderStatus.CREATED, "주문이 생성되었습니다.");
                    historyRepository.save(history);

                    return productClient.updateStock(request.getProductId(), request.getQuantity())
                            .then(Mono.just(new OrderResponse(savedOrder)));
                });
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, String message) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        OrderHistory history = order.updateStatus(newStatus, message);
        historyRepository.save(history);

        return new OrderResponse(order);
    }

    public List<OrderHistory> getOrderHistory(Long orderId) {
        return historyRepository.findByOrderIdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(history -> {
                    // 명시적으로 Order 정보 로딩
                    history.getOrder().getId();  // 지연 로딩 강제 초기화
                    return history;
                })
                .collect(Collectors.toList());
    }
}