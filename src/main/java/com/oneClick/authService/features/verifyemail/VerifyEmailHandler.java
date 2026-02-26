package com.oneClick.authService.features.verifyemail;

import com.oneClick.authService.features.verifyemail.dto.VerifyEmailRequest;
import com.oneClick.authService.features.verifyemail.dto.VerifyEmailResponse;
import com.oneClick.authService.shared.entity.AccountStatus;
import com.oneClick.authService.shared.entity.AuditLog;
/*import com.oneClick.authService.shared.exception.InvalidTokenException;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import com.oneClick.authService.shared.exception.TokenExpiredException;
import com.oneClick.authService.shared.exception.TokenAlreadyUsedException;*/
import com.oneClick.authService.shared.notification.EmailService;
import com.oneClick.authService.shared.repository.AuditLogRepository;
import com.oneClick.authService.shared.repository.EmailVerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerifyEmailHandler {

    /*private final EmailVerificationTokenRepository tokenRepo;
    private final AccountRepository accountRepo;
    private final AuditLogRepository auditLogRepo;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public VerifyEmailResponse handle(VerifyEmailRequest request, String ip, String userAgent){

        // ✅ Fix 1: Hash raw token
        String rawToken = request.getToken().trim();
        String tokenHash = PasswordUtil.sha256(rawToken);  // Import PasswordUtil

        //1. find token ✅ Fix method + exception
        var token = tokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid verification token"));

        //2. check expired ✅ Fix field name
        if(token.getExpiresAt().isBefore(Instant.now())){  // expiredAt → expiresAt
            throw new TokenExpiredException("Verification token has expired");
        }

        //3. check used ✅ Fix comment
        if(token.getUsedAt() != null){
            throw new TokenAlreadyUsedException("Token already used");
        }

        //4. update account ✅ Fix method name
        var account = accountRepo.findById(token.getAccountId())  // findbyId → findById
                .orElseThrow(ResourceNotFoundException::new);

        account.setStatus(AccountStatus.ACTIVE);
        account.setEmailVerifiedAt(Instant.now());
        accountRepo.save(account);

        //5. mark token used
        token.setUsedAt(Instant.now());
        tokenRepo.save(token);

        //6. send welcome email ✅ Comment đúng
        emailService.sendWelcomeEmail(account.getEmail(), account.getAccountId());

        //7. audit log ✅ Fix event constructor
        eventPublisher.publishEvent(new EmailVerifiedEvent(  // Import EmailVerifiedEvent
                account.getAccountId(),
                ip,  // ✅ Đúng thứ tự
                userAgent,
                "{\"verified_at\": \"" + Instant.now() + "\"}"
        ));

        // ✅ Fix return
        return new VerifyEmailResponse(account.getAccountId(), "Email verified successfully");
    }*/
}

/*
Workflow thực tế (VerifyEmail)
text
1️⃣ POST /api/auth/verify-email
   ↓
2️⃣ VerifyEmailHandler.handle()
   ↓ business logic (update account, token...)
   ↓
3️⃣ publisher.publishEvent(new AuditEvent(...))
   ↓ Spring EventBus (in-memory, fast)
4️⃣ AuditEventListener.handleAuditEvent() ✴️ ASYNC
   ↓
5️⃣ AuditLog.logEvent(...) → auditLogRepo.save()
   ↓ DB INSERT (non-blocking)
6️⃣ Response 200 OK ngay lập tức ✅
* */
