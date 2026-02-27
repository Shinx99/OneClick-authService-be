package com.oneClick.authService.shared.security.CustomUserDetail;

import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    // -> Login
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        log.debug("Loading user details for: {}", email);
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return new UserPrincipal(account);
    }

    // -> JWT Filter (transfer UUID) + @Cacheble
    @Cacheable(value = "userPrincipal", key = "#userId")
    @Transactional(readOnly = true)
    public UserDetails loadUserById(String userId){
        log.debug("Loading user by id: {}", userId);
        UUID accountId = UUID.fromString(userId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        return new UserPrincipal(account);
    }

    // -> ban User + switch Role + logout
    @CacheEvict(value = "userPrincipal", key = "#userId")
    public void evictUserCache(String userId){
        log.debug("Evicting cache for user: {}", userId);
    }


}
