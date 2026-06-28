# Token Lifecycle Complete

## Access Token (JWT)
- **Created:** Login, Refresh
- **Lifetime:** 15 minutes
- **Storage:** Client memory (not localStorage for high security apps)
- **Validated:** Every protected request
- **Revoked:** TokenBlacklistFilter (logout)

## Refresh Token (Opaque)
- **Created:** Login, Refresh (old revoked)
- **Lifetime:** 7 days
- **Storage:** Client secure storage + DB as SHA-256 hash
- **Used:** Only POST /api/auth/refresh
- **Rotation:** Phase 7 family tracking

## Scenario: Normal Day
```
09:00 Login → access(15m) + refresh(7d)
09:14 API calls with access token ✓
09:16 Access expired → POST /refresh → new pair, old refresh revoked
18:00 Logout → blacklist access + revoke refresh
```

## Scenario: Token Theft
```
Attacker steals access token at 09:05
09:16 token expires → useless
If stolen refresh token:
  - Legit user refreshes first → attacker's copy revoked (rotation)
  - Attacker uses revoked copy → ENTIRE family revoked (reuse detection)
```
