package com.oneClick.authService.features.verifyemail;

import com.oneClick.authService.features.verifyemail.dto.VerifyEmailRequest;
import com.oneClick.authService.features.verifyemail.dto.VerifyEmailResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
