package com.tempo.cart.service;

import com.tempo.cart.domain.CartItem;
import com.tempo.cart.domain.CartItemCache;
import com.tempo.cart.dto.CartItemRequest;
import com.tempo.cart.dto.CartItemResponse;
import com.tempo.cart.repository.CartItemCacheRepository;
import com.tempo.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final CartItemCacheRepository cacheRepository;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(Long userId) {
        log.info("장바구니 조회 시작 - userId: {}", userId);

        // 1. 캐시에서 먼저 조회
        List<CartItemCache> cachedItems = cacheRepository.findByUserId(userId);
        if (!cachedItems.isEmpty()) {
            log.info("캐시에서 장바구니 데이터 조회 성공 - userId: {}", userId);
            return cachedItems.stream()
                    .map(CartItemResponse::fromCache)
                    .collect(Collectors.toList());
        }

        // 2. DB에서 조회하고 캐시 갱신
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        items.forEach(item -> cacheRepository.save(CartItemCache.fromEntity(item)));
        log.info("DB에서 장바구니 데이터 조회 및 캐시 갱신 완료 - userId: {}", userId);

        return items.stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartItemResponse addCartItem(CartItemRequest request) {
        log.info("장바구니 상품 추가 시작 - userId: {}, productId: {}",
                request.getUserId(), request.getProductId());

        // 1. 기존 상품 확인
        CartItem existingItem = cartItemRepository
                .findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .orElse(null);

        CartItem savedItem;
        if (existingItem != null) {
            // 기존 상품 수량 업데이트
            existingItem.updateQuantity(existingItem.getQuantity() + request.getQuantity());
            savedItem = cartItemRepository.save(existingItem);
            log.info("기존 상품 수량 업데이트 완료 - itemId: {}", savedItem.getId());
        } else {
            // 새 상품 추가
            CartItem newItem = request.toEntity();
            savedItem = cartItemRepository.save(newItem);
            log.info("새 상품 추가 완료 - itemId: {}", savedItem.getId());
        }

        // 캐시 업데이트
        CartItemCache cacheItem = CartItemCache.fromEntity(savedItem);
        cacheRepository.save(cacheItem);
        log.info("캐시 업데이트 완료 - userId: {}, productId: {}",
                request.getUserId(), request.getProductId());

        return CartItemResponse.fromEntity(savedItem);
    }

    @Transactional
    public void removeCartItem(Long userId, Long productId) {
        log.info("장바구니 상품 제거 시작 - userId: {}, productId: {}", userId, productId);

        // DB에서 삭제
        cartItemRepository.deleteByUserIdAndProductId(userId, productId);

        // 캐시에서 삭제
        cacheRepository.deleteByUserIdAndProductId(userId, productId);

        log.info("장바구니 상품 제거 완료 - userId: {}, productId: {}", userId, productId);
    }

    @Transactional
    public CartItemResponse updateCartItemQuantity(Long userId, Long productId, Integer quantity) {
        log.info("장바구니 상품 수량 업데이트 시작 - userId: {}, productId: {}, quantity: {}",
                userId, productId, quantity);

        CartItem item = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));

        item.updateQuantity(quantity);
        CartItem savedItem = cartItemRepository.save(item);

        // 캐시 업데이트
        CartItemCache cacheItem = CartItemCache.fromEntity(savedItem);
        cacheRepository.save(cacheItem);

        log.info("장바구니 상품 수량 업데이트 완료 - itemId: {}", savedItem.getId());
        return CartItemResponse.fromEntity(savedItem);
    }
}