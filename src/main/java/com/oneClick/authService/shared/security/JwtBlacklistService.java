package com.oneClick.authService.shared.security;

// src/main/java/com/oneClick/authService_be/security/jwt/JwtBlacklistService.java

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    public void blacklistToken(String token, long expirationSeconds) {
        log.info("🚫 Blacklisting token (expires in {}s)", expirationSeconds);
        redisTemplate.opsForValue().set("blacklist:" + token, "BLACKLISTED", expirationSeconds, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        Boolean blacklisted = redisTemplate.hasKey("blacklist:" + token);
        return Boolean.TRUE.equals(blacklisted);
    }

    public void validateTokenNotBlacklisted(String token) {
        if (isBlacklisted(token)) {
            throw new IllegalStateException("Token has been revoked");
        }
    }
}
