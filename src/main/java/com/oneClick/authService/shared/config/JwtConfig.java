package com.oneClick.authService.shared.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.oneClick.authService.shared.util.PemUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {

    private String secret;
    private String privateKeyPath = "/app/keys/private.pem";
    private Long accessTokenExpiration;
    private Long refreshTokenExpiration;
    private String issue;

    @Bean
    public JwtEncoder jwtEncoder() throws Exception {
        RSAPrivateKey privateKey = PemUtils.readPrivateKeyFromFile(privateKeyPath);

        // ✅ Extract PUBLIC từ PRIVATE (modulus + exponent)
        RSAPublicKey publicKey = PemUtils.extractPublicKey(privateKey);  // Method mới

        RSAKey rsaKey = new RSAKey.Builder(publicKey)  // ✅ Builder nhận PublicKey
                .privateKey(privateKey)  // Chain private components
                .keyID("auth-key")
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        JWKSource jwkSource = new ImmutableJWKSet<>(jwkSet);

        return new NimbusJwtEncoder(jwkSource);
    }

   /* @Bean
    public JwtDecoder jwtDecoder() throws Exception {
        // DÙNG PUBLIC.PEM RIÊNG (bạn có sẵn) - KHÔNG cần extract
        RSAPublicKey publicKey = PemUtils.readPublicKeyFromFile("/app/keys/public.pem");

        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }*/
   @Bean
   public JwtDecoder jwtDecoder() throws Exception {
       try {
           // ✅ ƯU TIÊN public.pem
           return NimbusJwtDecoder.withPublicKey(PemUtils.readPublicKeyFromFile("/app/keys/public.pem")).build();
       } catch (Exception e1) {
           log.warn("public.pem not found, extracting from private.pem: {}", e1.getMessage());
           // ✅ FALLBACK extract từ private.pem
           RSAPrivateKey privateKey = PemUtils.readPrivateKeyFromFile("/app/keys/private.pem");
           RSAPublicKey publicKey = PemUtils.extractPublicKey(privateKey);
           return NimbusJwtDecoder.withPublicKey(publicKey).build();
       }
   }

}
