package com.oneClick.authService_be.application.port.output;

/*
* Output Port cho Email Service (Hexagonal Architechture)
* Domain/Application layer phụ thuộc vào interface này
* Infrastructure layer implement interface này
* */

public interface EmailPort {

    void sendVerificationEmail(String toEmail, String username,
                               String verificationToken, int expiryHours);

    void sendPasswordResetEmail(String toEmail, String username,
                                String resetToken, int expiryMinutes);

    void sendOtpEmail(String toEmail, String username,
                      String otpCode, int expiryMinutes);

    void sendPasswordChangedConfirmation(String toEmail, String username);

    void sendWelcomeEmail(String toEmail, String username);

    void sendSuspiciousLoginAlert(String toEmail, String username,
                                  String ipAddress, String location);


}
