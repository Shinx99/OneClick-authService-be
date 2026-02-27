package com.oneClick.authService.features.verifyemail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class VerifyEmailResponse {

    private UUID accountId;
    private String message;
}
