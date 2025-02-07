package com.tempo.payment.service;

import com.tempo.payment.client.OrderServiceClient;
import com.tempo.payment.client.PgClient;
import com.tempo.payment.domain.Payment;
import com.tempo.payment.domain.PaymentMethod;
import com.tempo.payment.domain.PaymentStatus;
import com.tempo.payment.dto.PaymentRequestDto;
import com.tempo.payment.dto.PaymentResponseDto;
import com.tempo.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PgClient pgClient;
    private final OrderServiceClient orderServiceClient;

    @Transactional
    public Mono<PaymentResponseDto> processPayment(PaymentRequestDto request) {
        log.info("결제 처리 시작 - orderId: {}, amount: {}", request.getOrderId(), request.getAmount());

        validatePaymentRequest(request);

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("결제 정보 저장 완료 - paymentId: {}", savedPayment.getId());

        return pgClient.requestPayment(request)
                .flatMap(pgResponse -> {
                    if (pgResponse.isSuccess()) {
                        savedPayment.setPaymentKey(pgResponse.getPaymentKey());
                        savedPayment.updateStatus(PaymentStatus.COMPLETED);
                        log.info("결제 성공 - paymentId: {}, paymentKey: {}",
                                savedPayment.getId(), pgResponse.getPaymentKey());

                        // 주문 서비스에 결제 완료 알림
                        return orderServiceClient.notifyPaymentComplete(
                                request.getOrderId(),
                                pgResponse.getPaymentKey(),
                                request.getPaymentMethod().name()
                        ).thenReturn(savedPayment);
                    } else {
                        savedPayment.fail(pgResponse.getErrorMessage());
                        log.error("결제 실패 - paymentId: {}, error: {}",
                                savedPayment.getId(), pgResponse.getErrorMessage());

                        // 주문 서비스에 결제 실패 알림
                        return orderServiceClient.notifyPaymentFailed(
                                request.getOrderId(),
                                pgResponse.getErrorMessage()
                        ).thenReturn(savedPayment);
                    }
                })
                .map(updatedPayment -> {
                    Payment finalPayment = paymentRepository.save(updatedPayment);
                    return new PaymentResponseDto(finalPayment);
                })
                .doOnError(error -> {
                    log.error("결제 처리 중 오류 발생", error);
                    savedPayment.fail("시스템 오류: " + error.getMessage());
                    paymentRepository.save(savedPayment);
                });
    }

    @Transactional
    public PaymentResponseDto cancelPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("완료된 결제만 취소할 수 있습니다.");
        }

        return pgClient.cancelPayment(payment.getPaymentKey())
                .map(pgResponse -> {
                    if (pgResponse.isSuccess()) {
                        payment.updateStatus(PaymentStatus.CANCELLED);
                        Payment cancelledPayment = paymentRepository.save(payment);

                        // 주문 서비스에 결제 취소 알림
                        orderServiceClient.notifyPaymentCancelled(
                                payment.getOrderId(),
                                payment.getPaymentKey(),
                                "사용자 요청에 의한 취소"
                        ).subscribe();

                        return new PaymentResponseDto(cancelledPayment);
                    } else {
                        throw new IllegalStateException("결제 취소 실패: " + pgResponse.getErrorMessage());
                    }
                })
                .block(); // 동기적 처리를 위해 block 사용
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));
        return new PaymentResponseDto(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제 정보를 찾을 수 없습니다."));
        return new PaymentResponseDto(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByStatus(String status) {
        PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        return paymentRepository.findByStatus(paymentStatus)
                .stream()
                .map(PaymentResponseDto::new)
                .collect(Collectors.toList());
    }

    private void validatePaymentRequest(PaymentRequestDto request) {
        if (request.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        if (request.getOrderId() == null) {
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }
        if (request.getPaymentMethod() == PaymentMethod.CARD) {
            validateCardInfo(request);
        }
    }

    private void validateCardInfo(PaymentRequestDto request) {
        if (request.getCardNumber() == null || request.getCardNumber().length() < 15) {
            throw new IllegalArgumentException("유효하지 않은 카드 번호입니다.");
        }
        if (request.getCardExpiry() == null || !request.getCardExpiry().matches("\\d{2}/\\d{2}")) {
            throw new IllegalArgumentException("유효하지 않은 카드 유효기간입니다.");
        }
        if (request.getCardCvc() == null || !request.getCardCvc().matches("\\d{3}")) {
            throw new IllegalArgumentException("유효하지 않은 CVC 번호입니다.");
        }
    }

    // 타임아웃된 결제 처리 (배치 작업용)
    @Transactional
    public void handleTimedOutPayments() {
        LocalDateTime timeoutCriteria = LocalDateTime.now().minusMinutes(30);
        List<Payment> timedOutPayments = paymentRepository.findTimedOutPayments(
                PaymentStatus.PENDING, timeoutCriteria);

        for (Payment payment : timedOutPayments) {
            payment.fail("결제 시간 초과");
            paymentRepository.save(payment);

            // 주문 서비스에 결제 실패 알림
            orderServiceClient.notifyPaymentFailed(payment.getOrderId(), "결제 시간 초과")
                    .subscribe();
        }
    }
}