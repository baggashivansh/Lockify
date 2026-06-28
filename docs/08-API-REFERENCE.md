# API Reference

Base URL: `http://localhost:8080`

All responses are JSON. Errors follow `ApiErrorResponse` format.

---

## Auth APIs (Public)

### Register
```
POST /api/auth/register
```

**Request:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "Password@123"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["USER"]
}
```

**Errors:**
- `400` — Validation failed
- `409` — Duplicate email/username

---

### Login
```
POST /api/auth/login
```

**Request:**
```json
{
  "identifier": "john@example.com",
  "password": "Password@123"
}
```

`identifier` = email **or** username

**Response:** `200 OK`
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "xK9mP2...",
  "tokenType": "Bearer",
  "expiresInSeconds": 900,
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "roles": ["USER"]
  }
}
```

**Errors:**
- `401` — Invalid credentials, disabled, locked

---

### Refresh Token
```
POST /api/auth/refresh
```

**Request:**
```json
{
  "refreshToken": "<refresh_token>"
}
```

**Response:** Same as login (new token pair)

**Errors:**
- `401` — Invalid/expired/revoked refresh token

---

## Auth APIs (Protected)

### Current User
```
GET /api/auth/me
Authorization: Bearer <access_token>
```

**Response:** `200 OK` — UserResponse

---

## User APIs (Protected)

### Profile
```
GET /api/user/profile
Authorization: Bearer <access_token>
```
Requires: `ROLE_USER`

### Read Permission Test
```
GET /api/user/read-test
Authorization: Bearer <access_token>
```
Requires: `READ` permission

---

## Admin APIs (Protected)

### Dashboard
```
GET /api/admin/dashboard
Authorization: Bearer <access_token>
```
Requires: `ADMIN` or `SUPER_ADMIN` role

### Create Permission Test
```
GET /api/admin/users/create-permission-test
```
Requires: `CREATE` permission

### Super Admin Only
```
GET /api/admin/super
```
Requires: `SUPER_ADMIN` role

---

## Error Response Format

```json
{
  "timestamp": "2025-06-28T10:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "message": "Request me validation errors hain",
  "path": "/api/auth/register",
  "validationErrors": {
    "password": "Password minimum 8 characters ka hona chahiye"
  }
}
```

---

## Health & Metrics

```
GET /actuator/health     — Public
GET /actuator/prometheus — Public (secure in production)
```
