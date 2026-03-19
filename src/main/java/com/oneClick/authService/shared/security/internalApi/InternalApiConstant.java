package com.oneClick.authService.shared.security.internalApi;

import org.springframework.stereotype.Component;

@Component
public class InternalApiConstant {
    public static final String INTERNAL_API_KEY_HEADER = "X-Internal_API_Key";
    public static final String INVALID_API_KEY_MSG = "Invalid internal API key";
}
