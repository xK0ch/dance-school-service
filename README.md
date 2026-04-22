# Tanzschule Family & Friends – Backend Service

REST API backend for the Tanzschule Family & Friends web application.

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.5**
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

1. Start a local PostgreSQL instance via Docker:
   ```bash
   docker run --rm -d --name tanzschule-db -p 5432:5432 -e POSTGRES_DB=tanzschule -e POSTGRES_USER=tanzschule -e POSTGRES_PASSWORD=tanzschule postgres:17-alpine
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

### Gallery Events

| Method | Endpoint                                          | Auth     | Description                         |
|--------|---------------------------------------------------|----------|-------------------------------------|
| GET    | `/api/gallery-events`                             | Public   | Get all events with images          |
| GET    | `/api/gallery-events/{id}`                        | Public   | Get event by ID with images         |
| POST   | `/api/gallery-events`                             | Required | Create a new gallery event          |
| PUT    | `/api/gallery-events/{id}`                        | Required | Update a gallery event              |
| DELETE | `/api/gallery-events/{id}`                        | Required | Delete event and all its images     |
| POST   | `/api/gallery-events/{id}/images`                 | Required | Upload image to event (multipart)   |
| DELETE | `/api/gallery-events/{id}/images/{imageId}`       | Required | Delete a single image               |
| PUT    | `/api/gallery-events/{id}/images/reorder`         | Required | Reorder images by list of IDs       |
| GET    | `/api/gallery-events/{id}/images/{imageId}/download` | Public | Download/display image file         |

**Gallery Event Request:**
```json
{
  "name": "Summer Dance Party",
  "date": "2026-07-15"
}
```

**Gallery Event Response:**
```json
{
  "id": 1,
  "name": "Summer Dance Party",
  "date": "2026-07-15",
  "images": [
    {
      "id": 1,
      "filename": "a1b2c3d4-...-e5f6.jpg",
      "originalFilename": "my-photo.jpg",
      "contentType": "image/jpeg",
      "fileSize": 204800,
      "displayOrder": 0,
      "galleryEventId": 1,
      "createdAt": "2026-03-28T12:00:00",
      "updatedAt": "2026-03-28T12:00:00"
    }
  ],
  "createdAt": "2026-03-28T12:00:00",
  "updatedAt": "2026-03-28T12:00:00"
}
```

**Upload Request (multipart/form-data):**
```
POST /api/gallery-events/1/images
Content-Type: multipart/form-data

