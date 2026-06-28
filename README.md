<p align="center">
  <img src="frontend/public/lockify.svg" width="72" alt="Lockify" />
</p>

<h1 align="center">Lockify</h1>

<p align="center">
  <strong>Enterprise-grade authentication & authorization — built from scratch, explained line by line.</strong>
</p>

<p align="center">
  Java 21 · Spring Boot · PostgreSQL · Redis · React Console
</p>

<p align="center">
  <a href="#quick-start">Quick Start</a> ·
  <a href="#lockify-console">API Console</a> ·
  <a href="#documentation">Docs</a> ·
  <a href="docs/DEEP-DIVE/00-MASTER-INDEX.md">Deep Dive</a>
</p>

---

## What is Lockify?

Most tutorials teach you *how to call* `spring-security-starter`. Lockify teaches you **how auth actually works** — JWT signing, refresh token rotation, account locking, MFA, OAuth2, ABAC — all implemented manually, all documented, all testable from a beautiful UI.

I built this because I wanted one project I could open in any interview, any production debug session, or any new SaaS product and say: *"I know exactly what every line does."*

Lockify is not a login demo. It is a full **Identity & Access Management platform** — the kind of foundation you drop behind a mobile app, a microservices mesh, or an enterprise dashboard.

---

## What You Get

Seven phases. Seven separate packages. One cohesive system.

| Phase | What it does | Try it in Console |
|-------|----------------|-------------------|
| **1 — Core Auth** | Register, login, manual JWT, refresh tokens, RBAC | `/auth` |
| **2 — Account Security** | Email verify, password reset, policies, account lock | `/account` |
| **3 — Sessions** | Device tracking, logout everywhere, trusted devices | `/sessions` |
| **4 — Enterprise** | Audit logs, rate limiting, TOTP MFA | `/mfa` |
| **5 — OAuth2** | Google / GitHub account linking | `/oauth` |
| **6 — Authorization** | Resource ownership, ABAC policies | `/resources` |
| **7 — Hardening** | Token rotation, blacklist, fingerprinting, adaptive auth | `/security` |

Credentials live in a **dedicated `user_credentials` table** — separated from identity and profile data. Passwords use **BCrypt + optional pepper**. Refresh tokens are stored as **SHA-256 hashes only**. This is how production systems are supposed to look.

---

## Quick Start

You need **Java 21**, **Docker**, **Maven**, and **Node.js** (for the console).

```bash
# 1. Clone and configure
cp .env.example .env
# Edit .env — at minimum set JWT_SECRET and PASSWORD_PEPPER (see .env.example)

# 2. Start infrastructure
docker compose up -d

# 3. Start the backend (port 8080)
mvn spring-boot:run

# 4. Start the frontend console (port 5173)
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173** — register, login, and test every API without Postman.

**Test password example:** `Password@123` (upper + lower + number + special char)

---

## Lockify Console

The frontend is a premium **liquid-glass API console** — light mode, Apple-blue accents, frosted cards. Every endpoint from every phase has a form, a **Send Request** button, and a live JSON response viewer.

- Tokens save automatically after login
- Protected routes attach `Authorization: Bearer` for you
- Backend health indicator in the sidebar
- Step-by-step guide built into the app

No Postman collections. No guessing headers. Just click and see.

→ Full walkthrough: [docs/FRONTEND-GUIDE.md](docs/FRONTEND-GUIDE.md)

---

## Project Structure

```
Lockify/
├── src/main/java/com/lockify/
│   ├── shared/           # User, credentials, security config
│   ├── phase1/coreauth/  # JWT, login, RBAC
│   ├── phase2/account/   # Email verify, password reset
│   ├── phase3/session/   # Sessions & devices
│   ├── phase4/enterprise/# Audit, rate limit, MFA
│   ├── phase5/oauth/     # Google, GitHub
│   ├── phase6/authorization/ # ABAC, ownership
│   └── phase7/hardening/ # Token rotation, blacklist
├── frontend/             # React API console
├── docs/                 # Deep dives, scenarios, file guides
└── .env.example          # Every secret documented
```

Each phase is its own folder — copy only what you need into another project.

→ Details: [docs/12-PHASE-FOLDER-STRUCTURE.md](docs/12-PHASE-FOLDER-STRUCTURE.md)

---

## Tech Stack

| Layer | Choices |
|-------|---------|
| **Backend** | Java 21, Spring Boot 3.4, Spring Security, JPA, Flyway |
| **Database** | PostgreSQL (credentials isolated in `user_credentials`) |
| **Cache** | Redis (rate limits, account lock, token blacklist) |
| **Auth** | Manual JWT (HMAC-SHA256), opaque refresh tokens, BCrypt + pepper |
| **Frontend** | React 18, TypeScript, Vite, Tailwind CSS |
| **Testing** | JUnit 5, Mockito, Testcontainers |
| **Observability** | Actuator, Prometheus |

---

## How I Built This (Philosophy)

1. **No copied auth libraries** — JWT is manual. You will understand every claim.
2. **Security first** — generic errors, hashed tokens, separated credentials, rate limits.
3. **Every phase documented** — what, why, how, code file, attack scenarios.
4. **Test everything** — unit tests + integration tests with real PostgreSQL.
5. **Explainable in an interview** — if I cannot explain a line, it does not ship.

---

## Documentation

Start here if you want to go deep:

| Resource | What's inside |
|----------|---------------|
| [Deep Dive Index](docs/DEEP-DIVE/00-MASTER-INDEX.md) | Master guide — what / why / how / scenarios |
| [Database Complete](docs/DEEP-DIVE/DATABASE-COMPLETE.md) | Every table, every column |
| [Credentials Deep Dive](docs/DEEP-DIVE/CREDENTIALS-DEEP-DIVE.md) | Pepper, BCrypt, breach scenarios |
| [Attack Scenarios](docs/DEEP-DIVE/ATTACK-SCENARIOS.md) | 20+ attacks and how Lockify blocks them |
| [JWT Line by Line](docs/DEEP-DIVE/JWT-LINE-BY-LINE.md) | JwtService explained |
| [Frontend Guide](docs/FRONTEND-GUIDE.md) | Console testing walkthrough |
| [.env.example](.env.example) | All environment variables |

The original module roadmap (27 modules across 7 phases) lives in the repo history and in [docs/11-PHASE-ROADMAP.md](docs/11-PHASE-ROADMAP.md) — all phases are implemented.

---

## Production Checklist

Before you deploy, read `.env.example` and verify:

- [ ] `JWT_SECRET` — 64+ random characters (`openssl rand -base64 64`)
- [ ] `PASSWORD_PEPPER` — set and backed up offline
- [ ] `DB_PASSWORD` — strong, unique
- [ ] HTTPS enabled behind a reverse proxy
- [ ] `CORS_ORIGINS` includes only your real frontend domain
- [ ] Redis has authentication in production

---

<p align="center">
  <br />
  <sub>
    Lockify — built entirely by hand to understand how enterprise authentication works internally.
  </sub>
  <br /><br />
  <strong>Built by Shivansh Bagga</strong>
  <br />
  <sub>© 2026</sub>
</p>
