package com.oneClick.authService.features.refreshtoken.controller;

import com.oneClick.authService.features.refreshtoken.dto.response.TokenRefreshResponse;
import com.oneClick.authService.features.refreshtoken.handler.RefreshTokenHandler;
import com.oneClick.authService.shared.dto.ApiResponse;
import com.oneClick.authService.shared.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenHandler refreshTokenHandler;

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String cookieToken,
            @RequestBody(required = false) TokenRefreshResponse body,
            HttpServletResponse httpResponse
    ) {

        // Get rawRefreshToken from cookie, fallback to body
        String rawRefreshToken = (cookieToken != null) ? cookieToken : (body != null ? body.getRefreshToken() : null);

        // Check if rawRefreshToken is null -> message
        if (rawRefreshToken == null) {
            throw new UnauthorizedException("Refresh token is required");
        }

        // Build response
        ApiResponse<TokenRefreshResponse> response = refreshTokenHandler.refreshAccessToken(rawRefreshToken, httpResponse);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false) String rawRefreshToken,
            HttpServletResponse httpResponse
    ) {
        // Check if rawRefreshToken == null
        if(rawRefreshToken == null){
            log.debug("rawRefreshToken is null! Can't log out!");
            throw new UnauthorizedException("Refresh token is required");
        }

        // Build response to revoke refresh token
        ApiResponse<Void> response = refreshTokenHandler.revokeRefreshToken(rawRefreshToken, httpResponse);

        return ResponseEntity.ok(response);
    }

}



