package com.oneClick.authService.features.InternalApiConnection.dto;

import com.oneClick.authService.shared.domain.entity.Account;
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
    private Set<String> roles;


    public static InternalAccountDto from(Account account) {
        InternalAccountDto dto = new InternalAccountDto();
        dto.setAccountId(account.getAccountId());
        dto.setEmail(account.getEmail());
        dto.setPhone(account.getPhone());
        dto.setStatus(account.getStatus());
        dto.setRoles(
                account.getRoles().stream()
                        .map(Role::getRoleName) // hoặc .map(Role::getName)
                        .collect(Collectors.toSet())
        );
        return dto;
    }
}
