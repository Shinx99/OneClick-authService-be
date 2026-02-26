// shared/entity/AuditLog.java
package com.oneClick.authService.shared.entity;

import com.oneClick.authService.shared.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_audit_logs")
@AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "audit_id")),

})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLog extends BaseAuditEntity {

    @Column(name = "account_id", nullable = true)
    private UUID accountId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;  // LOGIN_SUCCESS, REGISTER, PASSWORD_RESET...

    @Column(name = "ip", columnDefinition = "inet")
    private String ip;

    @Column(name = "user_agent", columnDefinition = "text")
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "meta", columnDefinition = "jsonb", nullable = true)
    String meta;  // {"clientId": "app-mobile", "device": "iPhone14"}

    // Convenience methods
    public void setCurrentUserContext(UUID accountId, String ip, String userAgent) {
        this.accountId = accountId;
        this.ip = ip;
        this.userAgent = userAgent;
    }

    public static AuditLog loginSuccess(UUID accountId, String ip, String userAgent) {
        return AuditLog.builder()
                .accountId(accountId)
                .eventType("LOGIN_SUCCESS")
                .ip(ip)
                .userAgent(userAgent)
                .build();
    }

    public static AuditLog register(UUID accountId, String ip, String userAgent) {
        return AuditLog.builder()
                .accountId(accountId)
                .eventType("ACCOUNT_REGISTERED")
                .ip(ip)
                .userAgent(userAgent)
                .meta("{\"status\": \"PENDING_EMAIL\"}")
                .build();
    }
}
