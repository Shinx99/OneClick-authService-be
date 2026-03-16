package com.oneClick.authService.shared.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class PemUtils {

    // Đọc PRIVATE KEY (PKCS#8, header -----BEGIN PRIVATE KEY-----)
    public static RSAPrivateKey readPrivateKeyFromFile(String path) throws Exception {
        String pem = Files.readString(Paths.get(path))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(pem);

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) kf.generatePrivate(spec);
    }

    // Nếu bạn có file public riêng (tùy chọn, nếu muốn dùng trực tiếp thay vì extract)
    public static RSAPublicKey readPublicKeyFromFile(String path) throws Exception {
        String pem = Files.readString(Paths.get(path))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(pem);

        java.security.spec.X509EncodedKeySpec spec =
                new java.security.spec.X509EncodedKeySpec(decoded);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);
        return (RSAPublicKey) publicKey;
    }

    // Extract PUBLIC từ PRIVATE (cần key type RSAPrivateCrtKey để lấy publicExponent)
    public static RSAPublicKey extractPublicKey(RSAPrivateKey privateKey) {
        if (!(privateKey instanceof RSAPrivateCrtKey)) {
            throw new IllegalArgumentException("Private key is not RSAPrivateCrtKey, cannot extract public exponent");
        }

        RSAPrivateCrtKey crtKey = (RSAPrivateCrtKey) privateKey;

        RSAPublicKeySpec spec = new RSAPublicKeySpec(
                crtKey.getModulus(),
                crtKey.getPublicExponent()
        );

        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) kf.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Cannot extract public key", e);
        }
    }
}
