package com.oneClick.authService_be.web.devtools;

import com.oneClick.authService_be.infrastructure.notification.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dev/email-test")
@RequiredArgsConstructor
public class EmailTestController {
    
    private final EmailService emailService;
    
    @GetMapping("/verification")
    public String testVerification(@RequestParam String email) {
        emailService.sendVerificationEmail(email, "John Doe", "test-token-123", 24);
        return "Verification email sent to " + email;
    }
    
    @GetMapping("/password-reset")
    public String testPasswordReset(@RequestParam String email) {
        emailService.sendPasswordResetEmail(email, "John Doe", "reset-token-456", 60);
        return "Password reset email sent to " + email;
    }
    
    @GetMapping("/otp")
    public String testOtp(@RequestParam String email) {
        emailService.sendOtpEmail(email, "John Doe", "123456", 5);
        return "OTP email sent to " + email;
    }
    
    @GetMapping("/welcome")
    public String testWelcome(@RequestParam String email) {
        emailService.sendWelcomeEmail(email, "John Doe");
        return "Welcome email sent to " + email;
    }
}
