package com.oneClick.authService.features.oauth.handler;

import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.features.login.mapper.LoginMapper;
import com.oneClick.authService.features.oauth.entity.OAuth;
import com.oneClick.authService.features.oauth.repository.OAuthRepository;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.RefreshToken;
import com.oneClick.authService.shared.domain.entity.Role;
import com.oneClick.authService.shared.domain.entity.Session;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.domain.repository.RefreshTokenRepository;
import com.oneClick.authService.shared.domain.repository.RoleRepository;
import com.oneClick.authService.shared.domain.repository.SessionRepository;
import com.oneClick.authService.shared.dto.ApiResponse;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import com.oneClick.authService.shared.security.jwt.JwtTokenProvider;
import com.oneClick.authService.shared.util.TokenHashUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OAuthHandler {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final OAuthRepository oAuthRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenHashUtil tokenHashUtil;
    private final LoginMapper loginMapper;

    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    public ApiResponse<LoginResponse> loginWithOAuth(String provider, String providerUserId, HttpServletRequest httpRequest, HttpServletResponse httpResponse){

        // 1. Load account with OAuth ID
        Account account = null;
        if (oAuthRepository.existsByProviderAndProviderUserId(provider, providerUserId)) {

            UUID accountId = oAuthRepository.findAccountIdByProviderAndProviderUserId(provider, providerUserId);
            account = accountRepository.findById(accountId).orElse(null);
            log.info("OAuth login for existing accountId: {}", account.getAccountId());

        }

        if(account == null) {

            // Create new GuestAccount = create accountGuest + link to OAUth
            account = createGuestAccount(providerUserId);

            // Link new accountGuest to OAuth
            OAuth oAuth = OAuth.builder()
                    .accountId(account.getAccountId())
                    .account(account)
                    .provider(provider)
                    .providerUserId(providerUserId)
                    .build();
            oAuthRepository.save(oAuth);
        }

        // 2 Load roles from account
        List<String> roles = account.getRoles().stream()
                .map(Role::getRoleName)
                .toList();

        // 3. Save session
        Session session = sessionRepository.save(Session.builder()
                .account(account)
                .ip(httpRequest.getRemoteAddr())
                .userAgent(httpRequest.getHeader("User-Agent"))
                .build());

        // 4. Generate access and refresh token
        String accessToken = jwtTokenProvider.generateAccessToken(
                account.getAccountId().toString(),
                roles);
        String rawRefreshToken = jwtTokenProvider.generateRefreshToken(
                account.getAccountId().toString());

        // 5. Save refresh token into db
        refreshTokenRepository.save(RefreshToken.builder()
                .session(session)
                .account(account)
                .tokenPrefix(rawRefreshToken.substring(0, 8))
                .tokenHash(tokenHashUtil.hash(rawRefreshToken))
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpiry()))
                .build());

        // 6. Set refresh token into httpOnly cookie
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
                .message("Login with OAUth successful")
                .data(response)
                .build();
    }

    // Helper createGuestAccount
    private Account createGuestAccount(String providerUserId){

        // Find role for entity
        Role candidateRole = roleRepository.findById((short) 2)
                .orElseThrow(() -> new RuntimeException("Role 'candidate' not found"));

        // Build accountGuest and save into db
        Account guest = Account.builder()
                .email("oauth_" + providerUserId + "@guest.com")
                .status("active")
                .build();
        guest.getRoles().add(candidateRole);
        return accountRepository.save(guest);
    }

}
