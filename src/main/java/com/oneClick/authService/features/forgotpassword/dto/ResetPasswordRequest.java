package com.oneClick.authService.features.forgotpassword.dto;

public record ResetPasswordRequest(String token, String newPassword) {
}
