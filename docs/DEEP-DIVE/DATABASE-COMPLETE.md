# Database Complete Reference

## V8 Credential Architecture (Production)

### users — Identity only (NO password)
- `username`, `email`, `enabled`, `email_verified`, `account_locked`
- **Entity:** `shared/domain/entity/User.java`
- **Migration:** V1 + V8 (password columns removed)

### user_credentials — Secrets (MOST PROTECTED)
- `password_hash`, `hash_algorithm`, `hash_strength`, `pepper_version`
- `failed_login_attempts`, `locked_until`, `password_expires_at`, `mfa_required`
- **Entity:** `UserCredential.java`
- **Service:** `CredentialService.java` — ONLY write path for passwords

### user_profiles — Display PII
- `first_name`, `display_name`, `phone`, `avatar_url`, `timezone`, `country_code`
- **Entity:** `UserProfile.java`

### api_keys — Machine-to-machine
- `key_hash` (SHA-256), `key_prefix`, `scopes`, `revoked`
- **Entity:** `ApiKey.java`

## Scenario: DB Breach
Attacker gets dump → has BCrypt hashes but needs `PASSWORD_PEPPER` from env + years to crack.

## Scenario: Registration Writes
`AuthService` → save User → `CredentialService.createCredentialsForUser()` → email verify token → audit log