file: <image file (JPEG, PNG, GIF, WebP)>
```

**Reorder Request:**
```json
[3, 1, 2]
```

### Course Categories

| Method | Endpoint                          | Auth     | Description                              |
|--------|-----------------------------------|----------|------------------------------------------|
| GET    | `/api/course-categories`          | Public   | Get all categories with courses (sorted) |
| GET    | `/api/course-categories/{id}`     | Public   | Get category by ID with courses          |
| POST   | `/api/course-categories`          | Required | Create a new category                    |
| PUT    | `/api/course-categories/{id}`     | Required | Update a category                        |
| DELETE | `/api/course-categories/{id}`     | Required | Delete a category                        |
| PUT    | `/api/course-categories/reorder`  | Required | Reorder categories by list of IDs        |

**Course Category Request:**
```json
{
  "name": "Erwachsene",
  "displayOrder": 0
}
```

**Course Category Response:**
```json
{
  "id": 1,
  "name": "Erwachsene",
  "displayOrder": 0,
  "courses": [
    {
      "id": 1,
      "name": "Welttanzprogramm Teil 1",
      "startDate": "2026-05-01",
      "startTime": "19:45:00",
      "endTime": "21:30:00",
      "numberOfHours": "8 Doppelstunden",
      "teacher": "Uwe Höftmann",
      "remark": "Anfängerkurs für Paare",
      "partnerOption": true,
      "categoryId": 1,
      "tariffs": [
        { "id": 1, "name": "Normal", "price": 78.00, "courseId": 1, "createdAt": "...", "updatedAt": "..." },
        { "id": 2, "name": "Wiederholer", "price": 65.00, "courseId": 1, "createdAt": "...", "updatedAt": "..." }
      ],
      "createdAt": "2026-04-06T12:00:00",
      "updatedAt": "2026-04-06T12:00:00"
    }
  ],
  "createdAt": "2026-04-06T12:00:00",
  "updatedAt": "2026-04-06T12:00:00"
}
```

**Reorder Request:**
```json
[3, 1, 2]
```

### Courses

| Method | Endpoint              | Auth     | Description                                |
|--------|-----------------------|----------|--------------------------------------------|
| GET    | `/api/courses/{id}`   | Public   | Get course by ID with tariffs              |
| POST   | `/api/courses`        | Required | Create a new course with tariffs           |
| PUT    | `/api/courses/{id}`   | Required | Update a course (replaces tariffs)         |
| DELETE | `/api/courses/{id}`   | Required | Delete a course and all its tariffs        |

**Course Request:**
```json
{
  "name": "Welttanzprogramm Teil 1",
  "startDate": "2026-05-01",
  "startTime": "19:45",
  "endTime": "21:30",
  "numberOfHours": "8 Doppelstunden",
  "teacher": "Uwe Höftmann",
  "remark": "Anfängerkurs für Paare",
  "partnerOption": true,
  "categoryId": 1,
  "tariffs": [
    { "name": "Normal", "price": 78.00 },
    { "name": "Wiederholer", "price": 65.00 }
  ]
}
```

### Events

| Method | Endpoint              | Auth     | Description                                  |
|--------|-----------------------|----------|----------------------------------------------|
| GET    | `/api/events`         | Public   | Get all events (sorted by date, display order) |
| GET    | `/api/events/{id}`    | Public   | Get event by ID with its time ranges         |
| POST   | `/api/events`         | Required | Create a new event (with optional time ranges) |
| PUT    | `/api/events/{id}`    | Required | Update an event (replaces time ranges)       |
| DELETE | `/api/events/{id}`    | Required | Delete an event and all its time ranges      |
| PUT    | `/api/events/reorder` | Required | Reorder events by list of IDs                |

**Event Request:**
```json
{
  "name": "Silvesterball",
  "date": "2026-12-31",
  "entryCost": 15.00,
  "entryCostWithCustomerCard": 10.00,
  "remark": "Dresscode: festlich",
  "timeRanges": [
    { "startTime": "18:00", "endTime": "20:00" },
    { "startTime": "21:00", "endTime": "23:59" }
  ]
}
```

Only `name` and `date` are required. `entryCost`, `entryCostWithCustomerCard`, `remark`, and the `timeRanges` list are all optional — an event can have zero, one, or several time ranges.

**Event Response:**
```json
{
  "id": "a1b2c3d4-...",
  "name": "Silvesterball",
  "date": "2026-12-31",
  "entryCost": 15.00,
  "entryCostWithCustomerCard": 10.00,
  "remark": "Dresscode: festlich",
  "displayOrder": 0,
  "timeRanges": [
    { "id": "b2c3d4e5-...", "startTime": "18:00:00", "endTime": "20:00:00", "eventId": "a1b2c3d4-...", "createdAt": "...", "updatedAt": "..." },
    { "id": "c3d4e5f6-...", "startTime": "21:00:00", "endTime": "23:59:00", "eventId": "a1b2c3d4-...", "createdAt": "...", "updatedAt": "..." }
  ],
  "createdAt": "2026-04-22T12:00:00",
  "updatedAt": "2026-04-22T12:00:00"
}
```

### News

| Method | Endpoint                          | Auth     | Description                              |
|--------|-----------------------------------|----------|------------------------------------------|
| GET    | `/api/news`                       | Public   | Get all news (sorted by display order)   |
| GET    | `/api/news/{id}`                  | Public   | Get news by ID                           |
| POST   | `/api/news`                       | Required | Create a new news entry                  |
| PUT    | `/api/news/{id}`                  | Required | Update a news entry                      |
| DELETE | `/api/news/{id}`                  | Required | Delete a news entry and its image        |
| POST   | `/api/news/{id}/image`            | Required | Upload/replace news image (multipart)    |
| DELETE | `/api/news/{id}/image`            | Required | Delete news image                        |
| GET    | `/api/news/{id}/image/download`   | Public   | Download/display news image file         |
| PUT    | `/api/news/reorder`               | Required | Reorder news by list of IDs              |

**News Request:**
```json
{
  "title": "Summer Dance Party",
  "description": "Join us for a fun evening of dancing!",
  "displayOrder": 0
}
```

**News Response:**
```json
{
  "id": "a1b2c3d4-...",
  "title": "Summer Dance Party",
  "description": "Join us for a fun evening of dancing!",
  "image": {
    "id": "b2c3d4e5-...",
    "filename": "a1b2c3d4-...-e5f6.jpg",
    "originalFilename": "party.jpg",
    "contentType": "image/jpeg",
    "fileSize": 204800,
    "displayOrder": 0,
    "newsId": "a1b2c3d4-...",
    "createdAt": "2026-04-13T12:00:00",
    "updatedAt": "2026-04-13T12:00:00"
  },
  "displayOrder": 0,
  "createdAt": "2026-04-13T12:00:00",
  "updatedAt": "2026-04-13T12:00:00"
}
```

**Upload Request (multipart/form-data):**
```
POST /api/news/{id}/image
Content-Type: multipart/form-data

