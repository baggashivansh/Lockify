# JWT Line-by-Line — JwtService.java

**File:** `phase1/coreauth/security/jwt/JwtService.java`

## generateAccessToken() — What/Why/How

| Line | What | Why |
|------|------|-----|
| claims.put("sub", userId) | Subject = user ID | Standard JWT claim |
| claims.put("roles", roles) | RBAC in token | Avoid DB hit per request |
| claims.put("type", "access") | Token type | Block refresh token on APIs |
| buildToken() | Header+Payload+Sign | RFC 7519 structure |

## validateAndParse() — Security Checks

| Step | What | Attack Prevented |
|------|------|------------------|
| Split by `.` | 3 parts required | Malformed token |
| sign(header.payload) | HMAC verify | Tampering |
| constantTimeEquals | No early exit on compare | Timing attack |
| exp check | Expiry validation | Stolen old token reuse |
| iss check | Issuer match | Token from wrong system |

## hashToken() — Refresh Tokens
SHA-256 one-way. DB stores hash. Client has raw. Leak = useless.

## Scenario: Attacker Modifies JWT Payload
1. Changes role USER → ADMIN in payload
2. Signature no longer matches
3. constantTimeEquals fails → 401 "Invalid token signature"

## Scenario: Valid Login Token Flow
1. Login → JWT with 15min exp
2. Each API: JwtAuthenticationFilter validates
3. SecurityContext set → @PreAuthorize works
4. Expired → 401 → client uses refresh token
