package org.spring.divas.userservice.common.exception;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import jakarta.servlet.http.HttpServletRequest;
import org.spring.divas.userservice.common.ratelimit.RateLimitFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Duration;
import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final long DEFAULT_RETRY_AFTER_SECONDS = 1;

    @ExceptionHandler(RequestNotPermitted.class)
    public ResponseEntity<ProblemDetail> handleRateLimitExceeded(
            RequestNotPermitted ex,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Please try again later.");
        problem.setTitle("Too Many Requests");
        problem.setProperty("timestamp", Instant.now());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds(request)))
                .body(problem);
    }

    private static long retryAfterSeconds(HttpServletRequest request) {
        if (!(request.getAttribute(RateLimitFilter.RATE_LIMITER_ATTRIBUTE)
                instanceof RateLimiter rateLimiter)) {
            return DEFAULT_RETRY_AFTER_SECONDS;
        }
        Duration refreshPeriod = rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod();
        long seconds = refreshPeriod.getSeconds() + (refreshPeriod.getNano() > 0 ? 1 : 0);
        return Math.max(seconds, DEFAULT_RETRY_AFTER_SECONDS);
    }
}
