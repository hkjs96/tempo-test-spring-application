package com.tempo.user.service;

import com.tempo.user.domain.User;
import com.tempo.user.dto.LoginRequestDto;
import com.tempo.user.dto.SignUpRequestDto;
import com.tempo.user.dto.TokenResponseDto;
import com.tempo.user.dto.UserResponseDto;
import com.tempo.user.repository.UserRepository;
import com.tempo.user.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserResponseDto signup(SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .build();

        User savedUser = userRepository.save(user);
        return UserResponseDto.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();
    }

    @Transactional(readOnly = true)
    public TokenResponseDto login(LoginRequestDto request) {
        try {
            // AuthenticationManager를 통해 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // 인증 성공 시 JWT 토큰 생성
            String token = jwtTokenProvider.generateToken(authentication);

            return TokenResponseDto.builder()
                    .token(token)
                    .type("Bearer")
                    .build();

        } catch (AuthenticationException e) {
            throw new BadCredentialsException("잘못된 계정정보입니다.");
        }
    }

    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}