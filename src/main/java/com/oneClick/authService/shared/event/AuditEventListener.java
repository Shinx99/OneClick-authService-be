
// shared/event/AuditEventListener.java (hoặc trong RegisterEventListener)
package com.oneClick.authService.shared.event;

//import com.oneClick.authService.features.register.event.AccountCreatedEvent;  // ✅ Fix 1: Import đúng
import com.oneClick.authService.shared.entity.AuditLog;
import com.oneClick.authService.shared.repository.AuditLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@Component
@Slf4j
@Async("taskExecutor")  // ✅ Fix 2: Chỉ @Async trên class/method, không @EventListener
public class AuditEventListener {

    private final AuditLogRepository auditLogRepo;
//    private final HttpServletRequest request;  // Inject nếu cần

    public AuditEventListener(AuditLogRepository auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

/*    @EventListener  // ✅ Fix 3: Giữ @EventListener
    public void handleAccountCreated(AccountCreatedEvent event) {
        AuditLog audit = AuditLog.register(
                event.getAccount().getId(),
                getClientIp(),
                getUserAgent()
        );
        auditLogRepo.save(audit);
    }*/

    // ✅ Fix 4: Implement missing methods
/*    private String getClientIp() {
        return Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(request.getRemoteAddr());
    }

    private String getUserAgent() {
        return request.getHeader("User-Agent");
    }*/
}


/*
Workflow Chi Tiết
text
1. User POST /api/auth/register (email="user@test.com")
2. RegisterHandler.register() → business logic OK
3. Handler cuối: eventPublisher.publishEvent(new AccountCreatedEvent(account))
4. Spring Event Bus → AuditEventListener.handleAccountCreated() (async)
5. Listener tạo AuditLog:
   account_id=123, event_type="ACCOUNT_REGISTERED",
   ip="192.168.1.1", user_agent="Chrome/120",
   meta={"status": "PENDING"}
6. auditLogRepo.save() → INSERT auth_audit_logs ✅
*/
