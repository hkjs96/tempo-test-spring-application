package com.tempo.user.dto;

import com.tempo.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String name;

    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}