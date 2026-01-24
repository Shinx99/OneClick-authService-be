/*
package com.oneClick.authService_be.domain.authentication.model.aggregate;

import com.oneClick.authService_be.domain.account.model.aggregate.Account;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;


@Entity
@Table(name = "auth_sessions")
public class Session extends AggregateRoot {

    @Id
    @Column(name = "session_id")
    private UUID sessionId;  // UUID

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Embedded
    private IpAddress ip;  // INET → Value object

    @Column(name = "user_agent")
    private String userAgent;  // TEXT

    @Embedded
    private DeviceInfo deviceInfo;  // Parsed from user_agent

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    // Business logic
    public void revoke() {
        if (this.revokedAt != null) {
            throw new SessionAlreadyRevokedException();
        }
        this.revokedAt = Instant.now();
        registerEvent(new SessionRevokedEvent(this.sessionId));
    }

    public boolean isActive() {
        return this.revokedAt == null;
    }

    public void updateLastSeen() {
        this.lastSeenAt = Instant.now();
    }
}*/
