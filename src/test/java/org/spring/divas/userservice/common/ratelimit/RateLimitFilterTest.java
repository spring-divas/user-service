package org.spring.divas.userservice.common.ratelimit;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.spring.divas.userservice.common.exception.GlobalExceptionHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RateLimitFilterTest {
    @Test
    void allowsRequestsAndSkipsActuatorEndpoints() throws Exception {
        RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
        RateLimitFilter filter = new RateLimitFilter(
                registry, mock(HandlerExceptionResolver.class));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);

        MockHttpServletRequest actuator = new MockHttpServletRequest("GET", "/actuator/health");
        filter.doFilter(actuator, new MockHttpServletResponse(), chain);
        assertThat(actuator.getAttribute(RateLimitFilter.RATE_LIMITER_ATTRIBUTE)).isNull();
    }

    @Test
    void resolvesDeniedRequestsAndFallsBackTo429WhenResolverDoesNotHandle() throws Exception {
        RateLimiterRegistry registry = RateLimiterRegistry.of(RateLimiterConfig.custom()
                .limitForPeriod(1)
                .limitRefreshPeriod(Duration.ofSeconds(2))
                .timeoutDuration(Duration.ZERO)
                .build());
        HandlerExceptionResolver resolver = mock(HandlerExceptionResolver.class);
        RateLimitFilter filter = new RateLimitFilter(registry, resolver);
        FilterChain chain = mock(FilterChain.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/users");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        MockHttpServletRequest deniedRequest = new MockHttpServletRequest("GET", "/users");
        filter.doFilter(deniedRequest, response, chain);
        verify(resolver).resolveException(org.mockito.ArgumentMatchers.eq(deniedRequest),
                org.mockito.ArgumentMatchers.eq(response), org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.any());
        assertThat(response.getStatus()).isEqualTo(429);
    }

    @Test
    void exceptionHandlerCalculatesRetryAfterAndUsesDefaultWithoutLimiter() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        var request = new MockHttpServletRequest();
        var denied = io.github.resilience4j.ratelimiter.RequestNotPermitted
                .createRequestNotPermitted(RateLimiterRegistry.of(RateLimiterConfig.custom()
                        .limitRefreshPeriod(Duration.ofMillis(1100)).build()).rateLimiter("test"));
        request.setAttribute(RateLimitFilter.RATE_LIMITER_ATTRIBUTE,
                RateLimiterRegistry.of(RateLimiterConfig.custom()
                        .limitRefreshPeriod(Duration.ofMillis(1100)).build()).rateLimiter("test"));
        var response = handler.handleRateLimitExceeded(denied, request);
        assertThat(response.getStatusCode().value()).isEqualTo(429);
        assertThat(response.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("2");
        assertThat(response.getBody().getTitle()).isEqualTo("Too Many Requests");

        var noLimiter = handler.handleRateLimitExceeded(denied, new MockHttpServletRequest());
        assertThat(noLimiter.getHeaders().getFirst(HttpHeaders.RETRY_AFTER)).isEqualTo("1");
    }
}
