# Lockify - Phase-Based Folder Structure

Har phase ka **alag package** hai — kisi bhi project me sirf woh phase copy kar sakte ho jo chahiye.

---

## Project Tree

```
src/main/java/com/lockify/
│
├── LockifyApplication.java          # Entry point - sab phases scan hote hain
│
├── shared/                          # Sab phases me common
│   ├── config/                      # SecurityConfig, JwtProperties, Redis, Password
│   ├── domain/
│   │   ├── entity/                  # User, Role, Permission (core domain)
│   │   └── repository/
│   ├── dto/                         # ApiErrorResponse
│   ├── exception/                   # GlobalExceptionHandler
│   └── util/                        # TokenHasher, ClientInfoExtractor
│
├── phase1/coreauth/                 # PHASE 1 - Core Authentication
│   ├── controller/                  # /api/auth/*, /api/user/*, /api/admin/*
│   ├── service/                     # AuthService (hub - phases ko call karta hai)
│   ├── entity/                      # RefreshToken
│   ├── repository/
│   ├── dto/
│   ├── mapper/
│   └── security/                    # JWT filter, UserDetails
│       └── jwt/
│
├── phase2/account/                  # PHASE 2 - Account Security
│   ├── config/                      # AccountSecurityProperties
│   ├── controller/                  # /api/account/*
│   ├── service/                     # Email verify, password reset, lock, policy
│   ├── entity/                      # EmailVerification, PasswordReset, etc.
│   ├── repository/
│   └── dto/
│
├── phase3/session/                  # PHASE 3 - Session Management
│   ├── controller/                  # /api/sessions/*, /api/devices/*
│   ├── service/                     # Session tracking, logout, devices
│   ├── entity/                      # UserSession, TrustedDevice
│   ├── repository/
│   └── dto/
│
├── phase4/enterprise/               # PHASE 4 - Enterprise Security
│   ├── audit/                       # Audit logs + @Auditable aspect
│   ├── ratelimit/                   # Redis rate limiting filter
│   └── mfa/                         # TOTP + Email OTP
│
├── phase5/oauth/                      # PHASE 5 - OAuth2 & SSO
│   ├── config/                      # OAuth2ClientConfig (Google/GitHub)
│   ├── controller/                  # /api/oauth2/*
│   ├── service/
│   ├── entity/                      # OAuthAccount
│   └── repository/
│
├── phase6/authorization/            # PHASE 6 - Advanced Authorization
│   ├── controller/                  # /api/resources/*
│   ├── service/                     # Resource ownership
│   ├── evaluator/                   # ABAC policy engine
│   ├── entity/                      # UserResource, UserAttribute, AbacPolicy
│   ├── annotation/                  # @OwnResource
│   └── dto/
│
└── phase7/hardening/                # PHASE 7 - Security Beast Mode
    ├── filter/                      # TokenBlacklistFilter
    ├── controller/                  # /api/security/*
    ├── service/                     # Rotation, events, fingerprint, adaptive
    ├── entity/
    └── repository/
```

---

## Database Migrations (Flyway)

| File | Phase |
|------|-------|
| `V1__init_schema.sql` | Phase 1 - users, roles, refresh_tokens |
| `V2__phase2_account_security.sql` | Phase 2 |
| `V3__phase3_session_management.sql` | Phase 3 |
| `V4__phase4_enterprise_security.sql` | Phase 4 |
| `V5__phase5_oauth.sql` | Phase 5 |
| `V6__phase6_authorization.sql` | Phase 6 |
| `V7__phase7_hardening.sql` | Phase 7 |

---

## API Map by Phase

### Phase 1 — `/api/auth`, `/api/user`, `/api/admin`
### Phase 2 — `/api/account`
### Phase 3 — `/api/sessions`, `/api/devices`
### Phase 4 — `/api/mfa` + audit (internal) + rate limit (filter)
### Phase 5 — `/api/oauth2`, `/login/oauth2/code/*`
### Phase 6 — `/api/resources`
### Phase 7 — `/api/security`

---

## Kisi Aur Project Me Ek Phase Copy Karna

1. `shared/` folder copy karo (minimum requirement)
2. Sirf chahiye wala `phaseN/` folder copy karo
3. Us phase ki Flyway migration run karo
4. `application.yml` me us phase ki properties add karo
5. `SecurityConfig` me public endpoints add karo (agar naye public APIs hon)

Example: Sirf Phase 2 chahiye?
→ Copy `shared/` + `phase1/coreauth/` (auth hub) + `phase2/account/`

---

## Cross-Phase Flow

```
Register (Phase 1)
  → Email verify token (Phase 2)
  → Audit log (Phase 4)

Login (Phase 1)
  → Account lock check (Phase 2)
  → Session create (Phase 3)
  → Audit (Phase 4)
  → Fingerprint + adaptive MFA flag (Phase 7)

Refresh (Phase 1)
  → Token rotation with families (Phase 7)
```

---

## Docs Per Phase

| Phase | Doc |
|-------|-----|
| 1 | [phase1/PHASE1.md](./phase1/PHASE1.md) |
| 2 | [phase2/PHASE2.md](./phase2/PHASE2.md) |
| 3 | [phase3/PHASE3.md](./phase3/PHASE3.md) |
| 4 | [phase4/PHASE4.md](./phase4/PHASE4.md) |
| 5 | [phase5/PHASE5.md](./phase5/PHASE5.md) |
| 6 | [phase6/PHASE6.md](./phase6/PHASE6.md) |
| 7 | [phase7/PHASE7.md](./phase7/PHASE7.md) |
