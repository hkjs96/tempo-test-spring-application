package com.tempo.cart.domain;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@RedisHash(value = "cart_item", timeToLive = 86400) // 24시간 유효
public class CartItemCache implements Serializable {
    @Id
    private String id;
    private Long itemId;  // JPA 엔티티의 ID
    private Long userId;
    private Long productId;
    private Integer quantity;
    private String productName;
    private Long price;

    @Builder
    public CartItemCache(Long itemId, Long userId, Long productId, Integer quantity, String productName, Long price) {
        this.id = generateId(userId, productId);
        this.itemId = itemId;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.productName = productName;
        this.price = price;
    }

    public static String generateId(Long userId, Long productId) {
        return String.format("%d:%d", userId, productId);
    }

    public void updateQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public CartItem toEntity() {
        return CartItem.builder()
                .userId(userId)
                .productId(productId)
                .quantity(quantity)
                .productName(productName)
                .price(price)
                .build();
    }

    public static CartItemCache fromEntity(CartItem cartItem) {
        return CartItemCache.builder()
                .itemId(cartItem.getId())
                .userId(cartItem.getUserId())
                .productId(cartItem.getProductId())
                .quantity(cartItem.getQuantity())
                .productName(cartItem.getProductName())
                .price(cartItem.getPrice())
                .build();
    }
}

