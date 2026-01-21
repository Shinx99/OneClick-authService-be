package com.oneClick.authService_be.role.domain;

import com.oneClick.authService_be.account.domain.Account;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "auth_accounts_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRole {

    @EmbeddedId
    private AccountRoleId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("accountId")
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountRoleId implements Serializable{

        @Column(name = "account_id", nullable = false)
        private Long accountId;

        @Column(name = "role_id", nullable = false)
        private Short roleId;
    }
}
