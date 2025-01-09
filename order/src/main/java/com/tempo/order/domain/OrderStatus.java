package com.tempo.order.domain;

public enum OrderStatus {
    CREATED,      // 주문 생성
    CONFIRMED,    // 주문 확정
    CANCELLED,    // 주문 취소
    COMPLETED     // 주문 완료
}