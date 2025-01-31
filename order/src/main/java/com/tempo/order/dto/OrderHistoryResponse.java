package com.tempo.order.dto;

import com.tempo.order.domain.OrderHistory;
import com.tempo.order.domain.OrderStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderHistoryResponse {
    private final Long id;
    private final Long orderId;
    private final OrderStatus previousStatus;
    private final OrderStatus newStatus;
    private final String message;
    private final LocalDateTime createdAt;

    public OrderHistoryResponse(OrderHistory history) {
        this.id = history.getId();
        this.orderId = history.getOrderId();
        this.previousStatus = history.getPreviousStatus();
        this.newStatus = history.getNewStatus();
        this.message = history.getMessage();
        this.createdAt = history.getCreatedAt();
    }
}