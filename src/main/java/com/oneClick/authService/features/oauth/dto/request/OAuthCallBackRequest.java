package com.oneClick.authService.features.oauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuthCallBackRequest {

    @NotBlank(message = "Provider user ID is required")
    private String providerUserId;

    private String email;
    private String name;
}
