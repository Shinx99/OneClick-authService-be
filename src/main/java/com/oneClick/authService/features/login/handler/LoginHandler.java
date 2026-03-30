package com.oneClick.authService.features.login.handler;

import com.oneClick.authService.features.login.dto.request.LoginRequest;
import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.features.login.mapper.LoginMapper;
import com.oneClick.authService.shared.audit.AuditContext;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.RefreshToken;
import com.oneClick.authService.shared.domain.entity.Role;
import com.oneClick.authService.shared.domain.entity.Session;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.domain.repository.RefreshTokenRepository;
import com.oneClick.authService.shared.domain.repository.SessionRepository;
import com.oneClick.authService.shared.dto.ApiResponse;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import com.oneClick.authService.shared.security.jwt.JwtTokenProvider;
import com.oneClick.authService.shared.util.TokenHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginHandler {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final TokenHashUtil tokenHashUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginMapper loginMapper;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    public ApiResponse<LoginResponse> login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        // 1. Verify credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        // 2. Load Account
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // 3. Load roles from account
        List<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        // 4. Save Session
        Session session = sessionRepository.save(Session.builder()
                .account(account)
                .ip(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build());

        // 5. Generate access and refresh token
        String accessToken = jwtTokenProvider.generateAccessToken(
                account.getAccountId().toString(),
                roles
        );
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken(account.getAccountId().toString());

        // 6. Save refresh token into db
        refreshTokenRepository.save(RefreshToken.builder()
                        .session(session)
                        .account(account)
                        .tokenPrefix(rawRefreshToken.substring(0, 8))
                        .tokenHash(tokenHashUtil.hash(rawRefreshToken))
                        .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiry()))
                .build());

        // 7. Set refresh token into httpOnly cookie
        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", rawRefreshToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(jwtTokenProvider.getRefreshTokenExpiry() / 1000)
                .build();
        httpResponse.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // 7. Build response
        LoginResponse response = loginMapper.toLoginResponse(account, session, accessToken, jwtTokenProvider.getAccessTokenExpiry());


        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();
    }
}
