package com.tempo.payment.dto;

import com.tempo.payment.domain.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentStatusRequestDto {
    private PaymentStatus status;
    private String reason;
}