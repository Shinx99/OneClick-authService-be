package com.oneClick.authService.features.refreshtoken.dto.response;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {

    //Basic User Info
    private UUID accountId;
    private String status;
    private String email;
    private List<String> roles;

    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
