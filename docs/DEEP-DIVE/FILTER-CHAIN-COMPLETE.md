# Filter Chain — Request Security Journey

## Order (outermost first)

```
1. SecurityHeadersFilter     → X-Frame-Options, HSTS, CSP
2. RateLimitFilter           → Redis sliding window (Phase 4)
3. TokenBlacklistFilter      → Revoked JWT check (Phase 7)
4. JwtAuthenticationFilter   → Bearer token parse (Phase 1)
5. Spring Security           → URL + @PreAuthorize rules
```

## Scenario: POST /api/auth/login

```
Client → SecurityHeaders (add headers) → RateLimit (count++) 
→ No JWT needed → AuthController → AuthService
→ AccountLock check → AuthenticationManager → BCrypt verify
→ Session create → JWT issue → Audit log → 200 + tokens
```

## Scenario: GET /api/admin/dashboard (USER role)

```
Client + Bearer JWT → RateLimit OK → Blacklist OK 
→ JWT valid, roles=[USER] → Spring Security URL OK
→ @PreAuthorize ADMIN required → 403 Forbidden
```

## Files
- `shared/security/SecurityHeadersFilter.java`
- `phase4/enterprise/ratelimit/filter/RateLimitFilter.java`
- `phase7/hardening/filter/TokenBlacklistFilter.java`
- `phase1/coreauth/security/jwt/JwtAuthenticationFilter.java`
- `shared/config/SecurityConfig.java`
