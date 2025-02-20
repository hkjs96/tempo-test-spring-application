package com.tempo.monolith.user.service;

import com.tempo.monolith.user.domain.User;
import com.tempo.monolith.user.dto.LoginRequestDto;
import com.tempo.monolith.user.dto.SignUpRequestDto;
import com.tempo.monolith.user.dto.TokenResponseDto;
import com.tempo.monolith.user.dto.UserResponseDto;
import com.tempo.monolith.user.repository.UserRepository;
import com.tempo.monolith.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto signup(SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();
        User savedUser = userRepository.save(user);
        return UserResponseDto.from(savedUser);
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            String token = jwtTokenProvider.generateToken(auth);
            return TokenResponseDto.builder().token(token).type("Bearer").build();
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponseDto.from(user);
    }
}