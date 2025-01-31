package com.tempo.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder
    public Order(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.CREATED;
    }

    public OrderHistory updateStatus(OrderStatus newStatus, String message) {
        OrderStatus previousStatus = this.status;
        this.status = newStatus;

        return OrderHistory.builder()
                .order(this)
                .previousStatus(previousStatus)
                .newStatus(newStatus)
                .message(message)
                .build();
    }
}
