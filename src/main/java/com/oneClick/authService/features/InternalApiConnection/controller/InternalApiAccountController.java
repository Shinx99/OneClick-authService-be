package com.oneClick.authService.features.InternalApiConnection.controller;

import com.oneClick.authService.features.InternalApiConnection.dto.InternalAccountDto;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalApiAccountController {

    private final AccountRepository accountRepository;

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<InternalAccountDto> getAccount(@PathVariable UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        return ResponseEntity.ok(InternalAccountDto.from(account));
    }

    @PutMapping("/accounts/{accountId}/status")
    public ResponseEntity<Void> updateAccountStatus(@PathVariable UUID accountId,
                                                    @RequestParam String status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        account.setStatus(status);
        accountRepository.save(account);
        return ResponseEntity.ok().build();
    }

}
