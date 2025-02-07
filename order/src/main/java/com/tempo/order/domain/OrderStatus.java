package com.tempo.order.domain;

public enum OrderStatus {
    CREATED,           // 주문 생성
    PAYMENT_PENDING,   // 결제 진행 중
    PAYMENT_COMPLETED, // 결제 완료
    PAYMENT_FAILED,    // 결제 실패
    CONFIRMED,         // 주문 확정
    CANCELLED,         // 주문 취소
    COMPLETED         // 주문 완료
}