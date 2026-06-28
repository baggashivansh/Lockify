# Authentication Flow - Detail Me

## Module 1: Registration

### API
`POST /api/auth/register`

### Flow
1. Client `RegisterRequest` bhejta hai (username, email, password)
2. Bean Validation check karti hai:
   - Username: 3-50 chars, alphanumeric + underscore
   - Email: valid format
   - Password: min 8, upper+lower+digit+special
3. `AuthService` duplicate email/username check
4. Password `BCryptPasswordEncoder` se encode
5. Default `USER` role assign
6. `users` table me save
7. `UserResponse` return (password kabhi nahi)

### Security Notes
- Password hash kabhi response me nahi
- Duplicate check DB level pe bhi unique constraint se backed hai

---

## Module 2: Login

### API
`POST /api/auth/login`

### Flow
1. Client `identifier` (email YA username) + `password` bhejta hai
2. `AuthenticationManager` credentials verify karta hai:
   - `CustomUserDetailsService` se user load
   - BCrypt password match
3. Account checks: enabled? locked?
4. `last_login_at` update
5. Access + Refresh token generate (Module 3 & 4)

### Security Notes
- Wrong password pe generic message: "Invalid email/username ya password"
- User exist karta hai ya nahi — ye info leak nahi karte (timing attacks ke liye future me constant-time compare)

---

## Module 3: JWT Access Token

### Manual Implementation
**File:** `security/jwt/JwtService.java`

### Token Structure
```
eyJhbGciOiJIUzI1NiIs...  .  eyJzdWIiOiIxIi...  .  SflKxwRJ...
        HEADER                 PAYLOAD              SIGNATURE
```

### Payload Claims
| Claim | Value |
|-------|-------|
| sub | User ID |
| username | Username |
| roles | ["USER"] |
| permissions | ["READ"] |
| type | "access" |
| iat | Issued at (epoch) |
| exp | Expiry (epoch) |
| iss | "lockify" |

### Signing
- Algorithm: HMAC-SHA256
- Secret: `lockify.security.jwt.secret` (min 32 chars)
- Tampering detect: signature mismatch → reject

### Default Expiry
15 minutes (configurable via `JWT_ACCESS_EXPIRY`)

---

## Module 4: Refresh Token

### Kyun Alag Token?
Access token short-lived hai (security). Refresh token long-lived hai (UX — bar-bar login nahi).

### Implementation
- **Opaque token** — JWT nahi, random 32-byte Base64 string
- **DB me hash store** — SHA-256 hash, raw token sirf client ke paas
- **Expiry:** 7 days (configurable)

### API
`POST /api/auth/refresh`

```json
{ "refreshToken": "<refresh_token_from_login>" }
```

### Flow
1. Refresh token ka hash banao
2. DB me dhundho — valid? revoked? expired?
3. Purana token **revoke** karo (rotation foundation)
4. Naya access + refresh token issue karo

### Revoke Scenarios
- Expired → "dubara login karo"
- Revoked → "dubara login karo"
- Invalid hash → 401

---

## Token Usage

### Header Format
```
Authorization: Bearer <access_token>
```

### Refresh Token Use
- Sirf `/api/auth/refresh` pe bhejo
- Access token ki tarah API calls pe mat use karo — filter reject karega (`type` must be `access`)

---

## Complete Auth Lifecycle

```text
[Register] → User created (no token)
     │
     ▼
[Login] → accessToken (15 min) + refreshToken (7 days)
     │
     ├──► API calls with accessToken
     │         │
     │         ├─ 200 OK (valid)
     │         └─ 401 (expired) → use refresh
     │
     ▼
[Refresh] → new accessToken + new refreshToken (old revoked)
     │
     ▼
[Login again] if refresh also expired
```

---

## Files Involved

| Step | File |
|------|------|
| Register API | `controller/AuthController.java` |
| Register logic | `service/AuthService.java` |
| Login auth | `config/SecurityConfig.java` + `AuthenticationManager` |
| JWT create/validate | `security/jwt/JwtService.java` |
| Request filter | `security/jwt/JwtAuthenticationFilter.java` |
| Refresh storage | `entity/RefreshToken.java`, `repository/RefreshTokenRepository.java` |

See also: [06-JWT-DEEP-DIVE.md](./06-JWT-DEEP-DIVE.md)
