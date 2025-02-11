package com.tempo.user.controller;

import com.tempo.user.dto.UserDto;
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
    public ResponseEntity<UserDto.UserResponse> signup(@RequestBody UserDto.SignUpRequest request) {
        return ResponseEntity.ok(userService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.TokenResponse> login(@RequestBody UserDto.LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto.UserResponse> getMyInfo() {
        return ResponseEntity.ok(userService.getMyInfo());
    }
}