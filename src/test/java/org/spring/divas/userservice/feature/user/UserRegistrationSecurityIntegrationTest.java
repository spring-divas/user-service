package org.spring.divas.userservice.feature.user;

import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.TestJwtConfig;
import org.spring.divas.userservice.feature.auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.jwt.enabled=true",
        "resilience4j.ratelimiter.instances.userApi.limit-for-period=100"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
class UserRegistrationSecurityIntegrationTest {

    private static final String REGISTRATION_JSON =
            """
            {"firstName": "Test", "lastName": "User",
             "email": "user@example.com", "password": "password123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    void anonymousUserCanRegisterAsCustomer() throws Exception {
        mockMvc.perform(register("/user/customer")).andExpect(status().isOk());
    }

    @Test
    void anonymousUserCannotRegisterAsAdminOrManager() throws Exception {
        mockMvc.perform(register("/user/admin")).andExpect(status().isUnauthorized());
        mockMvc.perform(register("/user/manager")).andExpect(status().isUnauthorized());
    }

    @Test
    void customerCannotCreateAdminOrManager() throws Exception {
        String token = token("CUSTOMER");

        mockMvc.perform(register("/user/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
        mockMvc.perform(register("/user/manager").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateAdminAndManager() throws Exception {
        String token = token("ADMIN");

        mockMvc.perform(register("/user/admin").header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
        mockMvc.perform(register("/user/manager").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private String token(String role) {
        return jwtService.generateAccessToken("1", List.of(role));
    }

    private MockHttpServletRequestBuilder register(String path) {
        return post(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTRATION_JSON);
    }
}
