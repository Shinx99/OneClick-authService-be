package com.oneClick.authService.shared.domain.repository;

import com.oneClick.authService.shared.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Account> findById(UUID id);

    @Query("SELECT a FROM Account a JOIN FETCH a.roles WHERE a.email = :email")
    Optional<Account> findByEmailWithRole(String email);

    Optional<Account> findByAccountId(UUID accountId);

}
