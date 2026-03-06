package com.oneClick.authService.features.register;

import com.oneClick.authService.features.register.dto.RegisterRequest;
import com.oneClick.authService.features.register.dto.RegisterResponse;
import com.oneClick.authService.shared.audit.AuditContext;
import com.oneClick.authService.shared.audit.AuditContextHolder;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.entity.Role;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.domain.repository.RoleRepository;
import com.oneClick.authService.shared.entity.EmailVerificationToken;
import com.oneClick.authService.shared.exception.BusinessException;
import com.oneClick.authService.shared.notification.EmailService;
import com.oneClick.authService.shared.repository.EmailVerificationTokenRepository;
import com.oneClick.authService.shared.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterHandler {

    private  final AccountRepository accountRepository;
    private  final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;
    private final RoleRepository roleRepository;

    @Transactional
    public RegisterResponse handle(RegisterRequest request, String ip, String userAgent){

        //1. check email exists

        if(accountRepository.existsByEmail(request.email())){
            throw new BusinessException("Email already registered", HttpStatus.CONFLICT, "EMAIL_EXISTS");
        }

        //2. Create account + PasswordCredential
        Account account = Account.builder()
                .email(request.email().trim().toLowerCase())
                .status("pending")
                .phone(request.phone())
                .build();

        //set password
        account.setPasswordCredential(request.password());

        //set role
        Role candidateRole = roleRepository.findByRoleName("candidate")
                .orElseThrow(()-> new BusinessException("Default role 'candidate' not found",
                        HttpStatus.INTERNAL_SERVER_ERROR,"ROLE_NOT_FOUND"));

        account.getRoles().add(candidateRole);

        account = accountRepository.save(account);

        // 3.Create verification token (Email 1)
        String plainToken = UUID.randomUUID().toString();
        String tokenHash = PasswordUtil.sha256(plainToken);

        var token = new EmailVerificationToken(
                null,
                account.getAccountId(),
                tokenHash,
                Instant.now().plus(24, ChronoUnit.HOURS),
                null,
                Instant.now()
        );
        tokenRepository.save(token);

        AuditContextHolder.setCurrentAccountId(account.getAccountId());


        //4. Send email 1: verify link
        emailService.sendVerificationEmail(
                account.getEmail(),
                account.getEmail(),
                plainToken,
                24
        );

        return new RegisterResponse(account.getAccountId(), "Registration successful, please verify your email");
    }


}
