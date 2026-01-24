package com.oneClick.authService_be;

import com.oneClick.authService_be.infrastructure.security.jwt.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class AuthServiceBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceBeApplication.class, args);
	}

}
