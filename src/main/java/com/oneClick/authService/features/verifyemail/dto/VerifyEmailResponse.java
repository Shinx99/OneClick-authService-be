package com.oneClick.authService.features.verifyemail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class VerifyEmailResponse {

    private UUID accountId;
    private String email;
    private String phone;
    private String status;
    private Set<String> roles;
    private String message;
}
