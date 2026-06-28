# Authorization & RBAC (Role-Based Access Control)

## Module 6 Overview

RBAC me **Roles** users ko group karte hain aur **Permissions** fine-grained actions define karte hain.

```
User ──has──► Role(s) ──has──► Permission(s)
```

---

## Default Roles

| Role | Permissions | Use Case |
|------|-------------|----------|
| USER | READ | Normal app user |
| ADMIN | READ, CREATE, UPDATE | Content managers |
| SUPER_ADMIN | READ, CREATE, UPDATE, DELETE | Full system access |

Seed data: `db/migration/V1__init_schema.sql`

---

## Spring Security Integration

### Authorities in JWT
Login pe roles aur permissions JWT payload me embed hote hain:

```json
{
  "roles": ["USER"],
  "permissions": ["READ"]
}
```

### LockifyUserDetails
**File:** `security/LockifyUserDetails.java`

Authorities banate waqt:
- Roles → `ROLE_USER`, `ROLE_ADMIN` (Spring convention)
- Permissions → `READ`, `CREATE`, etc.

### Method Security
**File:** `config/SecurityConfig.java` — `@EnableMethodSecurity`

Controllers me:
```java
@PreAuthorize("hasRole('ADMIN')")           // Role check
@PreAuthorize("hasAuthority('CREATE')")     // Permission check
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
```

---

## Endpoint Access Matrix

| Endpoint | Access |
|----------|--------|
| POST /api/auth/register | Public |
| POST /api/auth/login | Public |
| POST /api/auth/refresh | Public |
| GET /api/auth/me | Any authenticated user |
| GET /api/user/profile | ROLE_USER |
| GET /api/user/read-test | READ permission |
| GET /api/admin/dashboard | ADMIN or SUPER_ADMIN |
| GET /api/admin/super | SUPER_ADMIN only |
| GET /api/admin/users/create-permission-test | CREATE permission |

---

## How Authorization Works on Request

```
JWT Filter parses token
    → roles + permissions → GrantedAuthority list
    → SecurityContext authentication

Controller method invoked
    → @PreAuthorize expression evaluated
    → hasRole('ADMIN') checks ROLE_ADMIN authority
    → Fail → 403 Forbidden JSON
    → Pass → method executes
```

---

## Testing RBAC

1. Register normal user → gets USER role
2. Login → get token
3. `GET /api/admin/dashboard` → **403** (expected)
4. Manually DB me user ko ADMIN role do (future: admin API)
5. Login again → new token with ADMIN role → **200**

---

## Extending RBAC (Any Project)

### Naya Role Add Karna
1. `RoleName` enum me add karo
2. Flyway migration me INSERT into roles
3. role_permissions mapping set karo

### Naya Permission Add Karna
1. `PermissionName` enum me add karo
2. Migration me permission + role mapping
3. Controller pe `@PreAuthorize("hasAuthority('NEW_PERM')")`

### Permission-Based vs Role-Based
- **Role-based:** Simple apps — `hasRole('ADMIN')`
- **Permission-based:** Enterprise — `hasAuthority('USER_DELETE')`
- **Best practice:** Dono use karo — roles for broad access, permissions for granular

---

## Files

| File | Purpose |
|------|---------|
| `entity/Role.java` | Role entity |
| `entity/Permission.java` | Permission entity |
| `entity/User.java` | Many-to-many with roles |
| `controller/AdminController.java` | RBAC demo endpoints |
| `controller/UserController.java` | User-level endpoints |
| `config/SecurityConfig.java` | URL-level + enables method security |
