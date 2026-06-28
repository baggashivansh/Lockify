# Production Deployment Checklist

## Before Go-Live

### Secrets
- [ ] `JWT_SECRET` — minimum 32 random characters, cryptographically secure
- [ ] `DB_PASSWORD` — strong, unique
- [ ] Kabhi secrets git me commit mat karo

### Database
- [ ] Managed PostgreSQL (RDS, Cloud SQL, etc.)
- [ ] Flyway migrations CI/CD me run hon
- [ ] Backups enabled
- [ ] SSL connection to DB

### Redis
- [ ] Managed Redis with auth
- [ ] Network isolation (VPC)

### Application
- [ ] `spring.jpa.hibernate.ddl-auto=validate` (never `update` in prod)
- [ ] `server.error.include-message=never`
- [ ] `server.error.include-stacktrace=never`
- [ ] HTTPS only (reverse proxy / load balancer)
- [ ] CORS properly configured for your frontend domains

### Security
- [ ] Rate limiting on login/register (Phase 2 — Redis)
- [ ] Account lockout after failed attempts (Phase 2)
- [ ] Audit logging (Phase 4)
- [ ] Token blacklist for logout (Phase 7)

### Monitoring
- [ ] Prometheus metrics scraped
- [ ] Grafana dashboards
- [ ] Alerting on 5xx, auth failures spike
- [ ] Centralized logging (ELK, CloudWatch)

### Scaling
- [ ] Stateless app — horizontal scaling OK
- [ ] DB connection pool tuned (HikariCP)
- [ ] Health checks on `/actuator/health`

---

## Environment Template

```bash
export DB_HOST=your-db-host
export DB_PORT=5432
export DB_NAME=lockify
export DB_USER=lockify_app
export DB_PASSWORD=<secure>
export REDIS_HOST=your-redis-host
export JWT_SECRET=<64-char-random>
export JWT_ACCESS_EXPIRY=15
export JWT_REFRESH_EXPIRY=7
export SERVER_PORT=8080
```

---

## Docker Build (Example)

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/lockify-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
mvn clean package -DskipTests
docker build -t lockify:latest .
```

---

## Reuse in Other Projects

1. Extract `security/` package as internal library
2. Keep `docs/` as living documentation
3. Customize roles/permissions per product
4. Add product-specific entities in separate migrations
5. Frontend: store tokens securely (memory or httpOnly cookies — not localStorage for high security apps)
