# Setup Guide - Local Development

## Prerequisites

- **Java 21** — `java -version`
- **Maven 3.9+** — `mvn -version`
- **Docker Desktop** — PostgreSQL + Redis ke liye

---

## Step 1: Clone & Open

```bash
cd /path/to/Lockify
```

---

## Step 2: Start Infrastructure

```bash
docker compose up -d
```

Yeh start karega:
- PostgreSQL on `localhost:5432` (db: `lockify`, user: `lockify`, pass: `lockify_secret`)
- Redis on `localhost:6379`

Verify:
```bash
docker compose ps
```

---

## Step 3: Run Application

```bash
mvn spring-boot:run
```

App start hoga: `http://localhost:8080`

Health check:
```bash
curl http://localhost:8080/actuator/health
```

---

## Step 4: Test Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "demo_user",
    "email": "demo@lockify.com",
    "password": "Password@123"
  }'
```

---

## Step 5: Test Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "demo@lockify.com",
    "password": "Password@123"
  }'
```

Response me `accessToken` aur `refreshToken` milega.

---

## Step 6: Protected API Call

```bash
# TOKEN ko login response se copy karo
curl http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer <ACCESS_TOKEN>"
```

---

## Environment Variables (Production)

| Variable | Default (dev) | Description |
|----------|---------------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | lockify | Database name |
| `DB_USER` | lockify | DB username |
| `DB_PASSWORD` | lockify_secret | DB password |
| `REDIS_HOST` | localhost | Redis host |
| `JWT_SECRET` | (dev key in yml) | **Production me zaroor change karo** |
| `JWT_ACCESS_EXPIRY` | 15 | Access token minutes |
| `JWT_REFRESH_EXPIRY` | 7 | Refresh token days |
| `SERVER_PORT` | 8080 | App port |

---

## Run Tests

```bash
# Unit tests only (fast)
mvn test -Dtest=JwtServiceTest,AuthServiceTest

# Full tests (Docker required for Testcontainers)
mvn test
```

---

## Common Issues

### Port 5432 already in use
Local PostgreSQL chal raha hai — docker-compose me port change karo ya local PG stop karo.

### Flyway migration fail
Database clean karo:
```bash
docker compose down -v
docker compose up -d
```

### Redis connection error (tests)
Integration tests Redis auto-config exclude karte hain — normal hai.
