package com.tempo.monolith.user.controller;

import com.tempo.monolith.user.dto.LoginRequestDto;
import com.tempo.monolith.user.dto.SignUpRequestDto;
import com.tempo.monolith.user.dto.TokenResponseDto;
import com.tempo.monolith.user.dto.UserResponseDto;
import com.tempo.monolith.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@RequestBody SignUpRequestDto request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@RequestHeader("Authorization") String authHeader) {
        // 실제 환경에서는 SecurityContextHolder에서 이메일을 가져오도록 함
        String email = authHeader.substring(7); // 예시 (실제 구현 시 파싱 필요)
        return ResponseEntity.ok(userService.getMyInfo(email));
    }
}