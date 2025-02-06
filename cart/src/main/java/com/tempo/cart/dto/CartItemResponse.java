package com.tempo.cart.dto;

import com.tempo.cart.domain.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponse {
    private final String id;
    private final Long userId;
    private final Long productId;
    private final Integer quantity;
    private final String productName;
    private final Long price;
    private final Long totalPrice;

    public CartItemResponse(CartItem cartItem) {
        this.id = cartItem.getId();
        this.userId = cartItem.getUserId();
        this.productId = cartItem.getProductId();
        this.quantity = cartItem.getQuantity();
        this.productName = cartItem.getProductName();
        this.price = cartItem.getPrice();
        this.totalPrice = cartItem.getPrice() * cartItem.getQuantity();
    }
}
