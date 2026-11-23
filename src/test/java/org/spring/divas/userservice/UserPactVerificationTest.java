package org.spring.divas.userservice;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Consumer;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.spring.divas.userservice.common.enums.UserRole;
import org.spring.divas.userservice.feature.user.UserResponseDto;
import org.spring.divas.userservice.feature.user.UserService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "server.servlet.context-path=/api"
)
@ActiveProfiles("test")
@Import(TestJwtConfig.class)
@Provider("user-service")
@Consumer("order-service")
@PactFolder("${pactfolder.path}")
class UserPactVerificationTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void configureTarget(PactVerificationContext context) {
        when(userService.findById(123L)).thenReturn(user());
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void verifiesPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    private UserResponseDto user() {
        return new UserResponseDto(
                123L, "Oksana", "Stepanenko", "oksana@example.com", UserRole.CUSTOMER);
    }
}
