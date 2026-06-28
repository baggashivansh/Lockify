# JWT Deep Dive - Manual Implementation

## Kyun Manual?

README philosophy: *"No copied authentication implementations"*

`jjwt` ya `nimbus-jose-jwt` library use kar sakte the, lekin manual implementation se:
- Header, payload, signature samajh aata hai
- Interview me explain kar sakte ho
- Custom claims add karna easy hai

---

## JWT RFC 7519 Structure

```
BASE64URL(header) . BASE64URL(payload) . BASE64URL(signature)
```

### Header
```json
{ "alg": "HS256", "typ": "JWT" }
```

### Payload (Claims)
```json
{
  "sub": "1",
  "username": "john",
  "roles": ["USER"],
  "permissions": ["READ"],
  "type": "access",
  "iat": 1719500000,
  "exp": 1719500900,
  "iss": "lockify"
}
```

### Signature
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

---

## Code Walkthrough

**File:** `security/jwt/JwtService.java`

### generateAccessToken()
1. Claims map banao (sub, username, roles, permissions, type)
2. `iat`, `exp`, `iss` add karo
3. Header JSON → Base64URL
4. Payload JSON → Base64URL
5. HMAC sign karo → Base64URL signature
6. Teeno parts join: `header.payload.signature`

### validateAndParse()
1. `.` se 3 parts split — warna invalid format
2. Expected signature compute karo
3. `constantTimeEquals()` — timing attack prevention
4. Payload decode → exp check
5. iss check (issuer validation)
6. `JwtClaims` object return

### constantTimeEquals()
Normal `String.equals()` timing leak kar sakta hai — attacker signature guess kar sakta hai byte-by-byte. Constant-time comparison sab characters XOR karta hai.

---

## Access vs Refresh Token

| | Access Token | Refresh Token |
|---|-------------|---------------|
| Format | JWT | Opaque random string |
| Storage | Client only | Client + DB (hash) |
| Lifetime | 15 min | 7 days |
| API use | Authorization header | /refresh endpoint only |
| Revocable | Hard (until Phase 7) | Easy (DB revoke flag) |

---

## Security Checklist

- [x] Strong secret (256+ bits recommended)
- [x] Short access token expiry
- [x] Signature verification on every request
- [x] Issuer validation
- [x] Expiry validation
- [x] Token type check (access only for APIs)
- [x] Constant-time signature compare
- [ ] Token blacklist (Phase 7)
- [ ] Refresh token rotation with families (Phase 7)

---

## Common Attacks & Defense

| Attack | Defense in Lockify |
|--------|-------------------|
| Token tampering | HMAC signature verify |
| Expired token reuse | exp claim check |
| Algorithm confusion | Fixed HS256 in code |
| Refresh token theft | Hash in DB, revoke on refresh |
| XSS stealing token | Client responsibility — httpOnly cookies in future |

---

## Kisi Aur Project Me Copy Karna

1. `JwtService` + `JwtProperties` copy karo
2. Secret environment variable se lo
3. Claims apne app ke hisaab se customize karo
4. `JwtAuthenticationFilter` Spring Security chain me add karo
5. Expiry production workload ke hisaab se tune karo
