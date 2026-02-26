
// shared/event/AuditEventListener.java (hoặc trong RegisterEventListener)
package com.oneClick.authService.shared.event;

import com.oneClick.authService.shared.entity.AuditLog;
import com.oneClick.authService.shared.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditLogRepository auditLogRepo;

    // Generic listener cho tất cả audit events
    @Async("taskExecutor")
    @EventListener
    public void handleAuditEvent(AuditEvent event) {
        try {
            AuditLog auditLog = AuditLog.logEvent(
                    event.getAccountId(),
                    event.getEventType(),
                    event.getIp(),
                    event.getUserAgent(),
                    event.getMeta()
            );
            auditLogRepo.save(auditLog);
            log.debug("Audit logged: {}", event.getEventType());
        } catch (Exception e) {
            log.error("Failed to log audit event: {}", event.getEventType(), e);
        }
    }
}



/*
Workflow
1. POST /verify-email → VerifyEmailHandler.handle()
2. Handler: business logic + eventPublisher.publishEvent(EmailVerifiedEvent)
3. Spring EventBus → AuditEventListener.handleAuditEvent() (ASYNC)
4. Listener: AuditLog.logEvent() → auditLogRepo.save()
5. ✅ Audit trail tự động, Handler clean!

Bonus: EmailListener gửi mail, MetricsListener +counter...

*/
