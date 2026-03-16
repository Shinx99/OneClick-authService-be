package com.oneClick.authService.features.refreshtoken.controller;

import com.oneClick.authService.features.refreshtoken.dto.response.TokenRefreshResponse;
import com.oneClick.authService.features.refreshtoken.handler.RefreshTokenHandler;
import com.oneClick.authService.shared.audit.AuditContextHolder;
import com.oneClick.authService.shared.domain.entity.RefreshToken;
import com.oneClick.authService.shared.dto.ApiResponse;
import com.oneClick.authService.shared.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@Tag(name="Auth Refresh", description = "Refresh & logout APIs")
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final RefreshTokenHandler refreshTokenHandler;

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refreshToken(
            @CookieValue(name = "refreshToken", required = false)
            @Parameter(description = "Cookie refreshToken")
            String cookieToken,
            @RequestBody(required = false)
            @Parameter(description = "Body fallback")
            TokenRefreshResponse body,
            HttpServletResponse httpResponse
    ) {

        // Get rawRefreshToken from cookie, fallback to body
        String rawRefreshToken = (cookieToken != null) ? cookieToken : (body != null ? body.getRefreshToken() : null);

        // Check if rawRefreshToken is null -> message
        if (rawRefreshToken == null) {
            throw new UnauthorizedException("Refresh token is required");
        }

        /// logs account id for auditLog
        UUID accountId = refreshTokenHandler.getAccountIdByRefreshToken(rawRefreshToken);
        if (accountId != null) {
            AuditContextHolder.setCurrentAccountId(accountId);
            log.debug("🔥 RefreshController SET AuditContext: {}", accountId);
        }

        // Build response
        ApiResponse<TokenRefreshResponse> response = refreshTokenHandler.refreshAccessToken(rawRefreshToken, httpResponse);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refreshToken", required = false)
            @Parameter(description = "Cookie refreshToken")
            String rawRefreshToken,
            HttpServletResponse httpResponse
    ) {
        // Check if rawRefreshToken == null
        if(rawRefreshToken == null){
            log.debug("rawRefreshToken is null! Can't log out!");
            throw new UnauthorizedException("Refresh token is required");
        }

        /// logs account id for auditLog
        UUID accountId = refreshTokenHandler.getAccountIdByRefreshToken(rawRefreshToken);
        if (accountId != null) {
            AuditContextHolder.setCurrentAccountId(accountId);
            log.debug("🔥 LogoutController SET AuditContext: {}", accountId);
        }

        // Build response to revoke refresh token
        ApiResponse<Void> response = refreshTokenHandler.revokeRefreshToken(rawRefreshToken, httpResponse);

        return ResponseEntity.ok(response);
    }

}



