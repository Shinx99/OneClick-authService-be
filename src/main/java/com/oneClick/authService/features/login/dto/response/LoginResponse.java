package com.oneClick.authService.features.login.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    //Basic User Info
    private UUID accountId;
    private String status;
    private String email;
    private Boolean emailVerifiedAt;
    private List<String> roles;

    //Token Info
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;

    //Session Info
    private UUID sessionId;

}
