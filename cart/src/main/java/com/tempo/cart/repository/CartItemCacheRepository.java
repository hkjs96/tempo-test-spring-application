package com.tempo.cart.repository;

import com.tempo.cart.domain.CartItemCache;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemCacheRepository extends CrudRepository<CartItemCache, String> {
    List<CartItemCache> findByUserId(Long userId);
    void deleteByUserIdAndProductId(Long userId, Long productId);
}
