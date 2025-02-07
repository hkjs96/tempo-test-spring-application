package com.tempo.payment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentFailureNotificationDto {
    private String reason;

    public PaymentFailureNotificationDto(String reason) {
        this.reason = reason;
    }
}
