package com.tempo.user.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "blacklist", timeToLive = 86400) // 24시간
public class TokenBlacklist {
    @Id
    private String id;

    @Indexed
    private String token;

    private String reason;

    @Builder
    public TokenBlacklist(String token, String reason) {
        this.token = token;
        this.reason = reason;
    }
}
