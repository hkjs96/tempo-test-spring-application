package com.tempo.product.controller;

import com.tempo.product.dto.ProductRequest;
import com.tempo.product.dto.ProductResponse;
import com.tempo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
class ProductController {
    private final ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        log.info("GET /api/products/{} 요청 수신", id);
        ProductResponse response = productService.getProduct(id);
        log.info("GET /api/products/{} 응답 완료", id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        log.info("POST /api/products 요청 수신 - name: {}", request.getName());
        ProductResponse response = productService.createProduct(request);
        log.info("POST /api/products 응답 완료 - productId: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        log.info("PUT /api/products/{}/stock 요청 수신 - quantity: {}", id, quantity);
        productService.updateStock(id, quantity);
        log.info("PUT /api/products/{}/stock 응답 완료", id);
        return ResponseEntity.ok().build();
    }
}