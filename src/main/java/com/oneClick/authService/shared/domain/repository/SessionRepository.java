package com.oneClick.authService.shared.domain.repository;

import com.oneClick.authService.shared.domain.entity.Session;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {

    // Find Session Active (not be revoked) by sessionId
    @Query("SELECT s FROM Session s WHERE s.sessionId = :sessionId AND s.revokedAt IS NULL")
    Optional<Session> findActiveById(@Param("sessionId") UUID sessionId);

    // Find all Active Session of 1 Account (Logined equiment management)
    @Query("SELECT s FROM Session s WHERE s.account.accountId = :accountId AND s.revokedAt IS NULL")
    List<Session> findActiveByAccountId(@Param("accountId") UUID accountId);

    // Revoked 1 particular Session of account
    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.revokedAt = :now WHERE s.sessionId = :sessionId AND s.revokedAt IS NULL")
    int revokedById(@Param("sessionId") UUID sessionId, @Param("now") Instant now);

    // Revoked all Sessions of account\
    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.revokedAt = :now WHERE s.account.accountId = :accountId AND s.revokedAt IS NULL")
    int revokedAllByAccountId(@Param("accountId") UUID accountId, @Param("now") Instant now);

    // Update last_seen_at (use when request successfully overcome filter)
    @Modifying
    @Transactional
    @Query("UPDATE Session s SET s.lastSeenAt = :now WHERE s.sessionId = :sessionId ")
    void updateLastSeen(@Param("sessionId") UUID sessionId, @Param("now") Instant now);


}
