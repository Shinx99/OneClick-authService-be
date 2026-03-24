package com.oneClick.authService.features.changepassword;

import com.oneClick.authService.shared.security.CustomUserDetail.UserPrincipal;
import com.oneClick.authService.features.changepassword.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ChangePasswordController {
    private final ChangePasswordHandler changePasswordHandler;

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@AuthenticationPrincipal Jwt jwt, @RequestBody ChangePasswordRequest request) {

        String subject = jwt.getSubject();
        UUID currentAccountId = UUID.fromString(subject);

        changePasswordHandler.handle(currentAccountId, request);
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
}
