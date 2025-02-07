package com.tempo.order.service;

import com.tempo.order.client.ProductClient;
import com.tempo.order.domain.Order;
import com.tempo.order.domain.OrderHistory;
import com.tempo.order.domain.OrderStatus;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.dto.OrderStatusChangeResponse;
import com.tempo.order.repository.OrderHistoryRepository;
import com.tempo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderHistoryRepository historyRepository;
    private final ProductClient productClient;

    @Transactional
    public Mono<OrderResponse> createOrder(OrderRequest request) {
        log.info("주문 생성 시작 - productId: {}, quantity: {}", request.getProductId(), request.getQuantity());

        // 입력값 검증
        if (request.getQuantity() <= 0) {
            log.error("잘못된 주문 수량 - quantity: {}", request.getQuantity());
            return Mono.error(new IllegalArgumentException("주문 수량은 0보다 커야 합니다."));
        }

        return productClient.getProduct(request.getProductId())
                .doOnNext(product -> log.info("상품 정보 조회 완료 - productId: {}, stock: {}",
                        product.getId(), product.getStock()))
                .flatMap(product -> {
                    // 재고 확인
                    if (product.getStock() < request.getQuantity()) {
                        log.error("재고 부족 - productId: {}, requested: {}, available: {}",
                                product.getId(), request.getQuantity(), product.getStock());
                        return Mono.error(new IllegalStateException(
                                String.format("재고가 부족합니다. (요청: %d, 재고: %d)",
                                        request.getQuantity(), product.getStock())
                        ));
                    }

                    try {
                        // 주문 엔티티 생성
                        Order order = Order.builder()
                                .productId(request.getProductId())
                                .quantity(request.getQuantity())
                                .build();

                        // 주문 저장
                        Order savedOrder = orderRepository.save(order);
                        log.info("주문 엔티티 저장 완료 - orderId: {}", savedOrder.getId());

                        // 주문 히스토리 생성 및 저장
                        OrderHistory history = OrderHistory.builder()
                                .order(savedOrder)
                                .previousStatus(null)
                                .newStatus(OrderStatus.CREATED)
                                .message("주문이 생성되었습니다.")
                                .build();
                        historyRepository.save(history);
                        log.info("주문 히스토리 저장 완료 - orderId: {}", savedOrder.getId());

                        // 재고 업데이트 및 응답 반환
                        return productClient.updateStock(request.getProductId(), request.getQuantity())
                                .doOnSuccess(v -> log.info("재고 업데이트 완료 - productId: {}, orderId: {}",
                                        request.getProductId(), savedOrder.getId()))
                                .doOnError(e -> log.error("재고 업데이트 실패 - productId: {}, orderId: {}",
                                        request.getProductId(), savedOrder.getId(), e))
                                .thenReturn(new OrderResponse(savedOrder));
                    } catch (Exception e) {
                        log.error("주문 생성 중 에러 발생", e);
                        return Mono.error(e);
                    }
                })
                .doOnSuccess(response -> log.info("주문 생성 완료 - orderId: {}", response.getId()))
                .doOnError(e -> log.error("주문 생성 실패", e));
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("주문 상태 업데이트 시작 - orderId: {}, newStatus: {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        order.updateStatus(newStatus);
        log.info("주문 상태 업데이트 완료 - orderId: {}, status: {}", orderId, newStatus);

        return new OrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderStatusChangeResponse> getOrderStatusChanges(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        return order.getStatusChanges().stream()
                .map(OrderStatusChangeResponse::new)
                .collect(Collectors.toList());
    }

    public List<OrderHistory> getOrderHistory(Long orderId) {
        return historyRepository.findByIdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(history -> {
                    // 명시적으로 Order 정보 로딩
                    history.getOrder().getId();  // 지연 로딩 강제 초기화
                    return history;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse completePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + orderId));

        order.updateStatus(OrderStatus.PAYMENT_COMPLETED);
        Order savedOrder = orderRepository.save(order);

        return new OrderResponse(savedOrder);
    }
}