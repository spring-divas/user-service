package org.spring.divas.userservice.common.readers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyReader {

    private RsaKeyReader() {
        /* This utility class should not be instantiated */
    }


    public static PublicKey readPublicKey(String path)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource publicKeyResource = new FileSystemResource(path);
        try (var in = publicKeyResource.getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            String content = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] bytes = Base64.getDecoder().decode(content);
            return KeyFactory.getInstance("RSA")
                    .generatePublic(new X509EncodedKeySpec(bytes));
        }
    }

    public static PrivateKey readPrivateKey(String path)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Resource privateKeyResource = new FileSystemResource(path);
        try (var in = privateKeyResource.getInputStream()) {
            String pem = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            String content = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] bytes = Base64.getDecoder().decode(content);
            return KeyFactory.getInstance("RSA")
                    .generatePrivate(new PKCS8EncodedKeySpec(bytes));
        }
    }
}
