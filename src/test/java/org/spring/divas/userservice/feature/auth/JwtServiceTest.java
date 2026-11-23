package org.spring.divas.userservice.feature.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock private JwtEncoder jwtEncoder;
    @InjectMocks private JwtService service;

    @Test
    void generatesSignedAccessTokenWithIdentityRolesAndFifteenMinuteLifetime() {
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(
                Jwt.withTokenValue("signed-token")
                        .header("alg", "RS256")
                        .subject("42")
                        .claim("roles", List.of("CUSTOMER"))
                        .build());

        assertThat(service.generateAccessToken("42", List.of("CUSTOMER")))
                .isEqualTo("signed-token");

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);
        verify(jwtEncoder).encode(captor.capture());
        JwtClaimsSet claims = captor.getValue().getClaims();
        assertThat(claims.getClaims().get("iss")).isEqualTo("user-service");
        assertThat(claims.getSubject()).isEqualTo("42");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiresAt()).isEqualTo(claims.getIssuedAt().plusSeconds(900));
        assertThat(claims.getClaimAsStringList("roles")).containsExactly("CUSTOMER");
        assertThat(captor.getValue().getJwsHeader().getKeyId()).isEqualTo("auth-key-1");
        assertThat(captor.getValue().getJwsHeader().getAlgorithm().getName()).isEqualTo("RS256");

        Instant now = Instant.now();
        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(now);
    }
}
