package com.oneClick.authService.features.login.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.security.SecureRandom;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email is required!")
    @Email(message = "Invalid email format!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;

}
