package com.oneClick.authService.shared.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_password_credentials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordCredential {

    @Id
    @Column(name = "account_id")
    private UUID accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "password_algo", nullable = false, length = 20)
    @Builder.Default
    private String passwordAlgo = "bcrypt";

    @UpdateTimestamp
    @Column(name = "password_updated_at", nullable = false)
    private Instant passwordUpdatedAt;


}
