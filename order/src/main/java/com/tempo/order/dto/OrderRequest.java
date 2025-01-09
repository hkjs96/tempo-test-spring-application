package com.tempo.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderRequest {
    private Long productId;
    private Integer quantity;

    @Builder
    public OrderRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}