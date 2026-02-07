package com.oneClick.authService.shared.config;

import com.oneClick.authService.shared.security.CustomAccessDeniedHandler;
import com.oneClick.authService.shared.security.JwtAuthenticationEntryPoint;
import com.oneClick.authService.shared.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        log.info("🔐 Configuring Security Filter Chain...");

        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        // ============================================
                        // PUBLIC ENDPOINTS (No authentication required)
                        // ============================================

                        // Actuator & Health
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        // API Documentation
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        // Auth endpoints
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/api/auth/refresh-token",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()

                        // Verification endpoints
                        .requestMatchers(
                                "/api/verification/verify-email",
                                "/api/verification/resend-email"
                        ).permitAll()

                        // OAuth endpoints
                        .requestMatchers(
                                "/api/oauth/**"
                        ).permitAll()

                        // Test endpoints (ONLY for development)
                        .requestMatchers(
                                "/api/test/**",
                                "/api/dev/**"
                        ).permitAll()

                        // ============================================
                        // PROTECTED ENDPOINTS (Authentication required)
                        // ============================================

                        // Admin endpoints require ADMIN role
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("Security Filter Chain configured successfully");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
