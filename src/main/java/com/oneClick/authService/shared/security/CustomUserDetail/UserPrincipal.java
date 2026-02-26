package com.oneClick.authService.shared.security.CustomUserDetail;

import com.oneClick.authService.shared.domain.entity.Account;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String password;
    private final String status;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Account account){
        this.id = account.getAccountId();
        this.email = account.getEmail();
        this.password = account.getPasswordCredential().getPasswordHash();
        this.status = account.getStatus();

        if(account.getRoles() != null){
            this.authorities = account.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                    .collect(Collectors.toList());
        } else {
            this.authorities = Collections.emptyList();
        }
    }

    public UUID getId(){return id;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){return this.authorities;}

    @Override
    public String getPassword(){return this.password;}

    @Override
    public String getUsername() {return this.email;}

    @Override
    public boolean isAccountNonExpired(){return true;}

    @Override
    public boolean isAccountNonLocked() {return !"banned".equals(this.status);}

    @Override
    public boolean isCredentialsNonExpired(){return true;}

    @Override
    public boolean isEnabled() {return "active".equals(this.status);}


}
