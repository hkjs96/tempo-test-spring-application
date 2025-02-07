package com.tempo.payment.dto;

import com.tempo.payment.domain.Payment;
import com.tempo.payment.domain.PaymentMethod;
import com.tempo.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentResponseDto {
    private final Long id;
    private final Long orderId;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final PaymentMethod paymentMethod;
    private final LocalDateTime paidAt;
    private final String paymentKey;

    @Builder
    public PaymentResponseDto(Payment payment) {
        this.id = payment.getId();
        this.orderId = payment.getOrderId();
        this.amount = payment.getAmount();
        this.status = payment.getStatus();
        this.paymentMethod = payment.getPaymentMethod();
        this.paidAt = payment.getPaidAt();
        this.paymentKey = payment.getPaymentKey();
    }
}