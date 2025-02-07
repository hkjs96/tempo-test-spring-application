package com.tempo.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCompletionNotificationDto {
    private String paymentKey;
    private String paymentMethod;
    private String transactionTime;

    public PaymentCompletionNotificationDto(String paymentKey, String paymentMethod, String transactionTime) {
        this.paymentKey = paymentKey;
        this.paymentMethod = paymentMethod;
        this.transactionTime = transactionTime;
    }
}