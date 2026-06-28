# Lockify Console — Frontend

Premium API testing console for Lockify. Postman ki zaroorat nahi.

## Quick Start

```bash
# Terminal 1 — Backend
docker compose up -d
mvn spring-boot:run

# Terminal 2 — Frontend
cd frontend
npm install
npm run dev
```

Open **http://localhost:5173**

## Features

- Apple-style liquid glass UI (light mode, blue accent)
- Saari Phase 1-7 APIs test karo
- Auto JWT token management
- Step-by-step guide built-in
- Live backend health indicator
- Response viewer with timing

## Env

```bash
# Optional — default uses Vite proxy to :8080
VITE_API_URL=http://localhost:8080
```
