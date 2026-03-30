package com.oneClick.authService.features.register.dto;
// features/register/dto/RegisterRequest.java

import java.util.Set;

public record RegisterRequest (
         String email,
         String password,
         String phone,
         Set<String> roles
) {}
