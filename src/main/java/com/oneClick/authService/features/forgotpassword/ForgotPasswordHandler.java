package com.oneClick.authService.features.forgotpassword;

import com.oneClick.authService.features.forgotpassword.dto.ForgotPasswordRequest;
import com.oneClick.authService.features.forgotpassword.dto.ResetPasswordRequest;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.PasswordResetToken;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.domain.repository.PasswordResetTokenRepository;
import com.oneClick.authService.shared.exception.BusinessException;
import com.oneClick.authService.shared.notification.EmailService;
import com.oneClick.authService.shared.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordHandler {
    private final AccountRepository accountRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Transactional
    public void requestReset(ForgotPasswordRequest request) {
        // 1. Kiểm tra tài khoản tồn tại
        Account account = accountRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException("Email không tồn tại", HttpStatus.NOT_FOUND, "EMAIL_NOT_FOUND"));

        // 2. Tạo plain token và hash
        String plainToken = UUID.randomUUID().toString();
        String tokenHash = PasswordUtil.sha256(plainToken);

        // 3. Xóa các yêu cầu reset cũ của user này và lưu mới
        tokenRepository.deleteByAccountId(account.getAccountId());
        tokenRepository.flush();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .accountId(account.getAccountId())
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES)) // Hết hạn sau 30p
                .build();
        tokenRepository.save(resetToken);

        // 4. Gửi mail (Sử dụng hạ tầng EmailService bạn đã có)
        emailService.sendPasswordResetEmail(account.getEmail(), account.getEmail(), plainToken, 30);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String incomingTokenHash = PasswordUtil.sha256(request.token());

        // 1. Tìm token trong DB
        PasswordResetToken resetToken = tokenRepository.findByTokenHash(incomingTokenHash)
                .orElseThrow(() -> new BusinessException("Token không hợp lệ", HttpStatus.BAD_REQUEST, "INVALID_TOKEN"));

        // 2. Kiểm tra tính hợp lệ (đã dùng chưa? hết hạn chưa?)
        if (resetToken.getUsedAt() != null) {
            throw new BusinessException("Token này đã được sử dụng", HttpStatus.BAD_REQUEST, "TOKEN_ALREADY_USED");
        }
        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            throw new BusinessException("Token đã hết hạn", HttpStatus.BAD_REQUEST, "TOKEN_EXPIRED");
        }

        // 3. Tìm account và cập nhật mật khẩu mới
        Account account = accountRepository.findById(resetToken.getAccountId())
                .orElseThrow(() -> new BusinessException("Tài khoản không tồn tại", HttpStatus.NOT_FOUND, "ACCOUNT_NOT_FOUND"));

        PasswordUtil.validatePasswordStrength(request.newPassword());

        if (account.getPasswordCredential() != null) {
            // Nếu đã có mật khẩu cũ, chỉ đổi nội dung của nó
            account.getPasswordCredential().setPasswordHash(PasswordUtil.hashPassword(request.newPassword()));
            account.getPasswordCredential().setPasswordUpdatedAt(Instant.now());
        } else {
            // Nếu tài khoản chưa bao giờ có mật khẩu (ví dụ login Google), mới tạo mới
            account.setPasswordCredential(request.newPassword());
        }
        accountRepository.save(account);

        // 4. Đánh dấu đã dùng và gửi mail xác nhận
        resetToken.setUsedAt(Instant.now());
        tokenRepository.save(resetToken);

        emailService.sendPasswordChangedConfirmation(account.getEmail(), account.getEmail());
    }
}
