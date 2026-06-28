# Phase 1 Deep Dive — Core Authentication

## Modules 1-6 | Package: `phase1.coreauth`

### What
Register, Login, JWT, Refresh, Spring Security, RBAC

### Why Separate Package
Reusable auth core — other projects copy `phase1` + `shared` only

### Key Files
- `AuthService.java` — hub calling Phase 2-7
- `JwtService.java` — manual JWT (see DEEP-DIVE/JWT-LINE-BY-LINE.md)
- `JwtAuthenticationFilter.java` — every request

### Scenario: First API Call After Register
User registers → must verify email (Phase 2) → login → gets JWT → calls /api/auth/me
