package com.oneClick.authService.features.jwk;

import com.oneClick.authService.shared.config.JwtConfig;
import com.oneClick.authService.shared.util.PemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class JwkController {

    private final JwtConfig jwtConfig;
    private final JwtEncoder jwtEncoder;

    @GetMapping("/oauth2/jwks")
    public Map<String, Object> jwks() throws Exception {
        RSAPublicKey publicKey = PemUtils.readPublicKeyFromFile(  // Hoặc extract từ private
                jwtConfig.getPrivateKeyPath().replace("private.pem", "public.pem"));

        // Spring native JWK JSON
        Map<String, Object> jwk = Map.of(
                "kty", "RSA",
                "kid", "auth-key",
                "alg", SignatureAlgorithm.RS256.getName(),
                "use", "sig",
                "n", java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray()),
                "e", java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray())
        );
        return Map.of("keys", List.of(jwk));
    }
}