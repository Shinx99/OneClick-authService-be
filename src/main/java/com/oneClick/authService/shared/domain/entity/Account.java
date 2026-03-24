package com.oneClick.authService.shared.domain.entity;

import com.oneClick.authService.shared.util.PasswordUtil;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "auth_accounts")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false)
    private UUID accountId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "active";

    @Column(name = "email_verified_at")
    private Instant emailVerifiedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PasswordCredential passwordCredential;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "auth_accounts_roles",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    public void setPasswordCredential(String plainPassword){
        this.passwordCredential = PasswordCredential.builder()
                .account(this)
                .passwordHash(PasswordUtil.hashPassword(plainPassword))
                .build();
    }

    public boolean checkPassword(String plainPassword) {
        // Kiểm tra nếu passwordCredential chưa được khởi tạo
        if (this.passwordCredential == null || this.passwordCredential.getPasswordHash() == null) {
            return false;
        }

        // Sử dụng PasswordUtil để so sánh mật khẩu thô với hash trong DB
        // Giả định PasswordUtil của bạn có method verify hoặc matches
        return PasswordUtil.verifyPassword(plainPassword, this.passwordCredential.getPasswordHash());
    }

}
