package com.oneClick.authService.features.login.controller;

import com.oneClick.authService.features.login.dto.request.LoginRequest;
import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.features.login.handler.LoginHandler;
import com.oneClick.authService.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final LoginHandler loginHandler;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        return ResponseEntity.ok(loginHandler.login(request, httpRequest, httpResponse));
    }

}
