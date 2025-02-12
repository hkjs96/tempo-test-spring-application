package com.tempo.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenDto {
    private final String accessToken;
    private final String refreshToken;
    private final String tokenType;
    private final long expiresIn;
}