//infrastructure/not/notification/email/EmailTemplateService.java
package com.oneClick.authService_be.infrastructure.notification.email;

/*
* Process Thymeleaf templates
* Build template variables
* Return HTML strings
* */

public interface EmailTemplateService {
    String buildVerificationEmail(String username, String verifyUrl, int expiryHours, String supportUrl);

    String buildPasswordResetEmail(String username, String resetUrl, int expiryMinutes, String supportUrl);

    String buildOtpEmail(String username, String otpCode, int expiryMinutes, String supportUrl);

    String buildPasswordChangedEmail(String username, String ipAddress, String securityUrl);

    String buildWelcomeEmail(String username, String dashboardUrl, String docsUrl);

    String buildSuspiciousLoginEmail(String username, String ipAddress, String location, String changePasswordUrl);
}
