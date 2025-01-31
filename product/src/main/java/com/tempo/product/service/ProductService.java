package com.tempo.product.service;

import com.tempo.product.domain.Product;
import com.tempo.product.dto.ProductRequest;
import com.tempo.product.dto.ProductResponse;
import com.tempo.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        log.info("상품 조회 시작 - productId: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("상품을 찾을 수 없음 - productId: {}", id);
                    return new IllegalArgumentException("상품을 찾을 수 없습니다.");
                });
        log.info("상품 조회 완료 - productId: {}, name: {}", id, product.getName());
        return new ProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("상품 생성 시작 - name: {}", request.getName());
        Product product = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stock(request.getStock())
                .build();

        Product savedProduct = productRepository.save(product);
        log.info("상품 생성 완료 - productId: {}, name: {}", savedProduct.getId(), savedProduct.getName());
        return new ProductResponse(savedProduct);
    }

    @Transactional
    public void updateStock(Long id, Integer quantity) {
        log.info("재고 업데이트 시작 - productId: {}, quantity: {}", id, quantity);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("상품을 찾을 수 없음 - productId: {}", id);
                    return new IllegalArgumentException("상품을 찾을 수 없습니다.");
                });

        try {
            product.updateStock(quantity);
            log.info("재고 업데이트 완료 - productId: {}, updatedStock: {}", id, product.getStock());
        } catch (IllegalArgumentException e) {
            log.error("재고 부족 발생 - productId: {}, requestedQuantity: {}, currentStock: {}",
                    id, quantity, product.getStock());
            throw e;
        }
    }
}