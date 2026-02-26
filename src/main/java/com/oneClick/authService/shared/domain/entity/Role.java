package com.oneClick.authService.shared.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "auth_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Short roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<Account> accounts = new HashSet<>();

}
