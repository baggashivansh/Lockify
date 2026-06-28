# Lockify - Project Overview (Hinglish)

## Kya Hai Lockify?

Lockify ek **production-grade Authentication & Authorization platform** hai jo Java 21 aur Spring Boot se scratch se banaya gaya hai.

Yeh sirf "login/signup tutorial" nahi hai — yeh ek **complete IAM (Identity & Access Management)** foundation hai jo tum kisi bhi enterprise app, SaaS product, microservice, ya mobile backend me reuse kar sakte ho.

---

## Kyun Banaya?

Real companies me auth systems complex hote hain:
- JWT tokens
- Refresh tokens
- Role-based access (RBAC)
- Session management
- MFA, OAuth2 (future phases)

Lockify ka goal hai **har layer manually samajhna** taaki tum interview me, production debug me, ya apne project me confidently kaam kar sako.

---

## Abhi Kya Complete Hai? (Phase 1)

| Module | Status | Description |
|--------|--------|-------------|
| Module 1 - Registration | ✅ Done | Signup, validation, duplicate check |
| Module 2 - Login | ✅ Done | Email/username login |
| Module 3 - JWT | ✅ Done | Manual JWT (no library) |
| Module 4 - Refresh Tokens | ✅ Done | Opaque tokens, DB storage |
| Module 5 - Spring Security | ✅ Done | Filter chain, stateless |
| Module 6 - RBAC | ✅ Done | Roles + Permissions |

**Phase 2-7** roadmap me documented hain — email verification, MFA, OAuth2, etc.

---

## Tech Stack

```
Java 21 + Spring Boot 3.4
PostgreSQL (data) + Redis (cache foundation)
Flyway (migrations) + Testcontainers (tests)
Prometheus (metrics)
```

---

## Project Structure (Quick Map)

```
Lockify/
├── src/main/java/com/lockify/
│   ├── config/          → Security, JWT, Redis, Password config
│   ├── controller/      → REST APIs (HTTP layer)
│   ├── dto/             → Request/Response objects
│   ├── entity/          → Database tables (JPA)
│   ├── exception/       → Error handling
│   ├── mapper/          → Entity ↔ DTO conversion
│   ├── repository/      → Database queries
│   ├── security/        → JWT filter, UserDetails
│   └── service/         → Business logic
├── src/main/resources/
│   ├── application.yml  → Configuration
│   └── db/migration/    → Flyway SQL scripts
├── src/test/            → Unit + Integration tests
└── docs/                → Tum yahan ho — poori documentation
```

---

## Docs Navigation

| Document | Kya Milega |
|----------|------------|
| [01-ARCHITECTURE.md](./01-ARCHITECTURE.md) | System design, layers, clean architecture |
| [02-SETUP-GUIDE.md](./02-SETUP-GUIDE.md) | Local setup step-by-step |
| [03-REQUEST-FLOW.md](./03-REQUEST-FLOW.md) | Ek HTTP request ka poora journey |
| [04-AUTHENTICATION-FLOW.md](./04-AUTHENTICATION-FLOW.md) | Register, Login, JWT, Refresh detail me |
| [05-AUTHORIZATION-RBAC.md](./05-AUTHORIZATION-RBAC.md) | Roles, permissions, @PreAuthorize |
| [06-JWT-DEEP-DIVE.md](./06-JWT-DEEP-DIVE.md) | JWT manually kaise banaya |
| [07-DATABASE-SCHEMA.md](./07-DATABASE-SCHEMA.md) | Tables, relationships, seed data |
| [08-API-REFERENCE.md](./08-API-REFERENCE.md) | Saari APIs with examples |
| [09-TESTING-GUIDE.md](./09-TESTING-GUIDE.md) | Tests kaise run karein |
| [10-PRODUCTION-DEPLOYMENT.md](./10-PRODUCTION-DEPLOYMENT.md) | Production checklist |
| [11-PHASE-ROADMAP.md](./11-PHASE-ROADMAP.md) | Future modules plan |
| [files/INDEX.md](./files/INDEX.md) | Har source file ka explanation |

---

## Kisi Aur Project Me Kaise Use Karein?

1. **Copy pattern, not code blindly** — `security/`, `config/`, `service/` structure reuse karo
2. **JWT secret** environment variable se lo — kabhi code me hardcode mat karo
3. **Flyway migrations** apne tables ke liye extend karo
4. **RBAC seed data** apne app ke roles/permissions ke hisaab se change karo
5. **docs/** folder ko template ki tarah rakho — har naye module ke liye doc add karo

---

## Golden Rules (Development Philosophy)

1. Security first — password plain text kabhi nahi
2. Har feature ke tests likho
3. Har module document karo
4. External auth library copy mat karo — samajh ke likho
5. Production defaults use karo (stateless, no stack traces in response)
