package com.oneClick.authService.shared.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_email_verify_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailVerificationToken {

    @Id
    @Column(name = "verify_id")
    UUID verifyId;

    @Column(name = "account_id", nullable = false)
    UUID accountId;

    @Column(name = "token_hash", nullable = false)
    String tokenHash;

    @Column(name = "expires_at", nullable = false)
    Instant expiresAt;

    @Column(name = "used_at")
    Instant usedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    Instant createdAt;
}
