package com.tempo.payment.repository;

import com.tempo.payment.domain.Payment;
import com.tempo.payment.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // 주문 ID로 결제 정보 조회
    Optional<Payment> findByOrderId(Long orderId);

    // 결제 상태별 결제 목록 조회
    List<Payment> findByStatus(PaymentStatus status);

    // 특정 기간 내의 결제 목록 조회
    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 주문 ID로 완료된 결제 조회
    Optional<Payment> findByOrderIdAndStatus(Long orderId, PaymentStatus status);

    // 특정 상태의 결제 건수 조회
    long countByStatus(PaymentStatus status);

    // 결제 키로 결제 정보 조회
    Optional<Payment> findByPaymentKey(String paymentKey);

    // 특정 날짜 이후의 실패한 결제 목록 조회
    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt >= :date")
    List<Payment> findFailedPaymentsAfter(
            @Param("status") PaymentStatus status,
            @Param("date") LocalDateTime date);

    // 특정 기간 내 결제 상태별 건수 조회
    @Query("SELECT p.status, COUNT(p) FROM Payment p " +
            "WHERE p.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY p.status")
    List<Object[]> countByStatusAndDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 미완료 상태인 오래된 결제 조회 (timeout 처리용)
    @Query("SELECT p FROM Payment p " +
            "WHERE p.status = :status " +
            "AND p.createdAt < :timeoutDate")
    List<Payment> findTimedOutPayments(
            @Param("status") PaymentStatus status,
            @Param("timeoutDate") LocalDateTime timeoutDate);
}