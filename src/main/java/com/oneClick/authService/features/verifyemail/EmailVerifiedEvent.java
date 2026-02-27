// features/verifyemail/event/EmailVerifiedEvent.java
package com.oneClick.authService.features.verifyemail;

import com.oneClick.authService.shared.event.AuditEvent;

import java.util.UUID;

public class EmailVerifiedEvent extends AuditEvent {
    public EmailVerifiedEvent(UUID accountId, String ip, String userAgent, String meta) {
        super(accountId, "EMAIL_VERIFIED", ip, userAgent, meta);
    }
}
