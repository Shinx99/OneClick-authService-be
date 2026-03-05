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

import java.net.InetAddress;
import java.util.UUID;

// GlobalAuditAspect.java - REPLACE all
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class GlobalAuditAspect {
    private final AuditLogRepository auditLogRepo;

    @Pointcut("execution(* com.oneClick.authService.features..*Controller.*(..))")  // Match Controller methods
    public void controllers() {}

    @AfterReturning(pointcut = "controllers()", returning = "result")
    public void logSuccess(JoinPoint jp, Object result) {
        log.debug("SUCCESS: {}", jp.getSignature());  // TEST LOG
        HttpServletRequest req = getRequest();
        String eventType = detectEventType(req.getRequestURI());
        saveAudit(req, eventType, "SUCCESS", result != null ? "OK" : "NO_CONTENT");
    }

    @AfterThrowing(pointcut = "controllers()", throwing = "ex")
    public void logFailure(JoinPoint jp, Exception ex) {
        log.debug("FAIL: {} - {}", jp.getSignature(), ex.getMessage());  // TEST LOG
        HttpServletRequest req = getRequest();
        String eventType = detectEventType(req.getRequestURI()) + "_FAIL";
        saveAudit(req, eventType, "FAIL", ex.getMessage());
    }

    @After(value = "controllers()")
    public void logAlways(JoinPoint jp) {
        log.debug("AUDIT ALWAYS: {}", jp.getSignature());
    }

    // Utils - FIX path /api/auth/register
    private String detectEventType(String path) {
        if (path.contains("/register")) return "REGISTER";
        if (path.contains("/verify-email")) return "EMAIL_VERIFY";
        if (path.contains("/login")) return "LOGIN";
        return "API_CALL";
    }

    private HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void saveAudit(HttpServletRequest req, String eventType, String status, String meta) {
        try {
            InetAddress ip = InetAddress.getByName(req.getRemoteAddr());

            UUID accountId = AuditContext.getAccountId();
            if(accountId == null) accountId = null;

            String jsonMeta = "{\"status\":\"" + status + "\",\"detail\":\"" + meta + "\"}";
            AuditLog audit = AuditLog.logEvent(accountId, eventType, ip, req.getHeader("User-Agent"), jsonMeta);
            auditLogRepo.saveAndFlush(audit);  // Flush ngay
            log.debug("✅ SAVED DB: {} - {}", eventType, status);  // CONFIRM SAVE
        } catch (Exception e) {
            log.error("❌ Audit FAILED {}: {}", eventType, e.getMessage(), e);
        }
    }

    @After(value = "controllers()")
    public void clearContext(JoinPoint jp){
        AuditContext.clear();
    }
}
