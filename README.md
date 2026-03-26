# Tanzschule Family & Friends – Backend Service

REST API backend for the Tanzschule Family & Friends web application.

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.4**
- **Gradle 9.4.1** (Groovy DSL)
- **PostgreSQL 17** with **Flyway** migrations
- **Spring Security** with JWT authentication
- **JUnit 5** with **Testcontainers** for integration tests

## Getting Started

### Prerequisites

- Java 25 (e.g. [Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-25-ug/what-is-corretto-25.html))
- PostgreSQL 17
- Docker (for integration tests via Testcontainers)

### Local Development

1. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE tanzschule;
   CREATE USER tanzschule WITH PASSWORD 'tanzschule';
   GRANT ALL PRIVILEGES ON DATABASE tanzschule TO tanzschule;
   ```

2. Run the application:
   ```bash
   ./gradlew bootRun
   ```
   The service starts on `http://localhost:8080`.

3. Run tests:
   ```bash
   ./gradlew test
   ```

### Docker

Start the full stack (PostgreSQL + Backend):

```bash
docker compose -f docker-compose-tanzschule-family-and-friends-service.yml up --build -d
```

## API Endpoints

### Authentication

| Method | Endpoint          | Auth     | Description              |
|--------|-------------------|----------|--------------------------|
| POST   | `/api/auth/login` | Public   | Login, returns JWT token |

**Request:**
```json
{
  "username": "admin",
  "password": "admin"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOi...",
  "username": "admin"
}
```

### FAQ

| Method | Endpoint             | Auth     | Description                  |
|--------|----------------------|----------|------------------------------|
| GET    | `/api/faqs`          | Public   | Get all FAQs (sorted)        |
| GET    | `/api/faqs/{id}`     | Public   | Get FAQ by ID                |
| POST   | `/api/faqs`          | Required | Create a new FAQ             |
| PUT    | `/api/faqs/{id}`     | Required | Update an existing FAQ       |
| DELETE | `/api/faqs/{id}`     | Required | Delete a FAQ                 |
| PUT    | `/api/faqs/reorder`  | Required | Reorder FAQs by list of IDs  |

**FAQ Request:**
```json
{
  "question": "What courses do you offer?",
  "answer": "We offer salsa, bachata, kizomba, and more.",
  "displayOrder": 0
}
```

**Reorder Request:**
```json
[3, 1, 2]
```

Authenticated endpoints require a `Bearer` token in the `Authorization` header:
```
Authorization: Bearer eyJhbGciOi...
```

## Configuration

Configuration is done via `application.yml` and can be overridden with environment variables:

| Environment Variable | Default     | Description                |
|----------------------|-------------|----------------------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/tanzschule` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `tanzschule` | Database user |
| `SPRING_DATASOURCE_PASSWORD` | `tanzschule` | Database password |
| `JWT_SECRET` | *(dev default)* | JWT signing secret (min. 256 bits) |
| `JWT_EXPIRATION` | `86400000` | Token expiration in ms (default: 24h) |
| `ADMIN_USERNAME` | `admin` | Default admin username |
| `ADMIN_PASSWORD` | `admin` | Default admin password |

## Project Structure

```
src/main/java/de/tanzschule/service/
├── TanzschuleServiceApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── CorsConfig.java
├── auth/
│   ├── AdminUser.java
│   ├── AdminUserRepository.java
│   ├── AuthController.java
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── LoginRequest.java
│   └── LoginResponse.java
├── faq/
│   ├── Faq.java
│   ├── FaqRepository.java
│   ├── FaqService.java
│   ├── FaqController.java
│   ├── FaqRequest.java
│   └── FaqResponse.java
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

## Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

| Migration | Description |
|-----------|-------------|
| `V1__create_admin_user_table.sql` | Admin user table |
| `V2__create_faq_table.sql` | FAQ table with display_order |
