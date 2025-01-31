package com.tempo.order.service;

import com.tempo.order.client.ProductClient;
import com.tempo.order.domain.Order;
import com.tempo.order.domain.OrderHistory;
import com.tempo.order.domain.OrderStatus;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.repository.OrderHistoryRepository;
import com.tempo.order.repository.OrderRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository historyRepository;
    private final ProductClient productClient;
    private final Tracer tracer;

    @Transactional
    public Mono<OrderResponse> createOrder(OrderRequest request) {
        Span span = tracer.spanBuilder("createOrder").startSpan();

        try (var scope = span.makeCurrent()) {
            span.setAttribute("productId", request.getProductId());
            span.setAttribute("quantity", request.getQuantity());

            return productClient.getProduct(request.getProductId())
                    .flatMap(product -> {
                        if (product.getStock() < request.getQuantity()) {
                            span.setAttribute("error", true);
                            span.setAttribute("errorMessage", "재고 부족");
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
        } finally {
            span.end();
        }
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, String message) {
        Span span = tracer.spanBuilder("updateOrderStatus").startSpan();

        try (var scope = span.makeCurrent()) {
            span.setAttribute("orderId", orderId);
            span.setAttribute("newStatus", newStatus.name());

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

            OrderHistory history = order.updateStatus(newStatus, message);
            historyRepository.save(history);

            return new OrderResponse(order);
        } finally {
            span.end();
        }
    }

    @Transactional(readOnly = true)
    public List<OrderHistory> getOrderHistory(Long orderId) {
        Span span = tracer.spanBuilder("getOrderHistory").startSpan();

        try (var scope = span.makeCurrent()) {
            span.setAttribute("orderId", orderId);
            return historyRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        } finally {
            span.end();
        }
    }
}