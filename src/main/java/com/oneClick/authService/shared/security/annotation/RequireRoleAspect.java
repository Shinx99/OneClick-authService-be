// src/main/java/com/oneClick/authService_be/annotation/RequireRoleAspect.java
package com.oneClick.authService.shared.security.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@Slf4j
@Aspect
@Component
public class RequireRoleAspect {
    
    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        
        boolean hasRole = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_" + requireRole.value()));
        
        if (!hasRole) {
            log.warn("❌ Access denied for user {} to role {}", auth.getName(), requireRole.value());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Required role: " + requireRole.value());
        }
        
        return joinPoint.proceed();
    }
}
