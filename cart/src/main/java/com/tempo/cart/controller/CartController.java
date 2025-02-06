package com.tempo.cart.controller;

import com.tempo.cart.dto.CartItemRequest;
import com.tempo.cart.dto.CartItemResponse;
import com.tempo.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
        log.info("장바구니 조회 요청 - userId: {}", userId);
        List<CartItemResponse> cartItems = cartService.getCartItems(userId);
        log.info("장바구니 조회 완료 - userId: {}, itemCount: {}", userId, cartItems.size());
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addCartItem(@RequestBody CartItemRequest request) {
        log.info("장바구니 상품 추가 요청 - userId: {}, productId: {}",
                request.getUserId(), request.getProductId());
        CartItemResponse response = cartService.addCartItem(request);
        log.info("장바구니 상품 추가 완료 - itemId: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{userId}/products/{productId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        log.info("장바구니 상품 제거 요청 - userId: {}, productId: {}", userId, productId);
        cartService.removeCartItem(userId, productId);
        log.info("장바구니 상품 제거 완료 - userId: {}, productId: {}", userId, productId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/products/{productId}")
    public ResponseEntity<CartItemResponse> updateCartItemQuantity(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {
        log.info("장바구니 상품 수량 업데이트 요청 - userId: {}, productId: {}, quantity: {}",
                userId, productId, quantity);
        CartItemResponse response = cartService.updateCartItemQuantity(userId, productId, quantity);
        log.info("장바구니 상품 수량 업데이트 완료 - itemId: {}", response.getId());
        return ResponseEntity.ok(response);
    }
}