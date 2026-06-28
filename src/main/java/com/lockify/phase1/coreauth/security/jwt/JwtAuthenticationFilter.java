package com.lockify.phase1.coreauth.security.jwt;

import com.lockify.shared.exception.TokenException;
import com.lockify.phase1.coreauth.security.LockifyUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter - har protected request pe chalta hai.
 *
 * Flow:
 * 1. Authorization header se Bearer token nikalo
 * 2. Token validate karo (signature + expiry)
 * 3. SecurityContext me authentication set karo
 * 4. Agli filter/controller ko request forward karo
 *
 * Agar token nahi hai to filter skip - public endpoints ke liye OK hai.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Token nahi hai ya already authenticated hai - aage badho
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)
                || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(BEARER_PREFIX.length());
            JwtClaims claims = jwtService.validateAndParse(token);

            // Sirf access token se API access allowed - refresh token se nahi
            if (!"access".equals(claims.getTokenType())) {
                throw new TokenException("Access token required hai");
            }

            LockifyUserDetails userDetails = new LockifyUserDetails(
                    claims.getUserId(),
                    claims.getUsername(),
                    claims.getRoles(),
                    claims.getPermissions()
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // SecurityContext me user set - ab @PreAuthorize kaam karega
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenException ex) {
            // Invalid token - context clear karke 401 bhejne ke liye entry point handle karega
            SecurityContextHolder.clearContext();
            request.setAttribute("jwt_error", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
