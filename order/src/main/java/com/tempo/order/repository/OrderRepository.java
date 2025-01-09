package com.tempo.order.repository;

import com.tempo.order.domain.Order;
import com.tempo.order.domain.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 주문 ID로 주문 엔티티 조회
    Optional<Order> findById(Long id);

    // 상품 ID로 주문 목록 조회
    List<Order> findByProductId(Long productId);

    // 주문 상태로 주문 목록 조회
    List<Order> findByStatus(OrderStatus status);

    // 상품 ID와 주문 상태로 주문 목록 조회
    List<Order> findByProductIdAndStatus(Long productId, OrderStatus status);
}