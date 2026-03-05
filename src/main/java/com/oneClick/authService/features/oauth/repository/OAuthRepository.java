package com.oneClick.authService.features.oauth.repository;

import com.oneClick.authService.features.oauth.entity.OAuth;
import com.oneClick.authService.shared.domain.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuthRepository extends JpaRepository<OAuth, UUID> {

    // Find by provider and providerUserId
    @Query("SELECT o.account.accountId FROM OAuth o WHERE o.provider = :provider AND o.providerUserId = :providerUserId")
    UUID findAccountIdByProviderAndProviderUserId(@Param("provider") String provider,
                                                           @Param("providerUserId") String providerUserId);

    // Check exist by provider and providerUserId
    Boolean existsByProviderAndProviderUserId(@Param("provider") String provider, @Param("providerUserId") String providerUserId);

    // Find all OAuth of 1 account
    @Query("SELECT o FROM OAuth o WHERE o.accountId = :accountId")
    List<OAuth> findByAccountId(@Param("accountId") UUID accountId);

    // Delete OAuth of 1 account (cleanup when delete account)
    @Modifying
    @Query("DELETE FROM OAuth oi WHERE oi.account.accountId = :accountId")
    void deleteByAccountId(@Param("accountId") UUID accountId);

    // Check linked provider
    default boolean isProviderLinked(UUID accountId, String provider){
        return findByAccountId(accountId).stream().anyMatch(o -> o.getProvider().equalsIgnoreCase(provider));
    }
}
