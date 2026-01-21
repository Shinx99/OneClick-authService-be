package com.oneClick.authService_be.role.domain;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "auth_roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_roles_role_name", columnNames = "role_name")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Short roleId;

    @Column(name = "role_name", nullable = false, length = 50, unique = true)
    private String roleName;
}
