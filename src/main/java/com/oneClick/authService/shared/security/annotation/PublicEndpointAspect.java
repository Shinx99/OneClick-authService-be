// src/main/java/com/oneClick/authService_be/annotation/PublicEndpointAspect.java
package com.oneClick.authService.shared.security.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PublicEndpointAspect {
    
    @Around("@annotation(publicEndpoint)")
    public Object handlePublicEndpoint(ProceedingJoinPoint joinPoint, PublicEndpoint publicEndpoint) throws Throwable {
        // Clear security context for public endpoints
        SecurityContextHolder.clearContext();
        log.debug("🌐 Public endpoint accessed: {}", joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}
