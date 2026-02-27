// src/main/java/com/oneClick/authService_be/infrastructure/security/jwt/JwtTokenProvider.java
package com.oneClick.authService.shared.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Generate access token with user info and roles
     */
    public String generateAccessToken(String userId, List<String> roles) {
        log.debug("Generating access token for user: {}", userId);
        log.debug("Secret status: {}", jwtSecret != null ? "PRESENT" : "NULL");

        return Jwts.builder()
                .subject(userId)
                //.claim("email", email)
                .claim("roles", roles)
                .issuer(issuer)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate refresh token with UUID type
     */
    public String generateRefreshToken(String userId) {
        log.debug("Generating refresh token for user: {}", userId);

        return UUID.randomUUID().toString();
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

//    public String extractEmail(String token) {
//        return extractClaim(token, claims -> claims.get("email", String.class));
//    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Thêm vào JwtTokenProvider
    public Long getAccessTokenExpiry() {
        return accessTokenExpiration;  // ← trả về giá trị từ config (ms)
    }

    public Long getRefreshTokenExpiry() { return refreshTokenExpiration; }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            throw new IllegalStateException("JWT secret is not configured!");
        }
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
