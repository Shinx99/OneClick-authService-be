package com.oneClick.authService.features.refreshtoken.handler;


import com.oneClick.authService.features.refreshtoken.dto.response.TokenRefreshResponse;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.RefreshToken;
import com.oneClick.authService.shared.domain.entity.Role;
import com.oneClick.authService.shared.domain.repository.RefreshTokenRepository;
import com.oneClick.authService.shared.dto.ApiResponse;
import com.oneClick.authService.shared.exception.UnauthorizedException;
import com.oneClick.authService.shared.security.jwt.JwtTokenProvider;
import com.oneClick.authService.shared.util.TokenHashUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenHashUtil tokenHashUtil;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @Transactional
    public ApiResponse<TokenRefreshResponse> refreshAccessToken(String rawRefreshToken, HttpServletResponse httpResponse){


        // 1. Hash refresh token from client then find in DB
        String tokenHash = tokenHashUtil.hash(rawRefreshToken);

        // 2. Find Hashed refresh token in db to compare
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        // 3. Check if token is expired -> kill it
        if(refreshToken.getExpiresAt().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token expired, please login again");
        }

        // 4. Get account and roles
        Account account = refreshToken.getAccount();
        List<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        // 5. Rotate refresh token (delete old refresh token and create new refresh token)
        // Delete the old one
        refreshTokenRepository.delete(refreshToken);

        // Create the new one
        String newRawRefreshToken = jwtTokenProvider.generateRefreshToken(account.getAccountId().toString());
        refreshTokenRepository.save(RefreshToken.builder()
                .session(refreshToken.getSession())
                .account(account)
                .tokenPrefix(newRawRefreshToken.substring(0, 8))
                .tokenHash(tokenHashUtil.hash(newRawRefreshToken))
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiry()))
                .build()
        );

        // 6. Create new access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(account.getAccountId().toString(), roles);

        // 7. Set refresh token into httpOnly cookie
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", newRawRefreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiry() / 1000)
                .build();
        httpResponse.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());


        // 8. Transfer data to DTO (TokenRefreshResponse)
        TokenRefreshResponse response = TokenRefreshResponse.builder()
                .accountId(account.getAccountId())
                .email(account.getEmail())
                .roles(roles)
                .status(account.getStatus())
                .accessToken(newAccessToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiry())
                .build();

        return ApiResponse.<TokenRefreshResponse>builder()
                .success(true)
                .message("Token has been refreshed successfully!")
                .data(response)
                .build();
    }

    @Transactional
    public ApiResponse<Void> revokeRefreshToken(String rawRefreshToken, HttpServletResponse httpResponse){

        // Find token hash in DB
        String tokenHash = tokenHashUtil.hash(rawRefreshToken);

        // Revoke refresh token
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(refreshTokenRepository::delete);

        // Delete cookie
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)          // ← maxAge=0 để xóa cookie
                .build();
        httpResponse.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ApiResponse.<Void>builder()
                .success(true)
                .message("Logout successfully")
                .data(null)
                .build();
    }

    public UUID getAccountIdByRefreshToken(String rawRefreshToken) {
        String tokenHash = tokenHashUtil.hash(rawRefreshToken);
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .map(rt -> rt.getAccount().getAccountId())  // ← DB có account_id!
                .orElse(null);
    }


}
