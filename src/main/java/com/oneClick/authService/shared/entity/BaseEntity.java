// shared/entity/BaseEntity.java
package com.oneClick.authService.shared.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass  // JPA inherit fields
@Getter
@Setter
public abstract class BaseEntity {

    @Id  // Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment
    @Column(name = "id")  // Tên column DB
    private Long id;

    @CreationTimestamp  // Auto set khi INSERT
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp  // Auto set khi UPDATE
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
