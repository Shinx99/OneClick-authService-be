package com.oneClick.authService.features.login.controller;

import com.oneClick.authService.features.login.dto.request.LoginRequest;
import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.features.login.service.LoginService;
import com.oneClick.authService.shared.dto.ApiResponse;
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

    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity.ok(loginService.login(request));
    }

}
