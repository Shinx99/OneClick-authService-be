package com.oneClick.authService.features.register.dto;
// features/register/dto/RegisterRequest.java

public record RegisterRequest (
         String email,
         String password,
         String phone
) {}
