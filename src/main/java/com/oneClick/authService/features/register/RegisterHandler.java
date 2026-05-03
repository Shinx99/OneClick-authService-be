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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegisterHandler {

    private  final AccountRepository accountRepository;
    private  final EmailVerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final RoleRepository roleRepository;

    @Transactional
    public RegisterResponse handle(RegisterRequest request, String ip, String userAgent) {

        String email = request.email().trim().toLowerCase();
        log.info("Register attempt for email: {}", email);

        Account existingAccount = accountRepository.findByEmail(email).orElse(null);

        if (existingAccount != null) {
            if ("active".equals(existingAccount.getStatus())) {
                throw new BusinessException("Email already registered", HttpStatus.CONFLICT, "EMAIL_EXISTS");
            }

            if ("pending".equals(existingAccount.getStatus())) {
                log.info("Found pending account for email: {}, updating information", email);

                // Kiểm tra token cũ còn hiệu lực không
                EmailVerificationToken oldToken = tokenRepository
                        .findByAccountId(existingAccount.getAccountId())
                        .orElse(null);

                if (oldToken != null && oldToken.getExpiresAt().isAfter(Instant.now()) && oldToken.getUsedAt() == null) {
                    log.warn("Token still valid for account: {}", existingAccount.getAccountId());
                    throw new BusinessException(
                            "Email already registered and verification link is still valid. Please check your email.",
                            HttpStatus.CONFLICT,
                            "EMAIL_PENDING"
                    );
                }

                // CẬP NHẬT ACCOUNT CŨ (KHÔNG XÓA)
                existingAccount.setPhone(request.phone());

                // UPDATE PASSWORD (KHÔNG TẠO MỚI)
                existingAccount.updatePassword(request.password());

                // Cập nhật roles
                Set<Role> roleEntities = getRolesFromRequest(request);
                existingAccount.setRoles(roleEntities);

                // Reset status
                existingAccount.setStatus("pending");
                existingAccount.setEmailVerifiedAt(null);

                Account updatedAccount = accountRepository.saveAndFlush(existingAccount);
                log.info("Updated existing account: {}", updatedAccount.getAccountId());

                // Xóa token cũ
                if (oldToken != null) {
                    tokenRepository.delete(oldToken);
                    tokenRepository.flush();
                    log.info("Deleted old token for account: {}", updatedAccount.getAccountId());
                }

                // Tạo token mới
                String plainToken = UUID.randomUUID().toString();
                String tokenHash = PasswordUtil.sha256(plainToken);

                EmailVerificationToken newToken = new EmailVerificationToken(
                        null,
                        updatedAccount.getAccountId(),
                        tokenHash,
                        Instant.now().plus(24, ChronoUnit.HOURS),
                        null,
                        Instant.now()
                );
                tokenRepository.saveAndFlush(newToken);
                log.info("Created new token for updated account");

                // Gửi email
                emailService.sendVerificationEmail(
                        updatedAccount.getEmail(),
                        updatedAccount.getEmail(),
                        plainToken,
                        24
                );

                AuditContextHolder.setCurrentAccountId(updatedAccount.getAccountId());

                return new RegisterResponse(updatedAccount.getAccountId(),
                        "Registration updated successfully, please verify your email with the new link");
            }
        }

        // Tạo account mới
        log.info("Creating new account for email: {}", email);

        Set<Role> roleEntities = getRolesFromRequest(request);

        Account account = Account.builder()
                .email(email)
                .status("pending")
                .phone(request.phone())
                .roles(roleEntities)
                .build();

        // TẠO PASSWORD CHO ACCOUNT MỚI
        account.setPasswordCredential(request.password());

        account = accountRepository.saveAndFlush(account);
        log.info("New account created: accountId={}", account.getAccountId());

        // Tạo token
        String plainToken = UUID.randomUUID().toString();
        String tokenHash = PasswordUtil.sha256(plainToken);

        EmailVerificationToken token = new EmailVerificationToken(
                null,
                account.getAccountId(),
                tokenHash,
                Instant.now().plus(24, ChronoUnit.HOURS),
                null,
                Instant.now()
        );
        tokenRepository.saveAndFlush(token);

        // Gửi email
        emailService.sendVerificationEmail(
                account.getEmail(),
                account.getEmail(),
                plainToken,
                24
        );

        AuditContextHolder.setCurrentAccountId(account.getAccountId());

        return new RegisterResponse(account.getAccountId(),
                "Registration successful, please verify your email");
    }

    private Set<Role> getRolesFromRequest(RegisterRequest request) {
        Set<Role> roleEntities = new HashSet<>();
        for (String roleName : request.roles()) {
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> new BusinessException("Role '" + roleName + "' not found",
                            HttpStatus.INTERNAL_SERVER_ERROR, "ROLE_NOT_FOUND"));
            roleEntities.add(role);
        }
        return roleEntities;
    }
}
