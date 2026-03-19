package com.oneClick.authService.features.InternalApiConnection.dto;

import com.oneClick.authService.shared.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalAccountDto {

    private UUID accountId;
    private String email;
    private String phone;
    private String status;
    private List<String> roles;


    public InternalAccountDto(UUID accountId, String email, String phone, String status, Set<Role> roles) {
        this.accountId = accountId;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.roles = roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toList());
    }
}
