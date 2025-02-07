package com.tempo.payment.domain;

public enum PaymentStatus {
    READY,      // 결제 준비
    PENDING,    // 결제 진행 중
    COMPLETED,  // 결제 완료
    FAILED,     // 결제 실패
    CANCELLED   // 결제 취소
}
