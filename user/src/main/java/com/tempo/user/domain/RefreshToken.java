package com.tempo.user.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 604800) // 7일
public class RefreshToken {
    @Id
    private String id;

    @Indexed
    private String refreshToken;

    private String userEmail;

    @Builder
    public RefreshToken(String id, String refreshToken, String userEmail) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.userEmail = userEmail;
    }
}
