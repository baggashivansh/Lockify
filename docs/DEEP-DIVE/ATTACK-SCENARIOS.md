# Attack Scenarios — Defense Matrix

| # | Attack | Defense | File/Table |
|---|--------|---------|------------|
| 1 | SQL Injection | JPA parameterized queries | All repositories |
| 2 | Password DB steal | Pepper + BCrypt | user_credentials, PepperedPasswordEncoder |
| 3 | Brute force login | Rate limit + account lock | RateLimitFilter, AccountLockService |
| 4 | User enumeration | Same error message | AuthService, GlobalExceptionHandler |
| 5 | JWT tampering | HMAC signature | JwtService.constantTimeEquals |
| 6 | JWT timing attack | Constant-time compare | JwtService |
| 7 | Expired token reuse | exp claim check | JwtService |
| 8 | Refresh token DB leak | SHA-256 hash only | refresh_tokens.token_hash |
| 9 | Refresh token reuse | Token family revoke | TokenRotationService |
| 10 | Stolen access token | Short 15min expiry + blacklist | TokenBlacklistService |
| 11 | XSS steal token | Security headers CSP | SecurityHeadersFilter |
| 12 | Clickjacking | X-Frame-Options DENY | SecurityHeadersFilter |
| 13 | Credential stuffing | Rate limit + lock | Phase 2 + 4 |
| 14 | Password reuse | password_history table | PasswordPolicyService |
| 15 | Weak password | @Pattern validation | RegisterRequest |
| 16 | MITM | HSTS header (HTTPS) | SecurityHeadersFilter |
| 17 | Session hijack | Session tracking + revoke | SessionTrackingService |
| 18 | New device login | Fingerprint + adaptive MFA | DeviceFingerprintService |
| 19 | OAuth account takeover | Provider ID unique constraint | oauth_accounts |
| 20 | Privilege escalation | @PreAuthorize + JWT roles | SecurityConfig, controllers |

## Scenario Walkthrough: Credential Stuffing

**Attack:** 10,000 email+password pairs from leaked site tried on Lockify.

**Defense chain:**
1. RateLimitFilter: 10 req/min per IP → most blocked
2. AccountLockService: 5 fails → 30min lock per account
3. login_attempts table: audit trail for security team
4. security_events: suspicious activity logged (Phase 7)
5. Generic error: attacker can't tell which emails exist

## Scenario: Insider DB Access

DBA has PostgreSQL access but NOT application env:
- Sees BCrypt hashes in user_credentials
- Does NOT have PASSWORD_PEPPER
- Cannot compute valid hashes for known passwords
- Cannot use refresh token hashes (one-way SHA-256)
