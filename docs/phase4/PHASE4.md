# Phase 4 - Enterprise Security (`phase4/enterprise/`)

Modules 14-16: Audit logging, rate limiting, MFA (TOTP + Email OTP).

**Sub-packages:**
- `audit/` — @Auditable aspect, AuditService
- `ratelimit/` — Redis RateLimitFilter
- `mfa/` — TotpService (manual RFC 6238), MfaController

**APIs:** `/api/mfa/setup`, `/api/mfa/verify`, `/api/mfa/enable`
