// src/main/java/com/oneClick/authService_be/web/controller/TestController.java
package com.oneClick.authService_be.web.devtools;

import com.oneClick.authService_be.infrastructure.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "local"})  // Only active in dev/local profiles
public class TestController {
    
    private final JwtService jwtService;
    
    /**
     * Generate a test JWT token
     * Usage: curl http://localhost:8080/api/test/generate-token
     */
    @GetMapping("/generate-token")
    public Map<String, String> generateToken(
        @RequestParam(defaultValue = "test-user-123") String userId,
        @RequestParam(defaultValue = "test@example.com") String email,
        @RequestParam(defaultValue = "ROLE_USER") String role
    ) {
        log.info("🔑 Generating test token for userId: {}", userId);
        
        String token = jwtService.generateAccessToken(
            userId,
            email,
            List.of(role)
        );
        
        return Map.of(
            "token", token,
            "userId", userId,
            "email", email,
            "role", role
        );
    }
    
    /**
     * Test protected endpoint
     * Usage: curl -H "Authorization: Bearer <token>" http://localhost:8080/api/test/protected
     */
    @GetMapping("/protected")
    public Map<String, Object> protectedEndpoint() {
        log.info("🔒 Accessing protected endpoint");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return Map.of(
                "message", "Not authenticated",
                "authenticated", false
            );
        }
        
        return Map.of(
            "message", "You accessed protected resource!",
            "userId", auth.getName(),
            "authorities", auth.getAuthorities(),
            "authenticated", true
        );
    }
    
    /**
     * Decode and display token info
     * Usage: curl -H "Authorization: Bearer <token>" http://localhost:8080/api/test/decode-token
     */
    @GetMapping("/decode-token")
    public Map<String, Object> decodeToken(@RequestHeader("Authorization") String authHeader) {
        log.info("🔍 Decoding token");
        
        if (!authHeader.startsWith("Bearer ")) {
            return Map.of("error", "Invalid Authorization header format");
        }
        
        String token = authHeader.substring(7);
        
        try {
            String userId = jwtService.extractUserId(token);
            String email = jwtService.extractEmail(token);
            List<String> roles = jwtService.extractRoles(token);
            boolean isValid = jwtService.isTokenValid(token);
            
            return Map.of(
                "userId", userId,
                "email", email,
                "roles", roles,
                "isValid", isValid
            );
        } catch (Exception e) {
            return Map.of(
                "error", "Invalid token",
                "details", e.getMessage()
            );
        }
    }
    
    /**
     * Health check for test endpoints
     */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of(
            "status", "OK",
            "message", "Test endpoints are active",
            "profile", System.getProperty("spring.profiles.active", "default")
        );
    }
}
