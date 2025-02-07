package com.tempo.payment.dto;

import com.tempo.payment.domain.PaymentMethod;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class PaymentRequestDto {
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String cardNumber;
    private String cardExpiry;
    private String cardCvc;

    @Builder
    public PaymentRequestDto(Long orderId, BigDecimal amount, PaymentMethod paymentMethod,
                             String cardNumber, String cardExpiry, String cardCvc) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.cardNumber = cardNumber;
        this.cardExpiry = cardExpiry;
        this.cardCvc = cardCvc;
    }
}