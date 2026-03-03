package com.oneClick.authService.features.refreshtoken.dto.response;

import lombok.*;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
}
