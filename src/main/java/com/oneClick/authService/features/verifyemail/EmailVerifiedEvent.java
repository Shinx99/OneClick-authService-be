// features/verifyemail/event/EmailVerifiedEvent.java
package com.oneClick.authService.features.verifyemail;

import com.oneClick.authService.shared.event.AuditEvent;

public class EmailVerifiedEvent extends AuditEvent {
    public EmailVerifiedEvent(Long accountId, String ip, String userAgent, String meta) {
        super(accountId, "EMAIL_VERIFIED", ip, userAgent, meta);
    }
}
