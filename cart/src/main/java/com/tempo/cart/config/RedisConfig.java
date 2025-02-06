package com.tempo.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration  // 이 클래스가 설정 파일임을 선언
public class RedisConfig {

    // Redis와의 연결을 담당하는 RedisConnectionFactory 빈 등록
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // LettuceConnectionFactory: 기본적으로 Spring Boot에서 권장하는 Redis 클라이언트
        // 생성자에 host와 port를 지정할 수 있습니다.
        return new LettuceConnectionFactory("localhost", 6379);
    }

    // RedisTemplate 빈 등록 (Redis와의 데이터 입출력을 쉽게 하기 위한 도구)
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        // 키는 String 형태로, 값은 JSON 형태로 직렬화하는 설정 예시
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 해시 자료형을 위한 시리얼라이저 설정도 필요할 경우 추가
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}