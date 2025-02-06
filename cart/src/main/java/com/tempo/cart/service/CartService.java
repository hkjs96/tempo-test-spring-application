package com.tempo.cart.service;

import com.tempo.cart.domain.CartItem;
import com.tempo.cart.dto.CartItemRequest;
import com.tempo.cart.dto.CartItemResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_KEY_PREFIX = "cart:";

    private String getCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    public List<CartItemResponse> getCartItems(Long userId) {
        String cartKey = getCartKey(userId);
        List<Object> items = redisTemplate.opsForHash()
                .values(cartKey);

        return items.stream()
                .map(item -> new CartItemResponse((CartItem) item))
                .collect(Collectors.toList());
    }

    public CartItemResponse addCartItem(CartItemRequest request) {
        String cartKey = getCartKey(request.getUserId());
        String itemId = request.getProductId().toString();

        CartItem cartItem = CartItem.builder()
                .id(itemId)
                .userId(request.getUserId())
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .productName(request.getProductName())
                .price(request.getPrice())
                .build();

        redisTemplate.opsForHash().put(cartKey, itemId, cartItem);
        return new CartItemResponse(cartItem);
    }

    public void removeCartItem(Long userId, Long productId) {
        String cartKey = getCartKey(userId);
        String itemId = productId.toString();
        redisTemplate.opsForHash().delete(cartKey, itemId);
    }

    public void updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        String cartKey = getCartKey(userId);
        String itemId = productId.toString();

        CartItem existingItem = (CartItem) redisTemplate.opsForHash().get(cartKey, itemId);
        if (existingItem != null) {
            existingItem.updateQuantity(quantity);
            redisTemplate.opsForHash().put(cartKey, itemId, existingItem);
        }
    }
}