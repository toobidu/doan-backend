package org.example.quizizz.security;

import org.example.quizizz.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        // Tự động chọn thuật toán dựa vào độ dài secret key
        return Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
    }

    // Tạo access token với custom claims
    public String generateAccessToken(Long userId, String typeAccount, String rank) {
        return createToken(
                Map.of(
                        "userId", userId,
                        "typeAccount", typeAccount,
                        "rank", rank
                ),
                userId.toString(),
                jwtConfig.getAccessExpiration()
        );
    }

    // Tạo refresh token chỉ với userId
    public String generateRefreshToken(Long userId) {
        return createToken(
                Map.of("userId", userId),
                userId.toString(),
                jwtConfig.getRefreshExpiration()
        );
    }

    // Tạo token chung
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Lấy userId từ token
    public Long getUserIdFromToken(String token) {
        Object userId = getClaimFromToken(token, claims -> claims.get("userId"));
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        if (userId instanceof Long) {
            return (Long) userId;
        }
        if (userId instanceof String) {
            return Long.valueOf((String) userId);
        }
        return null;
    }

    // Lấy username từ token (subject)
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // Lấy custom claim từ token
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Lấy toàn bộ claims
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Kiểm tra token còn hạn không
    public boolean isTokenExpired(String token) {
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // Kiểm tra token hợp lệ
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            Claims claims = getAllClaimsFromToken(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
