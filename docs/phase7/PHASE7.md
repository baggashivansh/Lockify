# Phase 7 - Security Beast Mode (`phase7/hardening/`)

Modules 23-27: Token rotation, blacklist, security events, fingerprinting, adaptive auth.

**Package:** `com.lockify.phase7.hardening`

**Key services:**
- `TokenRotationService` — token families, reuse attack detection
- `TokenBlacklistService` + `TokenBlacklistFilter` — forced logout
- `AdaptiveAuthService` — new device → MFA recommend

**APIs:** `/api/security/events` (admin)
