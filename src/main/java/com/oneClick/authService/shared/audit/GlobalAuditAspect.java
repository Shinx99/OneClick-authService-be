package com.oneClick.authService.shared.audit;

import com.oneClick.authService.shared.entity.AuditLog;
import com.oneClick.authService.shared.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.oneClick.authService.shared.audit.AuditContextHolder;

import java.net.InetAddress;
import java.util.UUID;

// GlobalAuditAspect.java - REPLACE all
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GlobalAuditAspect {
    private final AuditLogRepository auditLogRepo;

    @Pointcut("execution(* com.oneClick.authService.features..*Controller.*(..))")
    public void controllers() {}

    @AfterReturning(pointcut = "controllers()", returning = "result")
    public void logSuccess(JoinPoint jp, Object result) {
        log.debug("SUCCESS: {}", jp.getSignature());
        HttpServletRequest req = getRequest();
        String eventType = detectEventType(req.getRequestURI(), req.getMethod());
        saveAudit(req, eventType, "SUCCESS", buildMetaSuccess(result, req));
    }

    @AfterThrowing(pointcut = "controllers()", throwing = "ex")
    public void logFailure(JoinPoint jp, Exception ex) {
        log.debug("FAIL: {} - {}", jp.getSignature(), ex.getMessage());
        HttpServletRequest req = getRequest();
        String eventType = detectEventType(req.getRequestURI(), req.getMethod()) + "_FAIL";
        saveAudit(req, eventType, "FAIL", buildMetaFailure(ex));
    }

    // *** FIX: Detect refresh/logout + method ***
    private String detectEventType(String path, String method) {
        String lowerPath = path.toLowerCase();
        if (lowerPath.contains("/register")) return "REGISTER";
        if (lowerPath.contains("/verify-email")) return "EMAIL_VERIFY";
        if (lowerPath.contains("/login")) return "LOGIN";
        if (lowerPath.contains("/refresh-token")) return "REFRESH_TOKEN";
        if (lowerPath.contains("/logout")) return "LOGOUT";
        return "API_CALL";
    }

    // *** NEW: Rich meta cho refresh/logout ***
    private String buildMetaSuccess(Object result, HttpServletRequest req) {
        String eventType = detectEventType(req.getRequestURI(), req.getMethod());
        StringBuilder meta = new StringBuilder();

        if ("REFRESH_TOKEN".equals(eventType) || "LOGOUT".equals(eventType)) {
            meta.append("{\"status\":\"SUCCESS\"");
            meta.append(",\"path\":\"").append(req.getRequestURI()).append("\"");
            meta.append(",\"method\":\"").append(req.getMethod()).append("\"");
            meta.append(",\"ip\":\"").append(req.getRemoteAddr()).append("\"");
            meta.append(",\"userAgent\":\"").append(req.getHeader("User-Agent")).append("\"");

            UUID accountId = AuditContextHolder.getCurrentAccountId();
            if (accountId != null) meta.append(",\"accountId\":\"").append(accountId).append("\"");

            // Cookie refreshToken (ẩn value, chỉ log existence)
            String refreshToken = req.getCookies() != null ?
                    java.util.Arrays.stream(req.getCookies())
                            .filter(c -> "refreshToken".equals(c.getName()))
                            .findFirst().map(c -> "EXISTS").orElse("NONE") : "NONE";
            meta.append(",\"refreshToken\":\"").append(refreshToken).append("\"");

            meta.append("}");
        } else {
            meta.append("{\"status\":\"SUCCESS\",\"detail\":\"OK\"}");
        }
        return meta.toString();
    }

    private String buildMetaFailure(Exception ex) {
        return "{\"status\":\"FAIL\",\"reason\":\"" + ex.getClass().getSimpleName() +
                "\",\"message\":\"" + ex.getMessage() + "\"}";
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveAudit(HttpServletRequest req, String eventType, String status, String jsonMeta) {
        try {
            InetAddress ip = InetAddress.getByName(req.getRemoteAddr());
            UUID accountId = AuditContextHolder.getCurrentAccountId();
            log.debug("Get accountId successful: " + accountId);

            AuditLog audit = AuditLog.logEvent(accountId, eventType, ip,
                    req.getHeader("User-Agent"), jsonMeta);
            auditLogRepo.saveAndFlush(audit);
            log.debug("✅ SAVED: {} - {} - {}", eventType, status, jsonMeta);
        } catch (Exception e) {
            log.error("❌ Audit save FAILED: {}", e.getMessage(), e);
        }
    }

    // GlobalAuditAspect.java
    @Before("controllers()")
    public void setContextBefore(JoinPoint jp) {
        AuditContextHolder.setFromSecurityContext();
        log.debug("🔍 {} - AccountId: {} - URI: {}",
                jp.getSignature(),
                AuditContextHolder.getCurrentAccountId(),
                getRequest().getRequestURI());
    }


    @After("controllers()")
    public void clearContext(JoinPoint jp) {
        AuditContextHolder.clear();
    }
}
