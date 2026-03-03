package com.oneClick.authService.features.register;

import com.oneClick.authService.features.register.dto.RegisterRequest;
import com.oneClick.authService.features.register.dto.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegisterController {

    public final RegisterHandler registerHandler;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletRequest httpServletRequest
            ){
        String ip = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        RegisterResponse response = registerHandler.handle(request, ip, userAgent);
        return ResponseEntity.ok(response);
    }

}
