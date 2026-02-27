package com.oneClick.authService.shared.repository;

// shared/repository/AuditLogRepository.java

import com.oneClick.authService.shared.entity.AuditLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    // Recent events for user
    @Query("SELECT a FROM AuditLog a WHERE a.accountId = :accountId ORDER BY a.createdAt DESC")
    List<AuditLog> findRecentByAccountId(UUID accountId, Pageable pageable);

    // Security events (failed login...)
    @Query("SELECT a FROM AuditLog a WHERE a.eventType LIKE 'LOGIN%' AND a.accountId = :accountId ORDER BY a.createdAt DESC")
    List<AuditLog> findSecurityEvents(UUID accountId);

    // Admin dashboard
    @Query(value = "SELECT event_type, COUNT(*) FROM auth_audit_logs WHERE created_at > NOW() - INTERVAL '24 hours' GROUP BY event_type", nativeQuery = true)
    List<Object[]> countEventsLast24h();
}
