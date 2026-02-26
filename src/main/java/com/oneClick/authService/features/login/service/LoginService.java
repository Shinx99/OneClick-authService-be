package com.oneClick.authService.features.login.service;

import com.oneClick.authService.features.login.dto.request.LoginRequest;
import com.oneClick.authService.features.login.dto.response.LoginResponse;
import com.oneClick.authService.shared.dto.ApiResponse;

public interface LoginService {
    ApiResponse<LoginResponse> login(LoginRequest request);
}
