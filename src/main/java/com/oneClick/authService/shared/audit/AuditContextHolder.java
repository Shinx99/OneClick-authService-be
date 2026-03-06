package com.oneClick.authService.shared.audit;

import com.oneClick.authService.shared.security.CustomUserDetail.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

// AuditContextHolder.java
@Component
public class AuditContextHolder {
    private static final ThreadLocal<UUID> currentAccountId = new ThreadLocal<>();
    
    public static void setCurrentAccountId(UUID id) { currentAccountId.set(id); }
    public static UUID getCurrentAccountId() { return currentAccountId.get(); }
    public static void clear() { currentAccountId.remove(); }
    
    // *** MỚI: Set từ JWT token ***
    public static void setFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserPrincipal userPrincipal) {
                setCurrentAccountId(userPrincipal.getId());
            }
        }
    }

}

