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

    // Find Token Hash
    Optional<RefreshToken> findByTokenHash(String tokenHash);


}
