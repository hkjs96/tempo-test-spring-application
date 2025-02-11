package com.tempo.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UserDto {

    @Getter
    public static class SignUpRequest {
        private String email;
        private String password;
        private String name;
    }

    @Getter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Builder
    public static class TokenResponse {
        private String token;
    }

    @Getter
    @Builder
    public static class UserResponse {
        private Long id;
        private String email;
        private String name;
    }
}
