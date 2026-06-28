# Lockify Console — Frontend Guide

Premium liquid-glass API console. **Postman ki zaroorat nahi.**

## Design

- Light mode, Apple blue (`#0071e3`)
- Glassmorphism — `backdrop-blur`, white/60 opacity cards
- Footer: **70% transparent** — "Built by Shivansh Bagga"
- Sidebar navigation by phase

## Setup

```bash
# 1. Backend (Terminal 1)
docker compose up -d
mvn spring-boot:run

# 2. Frontend (Terminal 2)
cd frontend
npm install
npm run dev
```

Open: **http://localhost:5173**

## Testing Flow (Step by Step)

### Step 1 — Guide Page
Home page pe poora flow likha hai. Backend health indicator sidebar me dikhega (green = online).

### Step 2 — Register (Phase 1 → Auth)
- Username, email, password bharo
- Password example: `Password@123`
- **Send Request** → 201 Created

### Step 3 — Login
- Same email/username + password
- Tokens **automatically save** honge (localStorage)
- Dashboard pe tokens dikhenge

### Step 4 — Protected APIs
Koi bhi page kholo (Sessions, MFA, Resources) → **Send Request**
- Token auto-attach hota hai `Authorization: Bearer ...` header me
- Response neeche JSON me dikhega + status + timing

### Step 5 — Email Verify (Phase 2)
- Register ke baad **backend terminal logs** me verification token dikhega:
  ```
  [EMAIL] Verification token for user@email.com — token: xxxxx
  ```
- Copy karke Account page pe paste karo

### Step 6 — Admin/RBAC Test
- USER role se `/api/admin/dashboard` → **403** (expected!)
- Yeh prove karta hai RBAC kaam kar raha hai

## Pages Map

| Page | APIs Covered |
|------|-------------|
| Phase 1 Auth | register, login, refresh, me |
| Phase 2 Account | verify-email, forgot/reset password |
| Phase 3 Sessions | sessions, devices, logout-all |
| Phase 4 MFA | setup, verify, enable, disable |
| Phase 5 OAuth | linked accounts |
| Phase 6 Resources | CRUD with ownership |
| Phase 7 Security | security events |
| Admin | RBAC test endpoints |

## Tech Stack

- React 18 + TypeScript + Vite
- Tailwind CSS (glass utilities)
- Lucide icons
- Vite proxy → `localhost:8080` (no CORS issues in dev)

## Production Build

```bash
cd frontend
npm run build
# dist/ folder — serve via Nginx/CDN
# Set VITE_API_URL=https://api.yourdomain.com
```

## CORS

Backend `CORS_ORIGINS` env me frontend URL add karo:
```
CORS_ORIGINS=http://localhost:5173,https://console.yourdomain.com
```
