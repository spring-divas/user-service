package org.spring.divas.userservice.feature.user;

import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.TestJwtConfig;
import org.spring.divas.userservice.common.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.security.interfaces.RSAPublicKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "admin.email=bootstrap-admin@feedo.com",
        "admin.password=admin-password"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
class AdminInitializerIntegrationTest {

    private static final String ADMIN_LOGIN_JSON =
            """
            {"email": "bootstrap-admin@feedo.com", "password": "admin-password"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RSAPublicKey publicKey;

    @Test
    void shouldCreateAdminThatCanLogInWithAdminRole() throws Exception {
        User admin = userRepository.findByEmail("bootstrap-admin@feedo.com").orElseThrow();
        assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(passwordEncoder.matches("admin-password", admin.getPassword())).isTrue();

        String token = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ADMIN_LOGIN_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Jwt jwt = NimbusJwtDecoder.withPublicKey(publicKey).build().decode(token);
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("ADMIN");
    }
}
