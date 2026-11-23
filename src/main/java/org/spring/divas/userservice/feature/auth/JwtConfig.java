package org.spring.divas.userservice.feature.auth;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import static org.spring.divas.userservice.common.readers.RsaKeyReader.readPrivateKey;
import static org.spring.divas.userservice.common.readers.RsaKeyReader.readPublicKey;

@Configuration
@Profile("!test")
public class JwtConfig {

    @Value("${security.jwt.private-key-path}")
    private String privateKeyPath;

    @Value("${security.jwt.public-key-path}")
    private String publicKeyPath;

    @Bean
    public JwtEncoder jwtEncoder()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PrivateKey privateKey = readPrivateKey(privateKeyPath);
        RSAKey rsaKey = new RSAKey.Builder(publicKey())
                .privateKey(privateKey)
                .keyID("auth-key-1")
                .build();
        JWKSource<SecurityContext> jwkSource =
                new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public RSAPublicKey publicKey()
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return (RSAPublicKey) readPublicKey(publicKeyPath);
    }
}