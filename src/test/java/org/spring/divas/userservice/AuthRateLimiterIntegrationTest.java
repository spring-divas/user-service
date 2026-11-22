package org.spring.divas.userservice;

import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.feature.auth.AuthService;
import org.spring.divas.userservice.feature.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.net.URI;

import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "resilience4j.ratelimiter.instances.authApi.limit-for-period=3",
        "resilience4j.ratelimiter.instances.authApi.limit-refresh-period=1m",
        "resilience4j.ratelimiter.instances.authApi.timeout-duration=0s",
        "resilience4j.ratelimiter.instances.userApi.limit-for-period=1",
        "resilience4j.ratelimiter.instances.userApi.limit-refresh-period=4500ms",
        "resilience4j.ratelimiter.instances.userApi.timeout-duration=0s"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
class AuthRateLimiterIntegrationTest {

    private static final String AUTH_REQUEST_JSON =
            """
            {"email": "user@example.com", "password": "password123"}
            """;

    private static final String REGISTRATION_JSON =
            """
            {"firstName": "Test", "lastName": "User",
             "email": "user@example.com", "password": "password123"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldRejectLoginAttemptsWithProblemDetailWhenRateLimitIsExceeded() throws Exception {
        when(authService.authenticate(any())).thenReturn("token");

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(login()).andExpect(status().isOk());
        }

        mockMvc.perform(login())
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(HttpHeaders.RETRY_AFTER, "60"))
                .andExpect(jsonPath("$.status").value(429))
                .andExpect(jsonPath("$.title").value("Too Many Requests"));

        mockMvc.perform(encodedLogin())
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(HttpHeaders.RETRY_AFTER, "60"));

        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().is(not(HttpStatus.TOO_MANY_REQUESTS.value())));
    }

    @Test
    void shouldRoundUpRetryAfterToRefreshPeriodOfExhaustedLimiter() throws Exception {
        mockMvc.perform(register()).andExpect(status().isOk());

        mockMvc.perform(register())
                .andExpect(status().isTooManyRequests())
                .andExpect(header().string(HttpHeaders.RETRY_AFTER, "5"));

        mockMvc.perform(get("/actuator-other"))
                .andExpect(status().isTooManyRequests());
    }

    private MockHttpServletRequestBuilder login() {
        return post("/auth/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(AUTH_REQUEST_JSON);
    }

    private MockHttpServletRequestBuilder encodedLogin() {
        return post(URI.create("/%61uth/token"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(AUTH_REQUEST_JSON);
    }

    private MockHttpServletRequestBuilder register() {
        return post("/user/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(REGISTRATION_JSON);
    }
}
