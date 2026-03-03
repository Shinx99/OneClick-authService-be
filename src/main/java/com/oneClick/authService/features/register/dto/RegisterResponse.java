package com.oneClick.authService.features.register.dto;
// features/register/dto/RegisterResponse.java

import java.util.UUID;

public record RegisterResponse (
        UUID accountId,
        String message
){}
