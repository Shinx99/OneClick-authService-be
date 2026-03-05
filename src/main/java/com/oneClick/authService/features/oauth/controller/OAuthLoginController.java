package com.oneClick.authService.features.oauth.controller;

import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.features.oauth.dto.request.OAuthCallBackRequest;
import com.oneClick.authService.features.oauth.handler.OAuthHandler;
import com.oneClick.authService.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OAuthLoginController {

    private final OAuthHandler oAuthHandler;

    @PostMapping("/oauth/{provider}/callback")
    public ResponseEntity<ApiResponse<LoginResponse>> oauthLogin(
            @PathVariable String provider,
            @RequestBody OAuthCallBackRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        return ResponseEntity.ok(oAuthHandler.loginWithOAuth(provider, request.getProviderUserId(), httpRequest, httpResponse));
    }

}
