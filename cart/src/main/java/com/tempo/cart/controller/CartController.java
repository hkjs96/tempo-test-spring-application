package com.tempo.cart.controller;

import com.tempo.cart.dto.CartItemRequest;
import com.tempo.cart.dto.CartItemResponse;
import com.tempo.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addCartItem(@RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addCartItem(request));
    }

    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok().build();
    }
}