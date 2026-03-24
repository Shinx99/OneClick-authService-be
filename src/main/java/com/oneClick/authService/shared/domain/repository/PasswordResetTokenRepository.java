package com.oneClick.authService.shared.domain.repository;

import com.oneClick.authService.shared.domain.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    // Xóa các token cũ khi user yêu cầu reset mới để tránh rác DB
    void deleteByAccountId(UUID accountId);
}
