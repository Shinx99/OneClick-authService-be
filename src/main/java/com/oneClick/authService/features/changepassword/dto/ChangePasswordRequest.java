package com.oneClick.authService.features.changepassword.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword
) {}
