package com.oneClick.authService.shared.event;

import lombok.Getter;

@Getter
public abstract class AuditEvent {
    private final Long accountId;
    private final String eventType;
    private final String ip;
    private final String userAgent;
    private final String meta;

    public AuditEvent(Long accountId, String eventType, String ip, String userAgent, String meta) {
        this.accountId = accountId;
        this.eventType = eventType;
        this.ip = ip;
        this.userAgent = userAgent;
        this.meta = meta;
    }
}
