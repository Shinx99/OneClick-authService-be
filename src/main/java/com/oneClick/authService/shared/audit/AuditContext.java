package com.oneClick.authService.shared.audit;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AuditContext {

    private static final ThreadLocal<UUID> currentAccountId = new ThreadLocal<>();

    public static void setCurrentAccountId(UUID id){
        currentAccountId.set(id);
    }

    public static UUID getAccountId(){
        return currentAccountId.get();
    }

    public static void clear(){
        currentAccountId.remove();
    }
}
