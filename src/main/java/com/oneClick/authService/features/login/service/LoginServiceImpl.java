package com.oneClick.authService.features.login.service;

import com.oneClick.authService.features.login.dto.request.LoginRequest;
import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.features.login.mapper.LoginMapper;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.Role;
import com.oneClick.authService.shared.domain.entity.Session;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.domain.repository.RefreshTokenRepository;
import com.oneClick.authService.shared.domain.repository.SessionRepository;
import com.oneClick.authService.shared.dto.ApiResponse;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import com.oneClick.authService.shared.exception.UnauthorizedException;
import com.oneClick.authService.shared.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    //private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    //private final SessionRepository sessionRepository;
    //private final RefreshTokenRepository refreshTokenRepository;
    private final LoginMapper loginMapper;

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest request){

        // 1. Verify credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );

        // 2. Load Account
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        // 3. Generate access token
        String accessToken = jwtTokenProvider.generateAccessToken(
                account.getAccountId().toString(),
                List.of()  // ← roles trống tạm
        );

        LoginResponse response = loginMapper.toLoginResponse(account, accessToken, jwtTokenProvider.getAccessTokenExpiry());

        return ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(response)
                .build();
    }
}
