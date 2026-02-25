package com.oneClick.authService.shared.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

// shared/entity/BaseAuditEntity.java
@MappedSuperclass
@Data
public abstract class BaseAuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;
    // KHÔNG có updatedAt!
}
