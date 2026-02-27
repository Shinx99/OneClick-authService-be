package com.oneClick.authService.shared.domain.repository;

import com.oneClick.authService.shared.domain.entity.RefreshToken;
import com.oneClick.authService.shared.domain.entity.Session;
import jakarta.transaction.Transactional;
import jdk.dynalink.linker.LinkerServices;
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
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    // Revoke refresh token for 1 particular session
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revokedAt = :now WHERE r.session.sessionId = :sessionId AND r.revokedAt IS NULL")
    int revokedAllBySessionId(@Param("sessionId") UUID sessionId, @Param("now") Instant now);

    // Revoke refresh token for 1 particular account
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revokedAt = :now WHERE r.account.accountId = :accountId AND r.revokedAt IS NULL")
    int revokedAllByAccountId(@Param("accountId") UUID accountId, @Param("now") Instant now);

}
