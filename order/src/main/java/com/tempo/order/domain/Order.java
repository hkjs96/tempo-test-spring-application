package com.tempo.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "order_status_changes",
            joinColumns = @JoinColumn(name = "order_id"))
    @OrderBy("createdAt DESC")
    private List<OrderStatusChange> statusChanges = new ArrayList<>();

    @Builder
    public Order(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.CREATED;
        addStatusChange(OrderStatus.CREATED);
    }

    public void updateStatus(OrderStatus newStatus) {
        this.status = newStatus;
        addStatusChange(newStatus);
    }

    private void addStatusChange(OrderStatus status) {
        this.statusChanges.add(new OrderStatusChange(status));
    }
}
