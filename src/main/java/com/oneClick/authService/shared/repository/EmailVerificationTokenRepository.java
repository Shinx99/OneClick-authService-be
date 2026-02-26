package com.oneClick.authService.shared.repository;

import com.oneClick.authService.shared.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, UUID> {

    /**
     * Tìm token theo hash (secure lookup)
     */
    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    /**
     * Tìm token chưa dùng của account
     */
    Optional<EmailVerificationToken> findFirstByAccountIdAndUsedAtIsNullOrderByCreatedAtDesc(Long accountId);

    /**
     * Cleanup expired tokens (cron job)
     */
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") Instant now);

    /**
     * Count unused tokens per account (prevent spam)
     */
    @Query("SELECT COUNT(t) FROM EmailVerificationToken t WHERE t.accountId = :accountId AND t.usedAt IS NULL")
    long countUnusedTokensByAccountId(@Param("accountId") Long accountId);
}
