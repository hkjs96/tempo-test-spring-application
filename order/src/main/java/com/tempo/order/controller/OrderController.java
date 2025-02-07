package com.tempo.order.controller;

import com.tempo.order.domain.OrderHistory;
import com.tempo.order.dto.OrderHistoryResponse;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.dto.OrderStatusUpdateRequest;
import com.tempo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
        log.info("주문 생성 요청 수신 - productId: {}, quantity: {}",
                request.getProductId(), request.getQuantity());
        return orderService.createOrder(request)
                .map(response -> {
                    log.info("주문 생성 완료 - orderId: {}", response.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .doOnError(error -> log.error("주문 생성 실패 - error: {}", error.getMessage()));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request) {
        log.info("주문 상태 변경 요청 - orderId: {}, newStatus: {}", orderId, request.getStatus());
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus()));
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@PathVariable Long orderId) {
        List<OrderHistory> histories = orderService.getOrderHistory(orderId);
        List<OrderHistoryResponse> response = histories.stream()
                .map(OrderHistoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{orderId}/payment/complete")
    public ResponseEntity<OrderResponse> completePayment(@PathVariable Long orderId) {
        log.info("주문 결제 완료 처리 요청 - orderId: {}", orderId);
        OrderResponse response = orderService.completePayment(orderId);
        log.info("주문 결제 완료 처리 완료 - orderId: {}", orderId);
        return ResponseEntity.ok(response);
    }
}