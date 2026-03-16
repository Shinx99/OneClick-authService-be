package com.oneClick.authService.shared.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;  // Thêm để parse/validate

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Generate RS256 access token với claims
     */
    public String generateAccessToken(String userId, List<String> roles) {
        log.debug("Generating RS256 access token for user: {}", userId);

        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(accessTokenExpiration);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userId)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256)
                .keyId("auth-key")
                .build();

        JwtEncoderParameters params = JwtEncoderParameters.from(header, claims);


        return jwtEncoder.encode(params).getTokenValue();
    }

    /**
     * Generate refresh token UUID
     */
    public String generateRefreshToken(String userId) {
        log.debug("Generating refresh token for user: {}", userId);
        return UUID.randomUUID().toString();
    }

    /**
     * Extract userId (subject)
     */
    public String extractUserId(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return jwt.getSubject();
    }

    /**
     * Extract roles
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return (List<String>) jwt.getClaimAsStringList("roles");
    }

    /**
     * Validate token
     */
    public boolean isTokenValid(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            Instant expiresAt = jwt.getExpiresAt();
            return expiresAt != null && expiresAt.isAfter(Instant.now());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Long getAccessTokenExpiry() {
        return accessTokenExpiration;
    }

    public Long getRefreshTokenExpiry() {
        return refreshTokenExpiration;
    }
}
