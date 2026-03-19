package com.oneClick.authService.shared.config;

import com.oneClick.authService.shared.security.CustomAccessDeniedHandler;
import com.oneClick.authService.shared.security.CustomUserDetail.CustomUserDetailsService;
import com.oneClick.authService.shared.security.internalApi.InternalApiKeyFilter;
import com.oneClick.authService.shared.security.jwt.JwtAuthenticationEntryPoint;
import com.oneClick.authService.shared.util.PemUtils;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final JwtConfig jwtConfig;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CorsConfigurationSource corsConfigurationSource;
    private final InternalApiKeyFilter internalApiKeyFilter;

//    @Bean
//    public JwtDecoder jwtDecoder() {
//        RSAPublicKey publicKey;
//        try {
//            publicKey = PemUtils.readPublicKeyFromFile("/app/keys/public.pem");
//        } catch (Exception e) {
//            throw new IllegalStateException("Cannot load RS256 public key", e);
//        }
//        return NimbusJwtDecoder.withPublicKey(publicKey).build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring RS256 Security Filter Chain...");

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // ✅ RS256 NATIVE - THAY THẾ jwtAuthFilter
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC (giữ nguyên)
                        .requestMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/verification/**").permitAll()
                        .requestMatchers("/api/test/**", "/api/dev/**").permitAll()

                        // PROTECTED
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Expose public key for other service
                        .requestMatchers("/oauth2/jwks").permitAll()

                        // Internal API path to communicate with recruitment service
                        .requestMatchers("/api/internal/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(internalApiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());  // Giữ DAO cho /auth/login

        log.info("RS256 Security Filter Chain configured");
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // AuthenticationManager để dùng trong LoginService (để gọi authManager.authenticate())
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
