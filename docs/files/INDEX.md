# File-by-File Guide (Har Source File Ka Kaam)

Yeh document har Java file explain karta hai — **kya karti hai** aur **kaise karti hai**.

---

## Root

### `LockifyApplication.java`
- **Kya:** Spring Boot entry point
- **Kaise:** `SpringApplication.run()` se saari auto-configuration start
- `@EnableConfigurationProperties(JwtProperties.class)` — JWT config bind

---

## config/ — Configuration Beans

### `JwtProperties.java`
- **Kya:** `application.yml` se JWT settings read karta hai
- **Fields:** secret, access expiry, refresh expiry, issuer
- **Production:** `JWT_SECRET` env var se override

### `SecurityConfig.java`
- **Kya:** Poora security filter chain define
- **Kaise:**
  - CSRF off (stateless REST)
  - Session STATELESS
  - Public vs protected URLs
  - JWT filter chain me add
  - `AuthenticationManager` bean

### `PasswordConfig.java`
- **Kya:** `BCryptPasswordEncoder` bean (strength 12)
- **Kaise:** Spring Security password hashing standard

### `RedisConfig.java`
- **Kya:** `StringRedisTemplate` bean
- **Future:** Rate limiting, session cache (Phase 2+)

---

## controller/ — HTTP Layer

### `AuthController.java`
- **Kya:** Auth REST endpoints
- **Endpoints:** register, login, refresh, me
- **Rule:** Sirf service call — koi business logic nahi

### `UserController.java`
- **Kya:** User-level protected endpoints demo
- **RBAC:** `@PreAuthorize` with roles/permissions

### `AdminController.java`
- **Kya:** Admin-only endpoints demo
- **RBAC:** ADMIN, SUPER_ADMIN, CREATE permission examples

---

## dto/ — Data Transfer Objects

### request/RegisterRequest.java
- Validation annotations — username, email, password rules

### request/LoginRequest.java
- identifier + password — flexible login

### request/RefreshTokenRequest.java
- refreshToken field

### response/UserResponse.java
- Safe user data — no password

### response/AuthResponse.java
- Tokens + user info after login/refresh

### response/ApiErrorResponse.java
- Standard error JSON structure

---

## entity/ — Database Models

### User.java
- JPA entity for `users` table
- ManyToMany with Role
- passwordHash, enabled, accountLocked flags

### Role.java / Permission.java
- RBAC core entities
- Enum mapping for name fields

### RefreshToken.java
- Opaque refresh token storage
- `isValid()` helper — revoked + expiry check

### RoleName.java / PermissionName.java
- Type-safe enums matching DB seed data

---

## exception/ — Error Handling

### LockifyException.java
- Base exception with HTTP status

### DuplicateResourceException.java — 409
### AuthenticationException.java — 401
### TokenException.java — 401 JWT errors

### GlobalExceptionHandler.java
- `@RestControllerAdvice` — catches all exceptions
- Consistent `ApiErrorResponse` return
- Validation, Security, generic errors handle

---

## mapper/

### UserMapper.java
- Entity → UserResponse conversion
- Password/ sensitive fields exclude

---

## repository/ — Data Access

### UserRepository.java
- findByEmail, findByUsername, exists checks
- findByEmailOrUsername — login flexibility

### RoleRepository.java
- findByName(RoleName)

### RefreshTokenRepository.java
- findByTokenHash
- revokeAllByUser — logout everywhere (future)

---

## security/ — Security Core

### LockifyUserDetails.java
- Spring `UserDetails` implementation
- Authorities from roles + permissions
- Two constructors: from User entity OR from JWT claims

### CustomUserDetailsService.java
- `loadUserByUsername` — login time DB se user load

### JwtAuthenticationEntryPoint.java
- 401 JSON response — no HTML error page

---

## security/jwt/

### JwtService.java
- **Manual JWT** generate + validate
- HMAC-SHA256 signing
- Opaque refresh token generation
- SHA-256 token hashing for DB

### JwtClaims.java
- Parsed token data holder

### JwtAuthenticationFilter.java
- `OncePerRequestFilter` — har request pe
- Bearer token extract → validate → SecurityContext set

---

## service/

### AuthService.java
- **Core business logic:**
  - `register()` — duplicate check, encode, save
  - `login()` — AuthenticationManager, tokens issue
  - `refresh()` — validate, revoke old, issue new
  - `getCurrentUser()` — profile fetch

---

## resources/

### application.yml
- DB, Redis, JWT, logging, actuator config

### application-test.yml
- Test profile overrides

### db/migration/V1__init_schema.sql
- Flyway — tables + seed roles/permissions

---

## test/

### JwtServiceTest.java — Unit: JWT generate/validate/tamper
### AuthServiceTest.java — Unit: registration logic
### AuthIntegrationTest.java — E2E with Testcontainers PostgreSQL

---

## Quick Reference: Request Kahan Jaata Hai?

| Request Type | Entry | Exit |
|-------------|-------|------|
| Register | AuthController | AuthService → UserRepository |
| Login | AuthController | AuthManager → AuthService → JwtService |
| Protected API | JwtFilter → Controller | Service → Repository |
| Error | Any layer | GlobalExceptionHandler |
