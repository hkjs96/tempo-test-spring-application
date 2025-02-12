package com.tempo.user.controller;

import com.tempo.user.dto.LoginRequestDto;
import com.tempo.user.dto.SignUpRequestDto;
import com.tempo.user.dto.TokenResponseDto;
import com.tempo.user.dto.UserResponseDto;
import com.tempo.user.service.UserService;
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
    public ResponseEntity<UserResponseDto> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }
}