file: <image file (JPEG, PNG, GIF, WebP)>
```

### Contact

| Method | Endpoint        | Auth   | Description                          |
|--------|-----------------|--------|--------------------------------------|
| POST   | `/api/contact`  | Public | Send a contact form message via email |

**Contact Request:**
```json
{
  "name": "Max Mustermann",
  "email": "max@example.com",
  "phone": "(optional) 0123 456789",
  "message": "I have a question about your courses."
}
```

Returns `200 OK` on success, `400 Bad Request` on validation errors, `503 Service Unavailable` if mail delivery fails.

Authenticated endpoints require a `Bearer` token in the `Authorization` header:
```
Authorization: Bearer eyJhbGciOi...
```

## API Documentation (OpenAPI / Swagger)

The API is documented using OpenAPI 3 via `springdoc-openapi`. When the application is running, the following endpoints are available:

| Endpoint             | Description                                  |
|----------------------|----------------------------------------------|
| `/v3/api-docs`       | OpenAPI specification as JSON                |
| `/v3/api-docs.yaml`  | OpenAPI specification as YAML                |
| `/swagger-ui.html`   | Interactive Swagger UI                       |

### Generating the OpenAPI JSON file

The `org.springdoc.openapi-gradle-plugin` is configured to start the application, fetch `/v3/api-docs`, and write the result to a file:

```bash
./gradlew generateOpenApiDocs
```

The generated file is written to:

```
build/openapi/openapi.json
```

This file is intended to be committed to the frontend repository and used by `ng-openapi-gen` to generate type-safe Angular services from the API contract.

### Conventions

- Each controller is annotated with `@Tag(name = "...", description = "...")` — the tag name determines the Angular service name in the generated frontend client (e.g. `Tag("News")` → `NewsService`).
- Public endpoints (e.g. login, contact) are annotated with `@SecurityRequirements` (empty) to opt out of the global JWT requirement.
- DTOs (request/response records) and field validation constraints (`@NotNull`, `@Size`, …) are picked up automatically and reflected in the spec.

## CI/CD (Jenkins)

The project uses a Jenkins pipeline for automated deployment. The `Jenkinsfile` reads sensitive configuration from Jenkins Credentials.

### Jenkinsfile

```groovy
pipeline {
  agent any

  environment {
    DB_PASSWORD = credentials('TANZSCHULE_DB_PASSWORD')
    JWT_SECRET = credentials('TANZSCHULE_JWT_SECRET')
    ADMIN_USERNAME = credentials('TANZSCHULE_ADMIN_USERNAME')
    ADMIN_PASSWORD = credentials('TANZSCHULE_ADMIN_PASSWORD')
    GALLERY_UPLOAD_DIR = '/srv/tanzschule/uploads/images'
    MAIL_HOST = credentials('TANZSCHULE_MAIL_HOST')
    MAIL_PORT = credentials('TANZSCHULE_MAIL_PORT')
    MAIL_USERNAME = credentials('TANZSCHULE_MAIL_USERNAME')
    MAIL_PASSWORD = credentials('TANZSCHULE_MAIL_PASSWORD')
  }

  stages {
    stage('Deploy') {
      steps {
        sh 'docker compose -f docker-compose-tanzschule-family-and-friends-service.yml down'
        sh 'docker image prune -af'
        sh 'docker compose -f docker-compose-tanzschule-family-and-friends-service.yml up --build -d'
      }
    }
  }
}
```

### Jenkins Credentials Setup

Under **Manage Jenkins > Credentials > Global**, create the following credentials as **Secret text**:

| Credential ID                    | Description                               | Example Value                          |
|----------------------------------|-------------------------------------------|----------------------------------------|
| `TANZSCHULE_DB_PASSWORD`         | PostgreSQL password                       | `my-secure-db-password`                |
| `TANZSCHULE_JWT_SECRET`          | JWT signing secret (min. 32 characters)   | `a3f8b2...` (long random string)       |
| `TANZSCHULE_ADMIN_USERNAME`      | Admin username                            | `admin`                                |
| `TANZSCHULE_ADMIN_PASSWORD`      | Admin password                            | `my-secure-admin-password`             |
| `TANZSCHULE_MAIL_HOST`           | SMTP server hostname                      | `smtp.example.com`                     |
| `TANZSCHULE_MAIL_PORT`           | SMTP server port                          | `587`                                  |
| `TANZSCHULE_MAIL_USERNAME`       | SMTP username / sender address            | `noreply@tsfaf.de`                     |
| `TANZSCHULE_MAIL_PASSWORD`       | SMTP password                             | `my-mail-password`                     |

`GALLERY_UPLOAD_DIR` is set directly in the Jenkinsfile as it is not a secret.

## Configuration

Configuration is done via `application.yml` and can be overridden with environment variables:

| Environment Variable         | Default                                       | Description                          |
|------------------------------|-----------------------------------------------|--------------------------------------|
| `SPRING_DATASOURCE_URL`     | `jdbc:postgresql://localhost:5432/tanzschule` | Database URL          |
| `SPRING_DATASOURCE_USERNAME`| `tanzschule`                                  | Database user                        |
| `SPRING_DATASOURCE_PASSWORD`| `tanzschule`                                  | Database password                    |
| `JWT_SECRET`                 | *(dev default)*                               | JWT signing secret (min. 256 bits)   |
| `JWT_EXPIRATION`             | `86400000`                                    | Token expiration in ms (default: 24h)|
| `ADMIN_USERNAME`             | `admin`                                       | Default admin username               |
| `ADMIN_PASSWORD`             | `admin`                                       | Default admin password               |
| `GALLERY_UPLOAD_DIR`         | `./uploads/gallery`                           | Directory for uploaded image files   |
| `MAIL_HOST`                  | `smtp.example.com`                            | SMTP server hostname                 |
| `MAIL_PORT`                  | `587`                                         | SMTP server port                     |
| `MAIL_USERNAME`              | `noreply@example.com`                         | SMTP username / sender address       |
| `MAIL_PASSWORD`              | `changeme`                                    | SMTP password                        |
| `CONTACT_RECIPIENT`          | `mail@fynn-koch.de`                           | Email address that receives contact messages |
| `CORS_ALLOWED_ORIGINS`      | `http://localhost:4200`                       | Allowed CORS origins                 |

