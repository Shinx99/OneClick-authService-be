package com.oneClick.authService_be.infrastructure.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

// infrastructure/security/handler/CustomAccessDeniedHandler.java
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        
        log.error("Access denied: {}", accessDeniedException.getMessage());
        
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        Map<String, Object> errorResponse = Map.of(
            "error", Map.of(
                "code", "FORBIDDEN",
                "message", "You do not have permission to access this resource",
                "details", accessDeniedException.getMessage()
            ),
            "timestamp", Instant.now().toString()
        );
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
