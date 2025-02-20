package com.tempo.monolith.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponseDto {
    private final String token;
    private final String type;
}