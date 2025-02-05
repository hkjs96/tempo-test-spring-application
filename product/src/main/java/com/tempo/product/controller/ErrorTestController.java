package com.tempo.product.controller;

import com.tempo.product.exception.ProductNotFoundException;
import com.tempo.product.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/v1/error-test")
public class ErrorTestController {

    private final WebClient webClient;

    public ErrorTestController(WebClient webClient) {
        this.webClient = webClient;
    }

    // 1. 서비스 내부 오류 (HTTP 500)
    @GetMapping("/internal-error")
    public Mono<ResponseEntity<String>> triggerInternalError() {
        return Mono.error(new RuntimeException("의도적으로 발생시킨 서버 내부 오류"));
    }

    // 2. 서비스 간 통신 시 타임아웃 (HTTP 504)
    @GetMapping("/timeout")
    public Mono<ResponseEntity<String>> triggerTimeout() {
        return Mono.delay(Duration.ofSeconds(10))
                .then(Mono.error(new TimeoutException("서비스 타임아웃 발생")));
    }

    // 3. 연쇄 장애 테스트 (Service A -> B -> C)
    @GetMapping("/cascade-failure")
    public Mono<ResponseEntity<String>> triggerCascadeFailure() {
        return webClient.get()
                .uri("http://localhost:8082/api/v1/orders/error")
                .retrieve()
                .bodyToMono(String.class)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.error(new ServiceException("연쇄 장애 테스트 중 오류 발생", e)));
    }

    // 4. 데이터베이스 조회 실패 (HTTP 503)
    @GetMapping("/db-error")
    public Mono<ResponseEntity<String>> triggerDatabaseError() {
        return Mono.error(new ProductNotFoundException("데이터베이스 연결 오류로 인한 상품 조회 실패"));
    }

    // 5. 메모리 부족 오류 (HTTP 503)
    @GetMapping("/out-of-memory")
    public Mono<ResponseEntity<String>> triggerOutOfMemory() {
        try {
            // 대량의 데이터를 생성하여 메모리 부족 상황 유발
            byte[] data = new byte[Integer.MAX_VALUE];
            return Mono.just(ResponseEntity.ok("메모리 부족 오류가 발생하지 않았습니다"));
        } catch (OutOfMemoryError e) {
            return Mono.error(new ServiceException("메모리 부족 오류 발생", e));
        }
    }
}