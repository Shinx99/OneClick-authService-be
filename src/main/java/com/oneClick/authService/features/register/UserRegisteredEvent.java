package com.oneClick.authService.features.register;

import com.oneClick.authService.shared.event.AuditEvent;
import java.util.UUID;

public class UserRegisteredEvent extends AuditEvent {
    public UserRegisteredEvent(UUID accountId, String ip, String userAgent, String meta) {
        super(accountId, "USER_REGISTERED", ip, userAgent, meta);
    }
}
