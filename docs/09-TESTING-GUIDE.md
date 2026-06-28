# Testing Guide

## Test Types

| Type | Location | Speed | Needs Docker |
|------|----------|-------|--------------|
| Unit | `*Test.java` (mockito) | Fast | No |
| Integration | `AuthIntegrationTest.java` | Slower | Yes (Testcontainers) |

---

## Run All Tests

```bash
mvn test
```

## Run Specific Tests

```bash
# JWT unit tests
mvn test -Dtest=JwtServiceTest

# Registration unit tests
mvn test -Dtest=AuthServiceTest

# Full integration flow
mvn test -Dtest=AuthIntegrationTest
```

---

## Module Test Coverage Map

### Module 1 - Registration
| Test Case | File | Status |
|-----------|------|--------|
| Valid registration | AuthServiceTest, AuthIntegrationTest | ✅ |
| Duplicate email | AuthServiceTest | ✅ |
| Duplicate username | AuthServiceTest | ✅ |
| Invalid email | AuthIntegrationTest (validation) | ✅ |
| Invalid password | AuthIntegrationTest | ✅ |

### Module 2 - Login
| Test Case | File | Status |
|-----------|------|--------|
| Valid login | AuthIntegrationTest | ✅ |
| Wrong password | Manual / future test | 🔜 |
| User not found | Manual / future test | 🔜 |
| Disabled user | Manual / future test | 🔜 |

### Module 3 - JWT
| Test Case | File | Status |
|-----------|------|--------|
| Valid token | JwtServiceTest | ✅ |
| Expired token | JwtServiceTest (future explicit) | 🔜 |
| Invalid signature | JwtServiceTest (tampered) | ✅ |
| Tampered token | JwtServiceTest | ✅ |

### Module 4 - Refresh
| Test Case | Status |
|-----------|--------|
| Valid refresh | 🔜 Add in integration test |
| Expired refresh | 🔜 |
| Revoked refresh | 🔜 |

### Module 6 - RBAC
| Test Case | File | Status |
|-----------|------|--------|
| User denied admin | AuthIntegrationTest | ✅ |
| Admin access | 🔜 |
| Permission denied | 🔜 |

---

## Integration Test Setup

`AuthIntegrationTest` uses **Testcontainers**:
- Spins up real PostgreSQL 16
- Flyway migrations run automatically
- Redis auto-config excluded (not needed for Phase 1 tests)

---

## Writing New Tests

### Unit Test Pattern
```java
@ExtendWith(MockitoExtension.class)
class MyServiceTest {
    @Mock private MyRepository repo;
    @InjectMocks private MyService service;
    
    @Test
    void shouldDoSomething() {
        when(repo.find()).thenReturn(...);
        // assert
    }
}
```

### Integration Test Pattern
```java
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class MyIntegrationTest {
    @Autowired MockMvc mockMvc;
    // full HTTP flow test
}
```

---

## Coverage Goal

README target: **95%+** for production modules.

```bash
mvn test jacoco:report  # if jacoco plugin added later
```
