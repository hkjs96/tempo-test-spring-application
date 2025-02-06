package com.tempo.cart.dto;

import com.tempo.cart.domain.CartItem;
import com.tempo.cart.domain.CartItemCache;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CartItemResponse {
    private final Long id;
    private final Long userId;
    private final Long productId;
    private final Integer quantity;
    private final String productName;
    private final Long price;
    private final Long totalPrice;

    @Builder
    public CartItemResponse(Long id, Long userId, Long productId, Integer quantity,
                            String productName, Long price) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
        this.totalPrice = price * quantity;
    }

    public static CartItemResponse fromEntity(CartItem cartItem) {
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .userId(cartItem.getUserId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .productName(cartItem.getProductName())
                .price(cartItem.getPrice())
                .build();
    }

    public static CartItemResponse fromCache(CartItemCache cache) {
        return CartItemResponse.builder()
                .id(cache.getItemId())
                .userId(cache.getUserId())
                .productId(cache.getProductId())
                .quantity(cache.getQuantity())
                .productName(cache.getProductName())
                .price(cache.getPrice())
                .build();
    }
}
