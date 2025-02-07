package com.tempo.payment.client;

import com.tempo.payment.dto.PaymentCancellationNotificationDto;
import com.tempo.payment.dto.PaymentCompletionNotificationDto;
import com.tempo.payment.dto.PaymentFailureNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final WebClient webClient;
    private static final String ORDER_SERVICE_URL = "http://localhost:8082";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public Mono<Void> notifyPaymentComplete(Long orderId, String paymentKey, String paymentMethod) {
        log.info("주문 서비스에 결제 완료 알림 - orderId: {}", orderId);
        PaymentCompletionNotificationDto notification = new PaymentCompletionNotificationDto(
                paymentKey,
                paymentMethod,
                LocalDateTime.now().format(DATE_FORMATTER)
        );

        return webClient.put()
                .uri(ORDER_SERVICE_URL + "/api/orders/{orderId}/payment/complete", orderId)
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("결제 완료 알림 성공 - orderId: {}", orderId))
                .doOnError(e -> log.error("결제 완료 알림 실패 - orderId: {}", orderId, e))
                .onErrorResume(e -> {
                    log.warn("결제 완료 알림 실패. 재시도 큐에 추가 필요 - orderId: {}", orderId);
                    return Mono.empty();
                });
    }

    public Mono<Void> notifyPaymentFailed(Long orderId, String reason) {
        log.info("주문 서비스에 결제 실패 알림 - orderId: {}, reason: {}", orderId, reason);
        PaymentFailureNotificationDto notification = new PaymentFailureNotificationDto(reason);

        return webClient.put()
                .uri(ORDER_SERVICE_URL + "/api/orders/{orderId}/payment/fail", orderId)
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("결제 실패 알림 성공 - orderId: {}", orderId))
                .doOnError(e -> log.error("결제 실패 알림 실패 - orderId: {}", orderId, e))
                .onErrorResume(e -> {
                    log.warn("결제 실패 알림 실패. 재시도 큐에 추가 필요 - orderId: {}", orderId);
                    return Mono.empty();
                });
    }

    public Mono<Void> notifyPaymentCancelled(Long orderId, String paymentKey, String reason) {
        log.info("주문 서비스에 결제 취소 알림 - orderId: {}", orderId);
        PaymentCancellationNotificationDto notification = new PaymentCancellationNotificationDto(
                paymentKey,
                reason,
                LocalDateTime.now().format(DATE_FORMATTER)
        );

        return webClient.put()
                .uri(ORDER_SERVICE_URL + "/api/orders/{orderId}/payment/cancel", orderId)
                .bodyValue(notification)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("결제 취소 알림 성공 - orderId: {}", orderId))
                .doOnError(e -> log.error("결제 취소 알림 실패 - orderId: {}", orderId, e))
                .onErrorResume(e -> {
                    log.warn("결제 취소 알림 실패. 재시도 큐에 추가 필요 - orderId: {}", orderId);
                    return Mono.empty();
                });
    }

    public Mono<Boolean> validateOrder(Long orderId) {
        log.info("주문 유효성 검증 요청 - orderId: {}", orderId);
        return webClient.get()
                .uri(ORDER_SERVICE_URL + "/api/orders/{orderId}/validate", orderId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .doOnSuccess(valid -> log.info("주문 유효성 검증 완료 - orderId: {}, valid: {}", orderId, valid))
                .doOnError(e -> log.error("주문 유효성 검증 실패 - orderId: {}", orderId, e))
                .onErrorReturn(false);
    }
}