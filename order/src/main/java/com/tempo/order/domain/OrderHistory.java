package com.tempo.order.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore  // 추가
    private Order order;

    @Enumerated(EnumType.STRING)
    private OrderStatus previousStatus;

    @Enumerated(EnumType.STRING)
    private OrderStatus newStatus;

    private String message;
    private LocalDateTime createdAt;

    @Builder
    public OrderHistory(Order order, OrderStatus previousStatus, OrderStatus newStatus, String message) {
        this.order = order;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    // OrderResponse를 위한 새로운 메서드 추가
    public Long getOrderId() {
        return order != null ? order.getId() : null;
    }
}
