package com.oneClick.authService.features.verifyemail.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyEmailRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
