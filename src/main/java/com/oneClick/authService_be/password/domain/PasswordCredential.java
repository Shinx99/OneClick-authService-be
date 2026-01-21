package com.oneClick.authService_be.password.domain;

import com.oneClick.authService_be.account.domain.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "auth_password_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordCredential {

    @Id
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Column(name = "password_algo", nullable = false,length = 20)
    @Builder.Default
    private String passwordAlgo = "bcrypt";

    @UpdateTimestamp
    @Column(name = "password_updated_at", nullable = false)
    private Instant passwordUpdatedAt;
}
