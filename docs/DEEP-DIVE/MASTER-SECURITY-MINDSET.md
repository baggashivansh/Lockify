# Master Security Mindset

## 5 Rules — Sapne Me Bhi Yaad Rahenge

1. **Plain password DB me kabhi nahi** → `user_credentials.password_hash` (BCrypt)
2. **Secrets env se aati hain** → `JWT_SECRET`, `PASSWORD_PEPPER` (.env.example)
3. **Identity ≠ Credentials** → `users` vs `user_credentials` vs `user_profiles`
4. **Tokens hashed in DB** → refresh tokens, API keys, verification tokens
5. **Generic errors** → "Invalid credentials" (user enumeration block)

## Technology Choices

| What | Why | File |
|------|-----|------|
| BCrypt strength 12 | Brute force slow, auto-salt | `PasswordConfig.java` |
| Pepper | DB steal pe extra secret chahiye | `PepperedPasswordEncoder.java` |
| Manual JWT | Samajhne ke liye, no black box | `JwtService.java` |
| SHA-256 token hash | One-way, fast verify | `TokenHasher.java` |
| Redis lock cache | Fast lock check, TTL auto-expire | `AccountLockService.java` |
| Flyway V1-V8 | Schema version control | `db/migration/` |
