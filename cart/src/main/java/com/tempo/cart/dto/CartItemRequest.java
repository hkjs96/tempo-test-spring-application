package com.tempo.cart.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CartItemRequest {
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String productName;
    private Long price;

    @Builder
    public CartItemRequest(Long userId, Long productId, Integer quantity, String productName, Long price) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }
}