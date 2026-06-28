# File-by-File Master Reference

## shared/ — Cross-phase foundation

| File | What | Why | How |
|------|------|-----|-----|
| `domain/entity/User.java` | Identity record | No secrets in identity | JPA entity, roles M2M |
| `domain/entity/UserCredential.java` | Password + lock state | Breach isolation | 1:1 with User |
| `domain/entity/UserProfile.java` | Display data | PII separate | 1:1 with User |
| `security/CredentialService.java` | Password operations hub | Single write path | BCrypt via encoder |
| `security/PepperedPasswordEncoder.java` | BCrypt + pepper | Extra DB breach layer | Wraps BCryptPasswordEncoder |
| `security/SecurityHeadersFilter.java` | HTTP security headers | Browser attacks | OncePerRequestFilter |
| `config/SecurityConfig.java` | Filter chain + URLs | Central security | SecurityFilterChain bean |
| `util/TokenHasher.java` | SHA-256 + random tokens | Verification/reset tokens | MessageDigest |

## phase1/coreauth/

| File | What | Why | How |
|------|------|-----|-----|
| `service/AuthService.java` | Auth hub | Orchestrates all phases | Register/Login/Refresh |
| `security/jwt/JwtService.java` | Manual JWT | Learn + control | HMAC-SHA256 |
| `security/jwt/JwtAuthenticationFilter.java` | Per-request auth | Stateless API | Bearer header parse |
| `security/LockifyUserDetails.java` | Spring UserDetails | SecurityContext | Roles + permissions |

## phase2/account/

| File | What | Why | How |
|------|------|-----|-----|
| `service/AccountLockService.java` | Brute force protection | 5 fails = lock | Redis + credentials table |
| `service/EmailVerificationService.java` | Email confirm | Real users only | Token hash in DB |
| `service/PasswordResetService.java` | Forgot password | Self-service recovery | Same response always |
| `service/PasswordPolicyService.java` | Password rules | No reuse | password_history |

## phase7/hardening/

| File | What | Why | How |
|------|------|-----|-----|
| `service/TokenRotationService.java` | Refresh rotation | Reuse attack detect | family_id in DB |
| `service/TokenBlacklistService.java` | Access revoke | Logout works | Redis TTL = token exp |
| `filter/TokenBlacklistFilter.java` | Block revoked JWT | Before JWT parse | Redis lookup |

Full phase docs: `docs/phase1-7/DEEP-DIVE.md`
