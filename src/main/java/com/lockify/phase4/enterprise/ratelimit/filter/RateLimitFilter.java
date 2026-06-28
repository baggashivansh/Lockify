package com.lockify.phase4.enterprise.ratelimit.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lockify.phase4.enterprise.ratelimit.config.RateLimitProperties;
import com.lockify.phase4.enterprise.ratelimit.exception.RateLimitExceededException;
import com.lockify.phase4.enterprise.ratelimit.service.RateLimitService;
import com.lockify.shared.dto.ApiErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

/**
 * Auth endpoints pe rate limiting lagata hai - brute force attacks slow ho jate hain.
 */
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;
    private final RateLimitProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (properties.isEnabled() && properties.getAuthEndpoints().contains(path)) {
            String clientKey = resolveClientKey(request);
            try {
                rateLimitService.checkRateLimit(clientKey, path);
            } catch (RateLimitExceededException ex) {
                writeTooManyRequests(response, request.getRequestURI(), ex.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeTooManyRequests(HttpServletResponse response, String path, String message) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
