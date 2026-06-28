# Architecture Guide

## High-Level Architecture

```text
                    ┌─────────────────────────────────────┐
                    │           Client (Web/Mobile)        │
                    └──────────────────┬──────────────────┘
                                       │ HTTP + Bearer JWT
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│                     CONTROLLER LAYER                              │
│  AuthController, UserController, AdminController                  │
│  → Request validate, Response return (NO business logic)          │
└──────────────────────────────┬───────────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────────┐
│                      SERVICE LAYER                                │
│  AuthService                                                      │
│  → Registration, Login, Token generation, Business rules          │
└──────────────────────────────┬───────────────────────────────────┘
                               │
              ┌────────────────┼────────────────┐
              ▼                ▼                ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│ SECURITY LAYER  │  │  JWT SERVICE    │  │ REPOSITORY LAYER│
│ Filter Chain    │  │  Manual JWT     │  │ JPA Repos       │
│ UserDetails     │  │  HMAC-SHA256    │  │                 │
└────────┬────────┘  └─────────────────┘  └────────┬────────┘
         │                                          │
         └──────────────────┬───────────────────────┘
                            ▼
                 ┌─────────────────────┐
                 │   PostgreSQL (Flyway)│
                 │   Redis (future)     │
                 └─────────────────────┘
```

---

## Clean Architecture Mapping

| Layer | Package | Responsibility |
|-------|---------|----------------|
| Presentation | `controller`, `dto` | HTTP, validation, serialization |
| Application | `service` | Use cases, orchestration |
| Domain | `entity` | Business entities |
| Infrastructure | `repository`, `security`, `config` | DB, framework, external |

**Rule:** Controller → Service → Repository. Kabhi Controller se direct Repository call mat karo.

---

## Security Architecture (Stateless JWT)

```text
LOGIN REQUEST
     │
     ▼
AuthenticationManager ──► Password verify (BCrypt)
     │
     ▼
JwtService.generateAccessToken()
     │
     ▼
Client stores accessToken + refreshToken

PROTECTED REQUEST
     │
     ▼
JwtAuthenticationFilter
     │── Extract "Bearer <token>"
     │── Validate signature + expiry
     │── Set SecurityContext
     ▼
Controller (@PreAuthorize checks role/permission)
```

### Kyun Stateless?

- **Scalable:** Koi server-side session store nahi — koi bhi server request handle kar sakta hai
- **Microservices friendly:** Token me roles/permissions hain — auth service alag ho sakti hai
- **Trade-off:** Token revoke karna mushkil (Phase 7 me blacklist aayega)

---

## Filter Chain Order

```
1. JwtAuthenticationFilter     → Token parse, SecurityContext set
2. UsernamePasswordAuthenticationFilter (bypass for JWT APIs)
3. Authorization filters       → hasRole, hasAuthority check
```

Public endpoints (`/api/auth/register`, `/login`, `/refresh`) filter se pehle `permitAll()` se bypass hote hain.

---

## Data Flow Example: Registration

```
POST /api/auth/register
  → AuthController.register()
  → @Valid RegisterRequest (Bean Validation)
  → AuthService.register()
      → Check duplicate email/username
      → BCrypt encode password
      → Assign USER role
      → userRepository.save()
  → UserMapper.toResponse() (password hide)
  → 201 Created + JSON
```

---

## Production Design Decisions

| Decision | Choice | Reason |
|----------|--------|--------|
| Password storage | BCrypt strength 12 | Industry standard, salted |
| Refresh token storage | SHA-256 hash in DB | DB leak pe tokens useless |
| JWT signing | HMAC-SHA256 manual | Learning + no extra dependency |
| Schema management | Flyway | Version controlled, reproducible |
| Open-in-view | false | Lazy loading bugs avoid |
| Error responses | Generic messages | Info leakage prevent |

---

## Extension Points (Future Phases)

- `RedisConfig` → Rate limiting, session cache
- `RefreshToken` entity → Token rotation (Phase 7)
- New tables in Flyway → `audit_logs`, `mfa_configurations`, etc.
- New filters → RateLimitFilter, AuditFilter
