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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginMapper loginMapper;

    public ApiResponse<LoginResponse> login(LoginRequest request, HttpServletRequest httpRequest){

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
                        .tokenHash(passwordEncoder.encode(rawRefreshToken))
                        .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiry()))
                .build());

        // 7. Build response
        LoginResponse response = loginMapper.toLoginResponse(account, session, accessToken, rawRefreshToken, jwtTokenProvider.getAccessTokenExpiry());


        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();
    }
}
