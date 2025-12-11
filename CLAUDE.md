# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot demo application designed to showcase Harness Test Intelligence. The application is an e-commerce backend with 500+ tests, intended to demonstrate how Test Intelligence can reduce test execution by selecting only relevant tests based on code changes.

## Build & Test Commands

```bash
# Build (skip tests)
mvn clean compile -DskipTests

# Run all tests
mvn test

# Run a single test class
mvn test -Dtest=UserServiceTest

# Run a single test method
mvn test -Dtest=UserServiceTest#testCreateUserSuccess

# Package the application
mvn package -DskipTests

# Run the application
mvn spring-boot:run
```

## Architecture

Standard Spring Boot layered architecture with constructor-based dependency injection:

```
src/main/java/com/harnessdemo/
├── Application.java          # Spring Boot entry point
├── controllers/              # REST endpoints (@RestController)
│   ├── UserController.java   # /api/users
│   ├── OrderController.java  # /api/orders
│   └── ProductController.java # /api/products
├── services/                 # Business logic (@Service, @Transactional)
│   ├── UserService.java
│   ├── OrderService.java
│   ├── ProductService.java
│   ├── PaymentService.java   # In-memory payment processing
│   └── NotificationService.java # In-memory notification system
├── repositories/             # Data access (JpaRepository interfaces)
└── models/                   # JPA entities with Jakarta validation
```

## Key Technical Details

- **Java 17**, Spring Boot 3.1.0, JUnit 5.9.2
- **Database**: H2 in-memory (runtime scope)
- **Testing**: Mockito for mocking, `@ExtendWith(MockitoExtension.class)` pattern
- Tests follow naming convention: `*Test.java` (configured in maven-surefire-plugin)

## Test Intelligence Demo

The codebase is structured for Harness TI demos:
- `UserService.java:89-94` contains a commented `validateEmail()` method
- Uncommenting this method during a demo shows TI selecting only ~50 related tests instead of all 500+
- Test placeholder methods exist in `UserServiceTest.java:491-522` for email validation

## Harness CI Pipeline

Located at `.harness/pipeline.yaml` - uses the `RunTests` step with:
- `runOnlySelectedTests: true` to enable Test Intelligence
- `testAnnotations: org.junit.jupiter.api.Test` for JUnit 5
