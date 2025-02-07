package com.tempo.payment.controller;

import com.tempo.payment.dto.PaymentRequestDto;
import com.tempo.payment.dto.PaymentResponseDto;
import com.tempo.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public Mono<ResponseEntity<PaymentResponseDto>> processPayment(
            @RequestBody PaymentRequestDto request) {
        log.info("결제 요청 수신 - orderId: {}, amount: {}",
                request.getOrderId(), request.getAmount());
        return paymentService.processPayment(request)
                .map(response -> {
                    log.info("결제 처리 완료 - paymentId: {}, status: {}",
                            response.getId(), response.getStatus());
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })
                .doOnError(error -> log.error("결제 처리 실패", error));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDto> getPayment(@PathVariable Long paymentId) {
        log.info("결제 정보 조회 요청 - paymentId: {}", paymentId);
        PaymentResponseDto response = paymentService.getPayment(paymentId);
        log.info("결제 정보 조회 완료 - paymentId: {}", paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("주문별 결제 정보 조회 요청 - orderId: {}", orderId);
        PaymentResponseDto response = paymentService.getPaymentByOrderId(orderId);
        log.info("주문별 결제 정보 조회 완료 - orderId: {}", orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponseDto> cancelPayment(
            @PathVariable Long paymentId,
            @RequestParam(required = false) String reason) {
        log.info("결제 취소 요청 - paymentId: {}, reason: {}", paymentId, reason);
        PaymentResponseDto response = paymentService.cancelPayment(paymentId);
        log.info("결제 취소 완료 - paymentId: {}", paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByStatus(
            @PathVariable String status) {
        log.info("상태별 결제 목록 조회 요청 - status: {}", status);
        List<PaymentResponseDto> responses = paymentService.getPaymentsByStatus(status);
        log.info("상태별 결제 목록 조회 완료 - status: {}, count: {}",
                status, responses.size());
        return ResponseEntity.ok(responses);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        log.error("잘못된 요청 처리", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}