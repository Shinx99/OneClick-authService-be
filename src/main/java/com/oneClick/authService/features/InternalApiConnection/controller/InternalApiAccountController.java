package com.oneClick.authService.features.InternalApiConnection.controller;

import com.oneClick.authService.features.InternalApiConnection.dto.InternalAccountDto;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalApiAccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<InternalAccountDto> getAccount(@PathVariable UUID accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        return ResponseEntity.ok(new InternalAccountDto(
                account.getAccountId(),
                account.getEmail(),
                account.getPhone(),
                account.getStatus(),
                account.getRoles()
        ));
    }

}
