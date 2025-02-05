package com.tempo.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderErrorTestController {

    // Order 서비스의 에러 발생 엔드포인트
    @GetMapping("/error")
    public ResponseEntity<String> triggerOrderError() {
        throw new RuntimeException("Order 서비스 내부 오류 발생");
    }
}