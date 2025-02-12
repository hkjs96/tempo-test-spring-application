package com.tempo.user.security;

import com.tempo.user.domain.RefreshToken;
import com.tempo.user.dto.TokenDto;
import com.tempo.user.repository.RefreshTokenRepository;
import com.tempo.user.repository.TokenBlacklistRepository;
import com.tempo.user.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final SecretKey key;
    private final JwtParser jwtParser;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final CustomUserDetailsService userDetailsService;
    private final TokenBlacklistRepository blacklistRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenDto createToken(String username) {
        String accessToken = createAccessToken(username);
        String refreshToken = createRefreshToken(username);

        // RefreshToken 저장
        refreshTokenRepository.save(RefreshToken.builder()
                .refreshToken(refreshToken)
                .userEmail(username)
                .build());

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenValidityInMilliseconds)
                .build();
    }

    private String createAccessToken(String username) {
        Claims claims = Jwts.claims()
                .subject(username)
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    private String createRefreshToken(String username) {
        Claims claims = Jwts.claims()
                .subject(username)
                .build();

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            if (blacklistRepository.existsByToken(token)) {
                throw new JwtAuthenticationException("Blacklisted token");
            }
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public void blacklistToken(String token, String reason) {
        blacklistRepository.save(TokenBlacklist.builder()
                .token(token)
                .reason(reason)
                .build());
    }

    public TokenDto refreshToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new JwtAuthenticationException("Invalid refresh token");
        }

        RefreshToken savedRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtAuthenticationException("Refresh token not found"));

        String username = savedRefreshToken.getUserEmail();

        // 기존 RefreshToken 삭제
        refreshTokenRepository.delete(savedRefreshToken);

        // 새로운 토큰 발급
        return createToken(username);
    }
}