# Request Flow - Ek API Call Ka Poora Safar

## Overview

Jab client `GET /api/auth/me` call karta hai Bearer token ke saath, yeh hota hai internally:

---

## Step-by-Step Flow

### 1. HTTP Request Aata Hai

```
GET /api/auth/me HTTP/1.1
Host: localhost:8080
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
```

### 2. Servlet Container (Tomcat)

Spring Boot embedded Tomcat request receive karta hai aur filter chain start karta hai.

### 3. JwtAuthenticationFilter

**File:** `security/jwt/JwtAuthenticationFilter.java`

```
Authorization header check
    │
    ├─ Header nahi? → Skip, aage badho
    │
    └─ "Bearer " prefix hai?
           │
           ▼
       Token extract karo
           │
           ▼
       JwtService.validateAndParse(token)
           │
           ├─ Invalid/Expired → jwt_error attribute set, context clear
           │
           └─ Valid → LockifyUserDetails banao
                      SecurityContextHolder me set karo
```

### 4. Spring Security Authorization

**File:** `config/SecurityConfig.java`

```
/api/auth/me → authenticated() required
User authenticated hai? (SecurityContext me principal hai?)
    │
    ├─ Nahi → JwtAuthenticationEntryPoint → 401 JSON
    │
    └─ Haan → Controller tak request jao
```

### 5. Controller

**File:** `controller/AuthController.java`

```java
@GetMapping("/me")
public ResponseEntity<UserResponse> me(@AuthenticationPrincipal LockifyUserDetails userDetails)
```

`@AuthenticationPrincipal` SecurityContext se user inject karta hai — manually token parse nahi karna padta.

### 6. Service Layer

**File:** `service/AuthService.java`

```java
getCurrentUser(userDetails) → DB se fresh user data → UserMapper.toResponse()
```

### 7. Response

```json
{
  "id": 1,
  "username": "demo_user",
  "email": "demo@lockify.com",
  "roles": ["USER"]
}
```

---

## Public Endpoint Flow (Register)

Public endpoints pe JWT filter kuch set nahi karta — seedha controller:

```
POST /api/auth/register
  → SecurityConfig: permitAll() ✓
  → JwtAuthenticationFilter: no token, skip
  → AuthController.register()
  → @Valid validation (DTO annotations)
  → AuthService.register()
  → 201 Created
```

---

## Error Flow

```
Validation fail → GlobalExceptionHandler → 400 + validationErrors map
Duplicate email → DuplicateResourceException → 409
Wrong password  → AuthenticationException → 401
No permission   → Spring AccessDeniedException → 403
Invalid JWT     → JwtAuthenticationEntryPoint → 401
```

**File:** `exception/GlobalExceptionHandler.java` — saari errors consistent `ApiErrorResponse` format me.

---

## Diagram

```mermaid
sequenceDiagram
    participant C as Client
    participant F as JwtFilter
    participant S as SecurityConfig
    participant CT as Controller
    participant SV as Service
    participant DB as PostgreSQL

    C->>F: GET /api/auth/me + Bearer Token
    F->>F: Validate JWT
    F->>S: SecurityContext set
    S->>CT: Authorized request
    CT->>SV: getCurrentUser()
    SV->>DB: findById()
    DB-->>SV: User entity
    SV-->>CT: UserResponse
    CT-->>C: 200 JSON
```
