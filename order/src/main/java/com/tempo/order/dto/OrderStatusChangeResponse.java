package com.tempo.order.dto;

import com.tempo.order.domain.OrderStatus;
import com.tempo.order.domain.OrderStatusChange;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OrderStatusChangeResponse {
    private final OrderStatus status;
    private final LocalDateTime createdAt;

    public OrderStatusChangeResponse(OrderStatusChange change) {
        this.status = change.getStatus();
        this.createdAt = change.getCreatedAt();
    }
}