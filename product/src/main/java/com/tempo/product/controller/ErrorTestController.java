package com.tempo.product.controller;

import com.tempo.product.exception.ProductNotFoundException;
import com.tempo.product.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/v1/error-test")
public class ErrorTestController {

    private final RestTemplate restTemplate;

    public ErrorTestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 1. 서비스 내부 오류 (HTTP 500)
    @GetMapping("/internal-error")
    public ResponseEntity<String> triggerInternalError() {
        throw new RuntimeException("의도적으로 발생시킨 서버 내부 오류");
    }

    // 2. 서비스 간 통신 시 타임아웃 (HTTP 504)
    @GetMapping("/timeout")
    public ResponseEntity<String> triggerTimeout() throws TimeoutException {
        try {
            Thread.sleep(10000); // 10초 대기
            return ResponseEntity.ok("타임아웃이 발생하지 않았습니다");
        } catch (InterruptedException e) {
            throw new TimeoutException("서비스 타임아웃 발생");
        }
    }

    // 3. 연쇄 장애 테스트 (Service A -> B -> C)
    @GetMapping("/cascade-failure")
    public ResponseEntity<String> triggerCascadeFailure() {
        try {
            // Order 서비스 호출
            restTemplate.getForObject("http://localhost:8082/api/v1/orders/error", String.class);
            return ResponseEntity.ok("성공");
        } catch (Exception e) {
            throw new ServiceException("연쇄 장애 테스트 중 오류 발생", e);
        }
    }

    // 4. 데이터베이스 조회 실패 (HTTP 503)
    @GetMapping("/db-error")
    public ResponseEntity<String> triggerDatabaseError() {
        throw new ProductNotFoundException("데이터베이스 연결 오류로 인한 상품 조회 실패");
    }

    // 5. 메모리 부족 오류 (HTTP 503)
    @GetMapping("/out-of-memory")
    public ResponseEntity<String> triggerOutOfMemory() {
        try {
            // 대량의 데이터를 생성하여 메모리 부족 상황 유발
            byte[] data = new byte[Integer.MAX_VALUE];
            return ResponseEntity.ok("메모리 부족 오류가 발생하지 않았습니다");
        } catch (OutOfMemoryError e) {
            throw new ServiceException("메모리 부족 오류 발생", e);
        }
    }
}