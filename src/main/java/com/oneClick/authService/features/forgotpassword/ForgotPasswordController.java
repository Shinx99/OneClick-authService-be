package com.oneClick.authService.features.forgotpassword;

import com.oneClick.authService.features.forgotpassword.dto.ForgotPasswordRequest;
import com.oneClick.authService.features.forgotpassword.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ForgotPasswordController {
    private final ForgotPasswordHandler forgotPasswordHandler;

    @PostMapping("/forgot-password")
    public ResponseEntity<String> request(@RequestBody ForgotPasswordRequest request) {
        forgotPasswordHandler.requestReset(request);
        return ResponseEntity.ok("Yêu cầu đã được ghi nhận. Vui lòng kiểm tra email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> reset(@RequestBody ResetPasswordRequest request) {
        forgotPasswordHandler.resetPassword(request);
        return ResponseEntity.ok("Mật khẩu đã được thay đổi thành công.");
    }
}
