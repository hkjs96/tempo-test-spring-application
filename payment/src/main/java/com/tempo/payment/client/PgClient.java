package com.tempo.payment.client;

import com.tempo.payment.dto.PaymentRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class PgClient {
    private final WebClient webClient;

    public Mono<PgPaymentResponse> requestPayment(PaymentRequestDto request) {
        log.info("PG사 결제 요청 - orderId: {}", request.getOrderId());
        // 실제 환경에서는 외부 PG사 API를 호출
        // 테스트를 위해 모의 응답 생성
        return Mono.just(createMockResponse(request))
                .doOnSuccess(response -> log.info("PG사 결제 응답 성공 - success: {}", response.isSuccess()))
                .doOnError(error -> log.error("PG사 결제 요청 실패", error));
    }

    public Mono<PgPaymentResponse> cancelPayment(String paymentKey) {
        log.info("PG사 결제 취소 요청 - paymentKey: {}", paymentKey);
        // 테스트를 위한 모의 응답
        return Mono.just(PgPaymentResponse.builder()
                        .success(true)
                        .paymentKey(paymentKey)
                        .build())
                .doOnSuccess(response -> log.info("PG사 결제 취소 성공 - paymentKey: {}", paymentKey))
                .doOnError(error -> log.error("PG사 결제 취소 실패 - paymentKey: {}", paymentKey, error));
    }

    private PgPaymentResponse createMockResponse(PaymentRequestDto request) {
        // 카드번호가 "0000"으로 끝나면 실패 처리 (테스트용)
        boolean isSuccess = !request.getCardNumber().endsWith("0000");

        return PgPaymentResponse.builder()
                .success(isSuccess)
                .paymentKey(generatePaymentKey())
                .errorCode(isSuccess ? null : "CARD_DECLINED")
                .errorMessage(isSuccess ? null : "카드가 거절되었습니다.")
                .build();
    }

    private String generatePaymentKey() {
        return "PAY-" + System.currentTimeMillis();
    }

    @Getter
    @Builder
    public static class PgPaymentResponse {
        private final boolean success;
        private final String paymentKey;
        private final String errorCode;
        private final String errorMessage;
    }
}