package com.oneClick.authService_be.infrastructure.notification.email;

import com.oneClick.authService_be.application.port.output.EmailPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailAdapter implements EmailPort {

    private final EmailService emailService;

    @Override
    public void sendVerificationEmail(String toEmail, String username, String verificationToken, int expiryHours) {

        log.info("[EMAIL_ADAPTER] Sending verification email to: {}", toEmail);
        emailService.sendVerificationEmail(toEmail, username,
                verificationToken, expiryHours);

    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String username, String resetToken, int expiryMinutes) {

        log.info("[EMAIL_ADAPTER] Sending password reset email to: {}", toEmail);
        emailService.sendPasswordResetEmail(toEmail, username,
                resetToken, expiryMinutes);
    }

    @Override
    public void sendOtpEmail(String toEmail, String username, String otpCode, int expiryMinutes) {
        log.info("[EMAIL_ADAPTER] Sending OTP email to: {}", toEmail);
        emailService.sendOtpEmail(toEmail, username, otpCode, expiryMinutes);
    }

    @Override
    public void sendPasswordChangedConfirmation(String toEmail, String username) {
        log.info("[EMAIL_ADAPTER] Sending password changed confirmation to: {}", toEmail);
        emailService.sendPasswordChangedConfirmation(toEmail, username);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username) {
        log.info("[EMAIL_ADAPTER] Sending welcome email to: {}", toEmail);
        emailService.sendWelcomeEmail(toEmail, username);
    }

    @Override
    public void sendSuspiciousLoginAlert(String toEmail, String username, String ipAddress, String location) {
        log.warn("[EMAIL_ADAPTER] Sending suspicious login alert to: {}", toEmail);
        emailService.sendSuspiciousLoginAlert(toEmail, username,
                ipAddress, location);

    }
}
