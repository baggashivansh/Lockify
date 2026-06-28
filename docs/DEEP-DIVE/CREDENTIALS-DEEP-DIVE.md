# Credentials Deep Dive

## What Is Used
- **BCrypt** (strength 12) — password hashing
- **Pepper** — server-side secret from `PASSWORD_PEPPER` env
- **user_credentials table** — isolated from identity

## Why

### Why BCrypt not MD5/SHA?
MD5/SHA are fast → attacker tries billions/sec. BCrypt is intentionally slow (2^12 iterations).

### Why Pepper on top of BCrypt?
BCrypt has per-password salt in hash. Pepper adds app-wide secret NOT in database.
```
Attacker has: password_hash from DB
Attacker needs: pepper from server env + correct password
```

### Why separate user_credentials table?
- DB admin can revoke access to credentials table only
- Profile export for GDPR doesn't include password hashes
- Microservice split: Auth service owns credentials DB

## How Implemented

**File:** `shared/security/PepperedPasswordEncoder.java`
```text
encode(password):
  1. peppered = password + PASSWORD_PEPPER
  2. return BCrypt.encode(peppered)

matches(raw, hash):
  1. return BCrypt.matches(raw + PEPPER, hash)
```

**File:** `shared/security/CredentialService.java`
- `createCredentialsForUser()` — register
- `updatePassword()` — reset
- `recordFailedAttempt()` / `lockUntil()` — brute force

## Scenarios

### Scenario 1: User Registers
1. POST /api/auth/register
2. User saved (no password column)
3. CredentialService: BCrypt(pepper+password) → user_credentials
4. Profile created with display_name

### Scenario 2: 5 Failed Logins
1. AccountLockService.recordFailedLogin()
2. credential.failed_login_attempts++
3. At 5: locked_until = now + 30min, Redis cache set
4. Generic 401 on next attempt

### Scenario 3: Password Reset
1. Token hash in password_resets (not raw in DB)
2. validatePasswordNotInHistory() — last 5 passwords
3. CredentialService.updatePassword() — new BCrypt hash
