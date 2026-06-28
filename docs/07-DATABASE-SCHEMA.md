# Database Schema

## ER Diagram (Text)

```text
users ──────< user_roles >────── roles
                                    │
                                    └──< role_permissions >── permissions

users ──────< refresh_tokens
```

---

## Tables (Phase 1)

### users
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL PK | Auto increment |
| username | VARCHAR(50) UNIQUE | Login identifier |
| email | VARCHAR(255) UNIQUE | Login identifier |
| password_hash | VARCHAR(255) | BCrypt hash only |
| enabled | BOOLEAN | Disabled account support |
| account_locked | BOOLEAN | Phase 2 brute force ke liye ready |
| created_at | TIMESTAMPTZ | Audit |
| updated_at | TIMESTAMPTZ | Audit |
| last_login_at | TIMESTAMPTZ | Security monitoring |

### roles
| Column | Type |
|--------|------|
| id | BIGSERIAL PK |
| name | VARCHAR(50) UNIQUE (USER, ADMIN, SUPER_ADMIN) |
| description | VARCHAR(255) |

### permissions
| Column | Type |
|--------|------|
| id | BIGSERIAL PK |
| name | VARCHAR(100) UNIQUE (READ, CREATE, UPDATE, DELETE) |

### user_roles (junction)
| Column | Type |
|--------|------|
| user_id | FK → users |
| role_id | FK → roles |

### role_permissions (junction)
| Column | Type |
|--------|------|
| role_id | FK → roles |
| permission_id | FK → permissions |

### refresh_tokens
| Column | Type | Notes |
|--------|------|-------|
| id | BIGSERIAL PK | |
| token_hash | VARCHAR(255) UNIQUE | SHA-256, not raw token |
| user_id | FK → users | |
| expires_at | TIMESTAMPTZ | |
| revoked | BOOLEAN | Logout/revoke support |
| created_at | TIMESTAMPTZ | |
| revoked_at | TIMESTAMPTZ | Nullable |

---

## Future Tables (Roadmap)

Documented in README — migrations add honge phase by phase:

- `login_attempts` — brute force (Phase 2)
- `password_history` — password reuse prevent (Phase 2)
- `email_verifications` — email verify (Phase 2)
- `user_sessions` — session tracking (Phase 3)
- `audit_logs` — enterprise audit (Phase 4)
- `mfa_configurations`, `otp_codes` — MFA (Phase 4)
- `oauth_accounts` — OAuth2 (Phase 5)

---

## Flyway

**Location:** `src/main/resources/db/migration/`

Naming: `V{version}__{description}.sql`

- `V1__init_schema.sql` — Phase 1 complete schema + seed data

**Rule:** Kabhi existing migration edit mat karo production me — nayi migration banao.

---

## JPA Entities

| Table | Entity Class |
|-------|-------------|
| users | `entity/User.java` |
| roles | `entity/Role.java` |
| permissions | `entity/Permission.java` |
| refresh_tokens | `entity/RefreshToken.java` |

Enums: `RoleName`, `PermissionName` — type-safe role/permission names.

---

## Indexes

```sql
idx_users_email
idx_users_username
idx_refresh_tokens_user_id
idx_refresh_tokens_expires
```

Production me query patterns ke hisaab se aur indexes add karo.
