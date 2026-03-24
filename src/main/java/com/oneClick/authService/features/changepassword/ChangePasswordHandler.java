package com.oneClick.authService.features.changepassword;


import com.oneClick.authService.features.changepassword.dto.ChangePasswordRequest;
import com.oneClick.authService.features.changepassword.dto.ChangePasswordResponse;
import com.oneClick.authService.shared.domain.entity.Account;
import com.oneClick.authService.shared.domain.repository.AccountRepository;
import com.oneClick.authService.shared.exception.BusinessException;
import com.oneClick.authService.shared.security.CustomUserDetail.CustomUserDetailsService;
import com.oneClick.authService.shared.security.CustomUserDetail.UserPrincipal;
import com.oneClick.authService.shared.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.pulsar.PulsarProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChangePasswordHandler {

    private final AccountRepository accountRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @Transactional
    public void handle(UUID accountId, ChangePasswordRequest request) {

        // 1. Chặn đứng ngay nếu ID bị null (Người dùng chưa xác thực hoặc JWT lỗi)
        if (accountId == null) {
            log.error("Không thể trích xuất Account ID từ Security Context");
            throw new BusinessException("Vui lòng đăng nhập để thực hiện chức năng này", HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }

        // 1. Tìm account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BusinessException("User not found", HttpStatus.NOT_FOUND, "NOT_FOUND"));

        // 2. Kiểm tra mật khẩu cũ (Sử dụng method checkPassword bạn đã viết ở Entity Account)
        if (!account.checkPassword(request.oldPassword())) {
            throw new BusinessException("Mật khẩu cũ không chính xác", HttpStatus.BAD_REQUEST, "INVALID_OLD_PASSWORD");
        }

        // 3. Validate độ mạnh mật khẩu mới (Dùng hàm có sẵn trong PasswordUtil của bạn)
        PasswordUtil.validatePasswordStrength(request.newPassword());

        // 5. Cập nhật mật khẩu mới (Chống lỗi Hibernate Session "A different object with the same identifier...")
        if (account.getPasswordCredential() != null) {
            // Chỉ ghi đè thay đổi trường hash, không thay thế hoàn toàn instance đối tượng
            account.getPasswordCredential().setPasswordHash(PasswordUtil.hashPassword(request.newPassword()));
            account.getPasswordCredential().setPasswordUpdatedAt(Instant.now());
        } else {
            // Chỉ tạo mới instance khi tài khoản gốc chưa từng có credential (VD: Login qua Google)
            account.setPasswordCredential(request.newPassword());
        }

        accountRepository.save(account);
        log.info("Đổi mật khẩu thành công cho accountId: {}", accountId);
    }
}
