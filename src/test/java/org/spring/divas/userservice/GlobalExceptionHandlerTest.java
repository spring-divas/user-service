package org.spring.divas.userservice;

import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.common.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNotFound_shouldReturn404() {
        ProblemDetail problem = handler.handleNotFound(new NoSuchElementException("User not found"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problem.getTitle()).isEqualTo("Resource Not Found");
        assertThat(problem.getDetail()).isEqualTo("User not found");
        assertThat(problem.getProperties()).containsKey("timestamp");
    }

    @Test
    void handleBadCredentials_shouldReturn401() {
        ProblemDetail problem = handler.handleBadCredentials(new BadCredentialsException("Invalid password"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
        assertThat(problem.getTitle()).isEqualTo("Authentication Failed");
        assertThat(problem.getDetail()).isEqualTo("Invalid password");
    }

    @Test
    void handleAccessDenied_shouldReturn403() {
        ProblemDetail problem = handler.handleAccessDenied(new AccessDeniedException("Forbidden"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(problem.getTitle()).isEqualTo("Access Denied");
    }

    @Test
    void handleGenericException_shouldReturn500() {
        ProblemDetail problem = handler.handleGenericException(new RuntimeException("Something unexpected"));

        assertThat(problem.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(problem.getTitle()).isEqualTo("Internal Server Error");
    }
}
