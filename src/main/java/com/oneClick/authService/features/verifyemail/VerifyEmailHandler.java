package com.oneClick.authService.features.verifyemail;

import com.oneClick.authService.features.verifyemail.dto.VerifyEmailRequest;
import com.oneClick.authService.features.verifyemail.dto.VerifyEmailResponse;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.entity.AccountStatus;
import com.oneClick.authService.shared.exception.BusinessException;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import com.oneClick.authService.shared.notification.EmailService;
import com.oneClick.authService.shared.repository.AuditLogRepository;
import com.oneClick.authService.shared.repository.EmailVerificationTokenRepository;
import com.oneClick.authService.shared.util.PasswordUtil;
import com.oneClick.authService.features.verifyemail.EmailVerifiedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 // VerifyEmailHandler.handle()
 1. Validate token ✓
 2. Activate account: status="active" ✓
 3. Mark token used ✓
 4. sendWelcomeEmail() → Email 2 (dashboard link) ✓ ← CHÍNH XÁC!
 5. Audit log ✓
 6. Return success ✓
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyEmailHandler {

    private final EmailVerificationTokenRepository tokenRepo;
    private final AccountRepository accountRepo;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public VerifyEmailResponse handle(VerifyEmailRequest request, String ip, String userAgent){

        String rawToken = request.getToken().trim();
        String tokenHash = PasswordUtil.sha256(rawToken);

        //1. Token validation
        var token = tokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BusinessException("Invalid verification token", HttpStatus.BAD_REQUEST, "INVALID_TOKEN"));

        if(token.getExpiresAt().isBefore(Instant.now())){
            throw new BusinessException("Verification token has expired", HttpStatus.BAD_REQUEST, "TOKEN_EXPIRED");
        }
        if(token.getUsedAt() != null){
            throw new BusinessException("Token already used", HttpStatus.BAD_REQUEST, "TOKEN_USED");
        }

        //2. Account activation
        var account = accountRepo.findById(token.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        account.setStatus("active");
        account.setEmailVerifiedAt(Instant.now());
        accountRepo.save(account);

        //3. Mark token used
        token.setUsedAt(Instant.now());
        tokenRepo.save(token);

        //4. Notifications email làm username
        emailService.sendWelcomeEmail(account.getEmail(), account.getEmail());

        eventPublisher.publishEvent(new EmailVerifiedEvent(
                account.getAccountId(),
                ip,
                userAgent,
                "{\"verified_at\": \"" + Instant.now() + "\"}"
        ));

        return new VerifyEmailResponse(account.getAccountId(), "Email verified successfully");
    }


}
/**
 1. NHẬN REQUEST
 Frontend gửi: GET /verify-email?token=abc123xyz

 2. HASH TOKEN (Security)
 rawToken="abc123xyz" → PasswordUtil.sha256() → "5e88...42d8" (64 chars)

 3. KIỂM TRA TOKEN (DB lookup)
 SELECT * FROM email_verification_token WHERE token_hash='5e88...42d8'
 → token.expiresAt > now() ✓
 → token.usedAt IS NULL ✓

 4. KÍCH HOẠT ACCOUNT
 UPDATE auth_accounts SET
 status='active',
 email_verified_at=NOW()
 WHERE account_id=token.account_id

 5. ĐÁNH DẤU TOKEN USED
 UPDATE email_verification_token SET used_at=NOW()

 6. NOTIFICATIONS
 ✅ Gửi welcome email (email làm username)
 ✅ AuditEvent: EMAIL_VERIFIED → auth_audit_logs

 7. RESPONSE
 { "accountId": "uuid", "message": "Email verified successfully" }

 TEST:
 1. POST /register → Nhận verification email
 2. Click link → GET /verify-email?token=...
 3. Check DB: status="active", audit_logs có "EMAIL_VERIFIED"
 4. Check inbox: Welcome email

 */