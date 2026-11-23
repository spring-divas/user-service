package org.spring.divas.userservice.common.exception;

import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.TestJwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
class GlobalExceptionHandlerIntegrationTest {

    private static final String UNKNOWN_USER_LOGIN_JSON =
            """
            {"email": "missing@example.com", "password": "password123"}
            """;

    private static final String INVALID_REGISTRATION_JSON =
            """
            {"firstName": "", "lastName": "User",
             "email": "not-an-email", "password": "short"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithUnknownEmailReturnsUnauthorizedProblemDetail() throws Exception {
        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(UNKNOWN_USER_LOGIN_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Authentication Failed"))
                .andExpect(jsonPath("$.detail").value("Invalid username or password"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void invalidRegistrationReturnsValidationErrors() throws Exception {
        mockMvc.perform(post("/user/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(INVALID_REGISTRATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.firstName").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void missingUserReturnsNotFoundProblemDetail() throws Exception {
        mockMvc.perform(get("/user/{id}", 999_999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource Not Found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
