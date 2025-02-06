package com.tempo.cart.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class CartItem implements Serializable {
    private String id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String productName;
    private Long price;

    @Builder
    public CartItem(String id, Long userId, Long productId, Integer quantity, String productName, Long price) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
