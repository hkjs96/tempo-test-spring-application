package com.tempo.cart.service;

import com.tempo.cart.domain.CartItem;
import com.tempo.cart.dto.CartItemRequest;
import com.tempo.cart.dto.CartItemResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CartServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final Long userId = 1L;
    private final Long productId = 100L;

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    @DisplayName("장바구니에 상품을 추가할 수 있다")
    void addCartItem() {
        // given
        CartItemRequest request = CartItemRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(2)
                .productName("테스트 상품")
                .price(10000L)
                .build();

        // when
        CartItemResponse response = cartService.addCartItem(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getProductId()).isEqualTo(productId);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualTo(20000L);
    }

    @Test
    @DisplayName("사용자의 장바구니 목록을 조회할 수 있다")
    void getCartItems() {
        // given
        CartItemRequest request1 = CartItemRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(2)
                .productName("테스트 상품1")
                .price(10000L)
                .build();

        CartItemRequest request2 = CartItemRequest.builder()
                .userId(userId)
                .productId(productId + 1)
                .quantity(1)
                .productName("테스트 상품2")
                .price(20000L)
                .build();

        cartService.addCartItem(request1);
        cartService.addCartItem(request2);

        // when
        List<CartItemResponse> cartItems = cartService.getCartItems(userId);

        // then
        assertThat(cartItems).hasSize(2);
        assertThat(cartItems)
                .extracting("productName")
                .containsExactlyInAnyOrder("테스트 상품1", "테스트 상품2");
    }

    @Test
    @DisplayName("장바구니에서 상품을 제거할 수 있다")
    void removeCartItem() {
        // given
        CartItemRequest request = CartItemRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(2)
                .productName("테스트 상품")
                .price(10000L)
                .build();
        cartService.addCartItem(request);

        // when
        cartService.removeCartItem(userId, productId);

        // then
        List<CartItemResponse> cartItems = cartService.getCartItems(userId);
        assertThat(cartItems).isEmpty();
    }

    @Test
    @DisplayName("장바구니 상품의 수량을 업데이트할 수 있다")
    void updateCartItemQuantity() {
        // given
        CartItemRequest request = CartItemRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(2)
                .productName("테스트 상품")
                .price(10000L)
                .build();
        cartService.addCartItem(request);

        // when
        cartService.updateCartItemQuantity(userId, productId, 3);

        // then
        List<CartItemResponse> cartItems = cartService.getCartItems(userId);
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.get(0).getQuantity()).isEqualTo(3);
        assertThat(cartItems.get(0).getTotalPrice()).isEqualTo(30000L);
    }
}