## Project Structure

```
src/main/java/de/tanzschule/service/
├── TanzschuleServiceApplication.java
├── common/
│   ├── BaseEntity.java
│   └── BaseResponse.java
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
├── gallery/
│   ├── GalleryEvent.java
│   ├── GalleryEventRepository.java
│   ├── GalleryEventService.java
│   ├── GalleryEventController.java
│   ├── GalleryEventRequest.java
│   └── GalleryEventResponse.java
├── image/
│   ├── Image.java
│   ├── ImageRepository.java
│   ├── ImageService.java
│   └── ImageResponse.java
├── event/
│   ├── Event.java
│   ├── EventController.java
│   ├── EventRepository.java
│   ├── EventRequest.java
│   ├── EventResponse.java
│   ├── EventService.java
│   ├── EventTimeRange.java
│   ├── EventTimeRangeRepository.java
│   ├── EventTimeRangeRequest.java
│   └── EventTimeRangeResponse.java
├── course/
│   ├── Course.java
│   ├── CourseCategory.java
│   ├── CourseCategoryController.java
│   ├── CourseCategoryRepository.java
│   ├── CourseCategoryRequest.java
│   ├── CourseCategoryResponse.java
│   ├── CourseCategoryService.java
│   ├── CourseController.java
│   ├── CourseRepository.java
│   ├── CourseRequest.java
│   ├── CourseResponse.java
│   ├── CourseService.java
│   ├── CourseTariff.java
│   ├── CourseTariffRepository.java
│   ├── CourseTariffRequest.java
│   └── CourseTariffResponse.java
├── news/
│   ├── News.java
│   ├── NewsRepository.java
│   ├── NewsService.java
│   ├── NewsController.java
│   ├── NewsRequest.java
│   └── NewsResponse.java
├── contact/
│   ├── ContactRequest.java
│   ├── ContactService.java
│   └── ContactController.java
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
| `V3__create_gallery_event_table.sql` | Gallery event table |
| `V4__create_news_table.sql` | News table |
| `V5__create_image_table.sql` | Image table with FKs to gallery_event and news |
| `V6__create_course_category_table.sql` | Course category table |
| `V7__create_course_table.sql` | Course table with FK to course_category |
| `V8__create_course_tariff_table.sql` | Course tariff table with FK to course |
| `V9__create_event_table.sql` | Event table (name, date, optional entry costs, remark, display order) |
| `V10__create_event_time_range_table.sql` | Event time range table with FK to event (multiple per event) |
