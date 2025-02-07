package com.tempo.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentCancellationNotificationDto {
    private String paymentKey;
    private String cancelReason;
    private String cancelledTime;

    public PaymentCancellationNotificationDto(String paymentKey, String cancelReason, String cancelledTime) {
        this.paymentKey = paymentKey;
        this.cancelReason = cancelReason;
        this.cancelledTime = cancelledTime;
    }
}