# Phase 2 Deep Dive — Account Security

## Modules 7-10 | Package: `phase2.account`

### What
Email verification, password reset, password policy, account locking

### Why
Without these, auth is incomplete for production — anyone can fake emails, brute force passwords

### How — Account Lock
```
failed_login → login_attempts INSERT → credential.failed_attempts++
≥5 → locked_until + Redis key → 401 generic message
```

### Scenario: Forgot Password
User forgets → POST /forgot-password → same message whether email exists → token logged (dev) / emailed (prod) → POST /reset-password with token → CredentialService.updatePassword
