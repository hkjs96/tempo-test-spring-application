package com.tempo.order.dto;

import com.tempo.order.domain.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderStatusUpdateRequest {
    private OrderStatus status;
    private String message;
}