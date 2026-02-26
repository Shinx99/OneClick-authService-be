package com.oneClick.authService.features.verifyemail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyEmailResponse {

    private Long accountId;
    private String message;
}
