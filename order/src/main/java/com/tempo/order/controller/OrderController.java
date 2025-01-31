package com.tempo.order.controller;

import com.tempo.order.domain.OrderHistory;
import com.tempo.order.dto.OrderHistoryResponse;
import com.tempo.order.dto.OrderRequest;
import com.tempo.order.dto.OrderResponse;
import com.tempo.order.dto.OrderStatusUpdateRequest;
import com.tempo.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Mono<ResponseEntity<OrderResponse>> createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request) {
        OrderResponse response = orderService.updateOrderStatus(
                orderId, request.getStatus(), request.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderHistoryResponse>> getOrderHistory(@PathVariable Long orderId) {
        List<OrderHistory> histories = orderService.getOrderHistory(orderId);
        List<OrderHistoryResponse> response = histories.stream()
                .map(OrderHistoryResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}