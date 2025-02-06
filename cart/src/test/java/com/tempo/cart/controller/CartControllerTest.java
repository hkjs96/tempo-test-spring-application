package com.tempo.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tempo.cart.dto.CartItemRequest;
import com.tempo.cart.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@Import(CartControllerTest.TestConfig.class)
class CartControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CartService cartService() {
            return mock(CartService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("장바구니 목록 조회 API")
    void getCartItems() throws Exception {
        // given
        Long userId = 1L;
        when(cartService.getCartItems(userId))
                .thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/cart/{userId}", userId))
                .andExpect(status().isOk());

        verify(cartService).getCartItems(userId);
    }

    @Test
    @DisplayName("장바구니 상품 추가 API")
    void addCartItem() throws Exception {
        // given
        CartItemRequest request = CartItemRequest.builder()
                .userId(1L)
                .productId(100L)
                .quantity(2)
                .productName("테스트 상품")
                .price(10000L)
                .build();

        // when & then
        mockMvc.perform(post("/api/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).addCartItem(any(CartItemRequest.class));
    }

    @Test
    @DisplayName("장바구니 상품 제거 API")
    void removeCartItem() throws Exception {
        // given
        Long userId = 1L;
        Long productId = 100L;

        // when & then
        mockMvc.perform(delete("/api/cart/{userId}/products/{productId}", userId, productId))
                .andExpect(status().isOk());

        verify(cartService).removeCartItem(userId, productId);
    }
}