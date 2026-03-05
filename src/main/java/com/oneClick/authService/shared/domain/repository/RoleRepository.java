package com.oneClick.authService.shared.domain.repository;

import com.oneClick.authService.shared.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Short> {

    Optional<Role> findByRoleName(String roleName);



}
