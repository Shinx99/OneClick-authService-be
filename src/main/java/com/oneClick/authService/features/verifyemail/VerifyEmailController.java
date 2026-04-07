package com.oneClick.authService.features.verifyemail;

import com.oneClick.authService.features.verifyemail.dto.VerifyEmailRequest;
import com.oneClick.authService.features.verifyemail.dto.VerifyEmailResponse;
import com.oneClick.authService.shared.audit.AuditContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class VerifyEmailController {

   private final VerifyEmailHandler verifyEmailHandler;

   @PostMapping("/verify-email")
   public ResponseEntity<VerifyEmailResponse> verifyEmail(
           @RequestBody VerifyEmailRequest request,
           HttpServletRequest httpServletRequest
           ){
       String ip = getClientIpAddress(httpServletRequest);
       String userAgent = httpServletRequest.getHeader("User-Agent");

       log.info("Verify email request for token: {}", request.getToken());

       String token = request.getToken();  // ← khai báo token, xử lý việc lấy account id cho auditlog
       UUID accountId = verifyEmailHandler.getAccountIdByVerifyToken(token);
       if (accountId != null) {
           AuditContextHolder.setCurrentAccountId(accountId);
           log.debug("VerifyEmail SET AuditContext EARLY: {}", accountId);
       }

       VerifyEmailResponse response = verifyEmailHandler.handle(request, ip, userAgent);

       return ResponseEntity.ok(response);

   }

    private String getClientIpAddress(HttpServletRequest httpServletRequest) {
       String xForwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
       if(xForwardedFor != null && !xForwardedFor.isEmpty()){
        return xForwardedFor.split(",")[0].trim();
       }

       return httpServletRequest.getRemoteAddr();

    }

}
