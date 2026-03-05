package com.oneClick.authService.features.oauth.entity;

import com.oneClick.authService.shared.domain.entity.Account;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Entity
@Table(name = "auth_oauth_identities",
        indexes = @Index(name = "idx_oauth_account", columnList = "account_id")
    )
public class OAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "oauth_id")
    private UUID oauthId;

    @Column(name = "account_id", nullable = false)
    private UUID accountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "provider", nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @PrePersist
    private void validate() {
        if (providerUserId == null || providerUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("providerUserId cannot be empty");
        }
    }
}
