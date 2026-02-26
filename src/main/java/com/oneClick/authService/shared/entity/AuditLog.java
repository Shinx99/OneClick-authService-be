package com.oneClick.authService.shared.entity;

import com.oneClick.authService.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "auth_audit_logs")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "audit_id")),
        // ✅ Giữ id override, bỏ updatedAt vì BaseAuditEntity không có
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog extends BaseAuditEntity {

    @Column(name = "account_id", nullable = true)
    private Long accountId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;  // LOGIN_SUCCESS, EMAIL_VERIFIED...

    @Column(name = "ip", columnDefinition = "inet")
    private String ip;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta", columnDefinition = "jsonb", nullable = true)
    String meta;  // {"verified_at": "2026-02-26T10:00:00Z"}

    // ================================
    // ✅ GENERIC FACTORY METHODS
    // ================================

    /**
     * Generic audit log cho mọi event
     */
    public static AuditLog logEvent(Long accountId, String eventType, String ip, String userAgent, String meta) {
        return AuditLog.builder()
                .accountId(accountId)
                .eventType(eventType)
                .ip(ip)
                .userAgent(userAgent)
                .meta(meta)
                .build();
    }

    public static AuditLog logEvent(Long accountId, String eventType, String ip, String userAgent) {
        return logEvent(accountId, eventType, ip, userAgent, null);
    }

    // ================================
    // ✅ SPECIFIC EVENTS
    // ================================

    public static AuditLog emailVerified(Long accountId, String ip, String userAgent) {
        return logEvent(accountId, "EMAIL_VERIFIED", ip, userAgent,
                "{\"action\": \"account_activated\"}");
    }

    public static AuditLog accountRegistered(Long accountId, String ip, String userAgent) {
        return logEvent(accountId, "ACCOUNT_REGISTERED", ip, userAgent,
                "{\"status\": \"PENDING_EMAIL\"}");
    }

    public static AuditLog loginSuccess(Long accountId, String ip, String userAgent) {
        return logEvent(accountId, "LOGIN_SUCCESS", ip, userAgent);
    }

    public static AuditLog loginFailed(String ip, String userAgent, String reason) {
        return logEvent(null, "LOGIN_FAILED", ip, userAgent,
                "{\"reason\": \"" + reason + "\"}");
    }

    public static AuditLog passwordReset(Long accountId, String ip, String userAgent) {
        return logEvent(accountId, "PASSWORD_RESET", ip, userAgent);
    }

    public static AuditLog profileUpdated(Long accountId, String ip, String userAgent) {
        return logEvent(accountId, "PROFILE_UPDATED", ip, userAgent);
    }

    // ================================
    // ✅ UTILITY METHODS
    // ================================

    public void setCurrentUserContext(Long accountId, String ip, String userAgent) {
        this.accountId = accountId;
        this.ip = ip;
        this.userAgent = userAgent;
    }
}
