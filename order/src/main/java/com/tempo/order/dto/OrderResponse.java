package com.tempo.order.dto;

import com.tempo.order.domain.Order;
import com.tempo.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderResponse {
    private final Long id;
    private final Long productId;
    private final Integer quantity;
    private final OrderStatus status;

    @Builder
    public OrderResponse(Order order) {
        this.id = order.getId();
        this.productId = order.getProductId();
        this.quantity = order.getQuantity();
        this.status = order.getStatus();
    }
}
