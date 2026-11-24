# LMS Course Catalogue

A working slice of the Global Learning LMS platform for browsing and maintaining the public course catalogue.

The project is split into a Spring Boot API and a React interface:

- **Backend:** Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security, Flyway, and H2
- **Frontend:** React 18 and Vite 5
- **Course data:** Stored in an in-memory H2 database and recreated from Flyway migrations whenever the backend starts

## Features

- List active courses
- Filter the catalogue by course level
- Search courses by code or title
- View an individual course
- Update a course
- Deactivate a course

Supported course levels are `FOUNDATION`, `INTERMEDIATE`, and `ADVANCED`.

## Project structure

```text
backend/
  src/main/java/com/globallearning/lms/catalog/
    domain/       Course entity and course levels
    repository/   JPA queries, including native keyword search
    service/      Catalogue operations and legacy code parsing
    web/          REST controllers, DTOs, mapping, and error handling
    audit/        Central catalogue audit logging
    config/       Spring Security configuration
  src/main/resources/
    db/migration/ Flyway schema and seed data
    application.yml
frontend/
  src/
    api/          Catalogue HTTP requests
    components/   Catalogue UI components
    App.jsx       Catalogue state and filtering
    constants.js  Shared level labels and filter values
```

## Prerequisites

- Java 21
- Maven
- Node.js and npm

## Run locally

The backend and frontend run as separate processes.

### 1. Start the API

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

The H2 database is in-memory and is reset on startup. Flyway creates the `course` table and loads the seed catalogue from:

- `backend/src/main/resources/db/migration/V1__create_course.sql`
- `backend/src/main/resources/db/migration/V2__seed_courses.sql`

### 2. Start the frontend

In a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

Vite proxies `/api` requests to `http://localhost:8080`, so the frontend and API can be developed on separate local ports.

## API

The API is rooted at `/api/courses`.

| Method | Endpoint | Purpose |
|---|---|---|
| `GET` | `/api/courses` | List active courses |
| `GET` | `/api/courses?level=ADVANCED` | Filter active courses by level |
| `GET` | `/api/courses?q=design` | Search active courses by code or title |
| `GET` | `/api/courses/{code}` | Get a course by code |
| `PUT` | `/api/courses/{code}` | Update title, description, and credits |
| `POST` | `/api/courses/{code}/deactivate` | Deactivate a course |

Example request:

```bash
curl http://localhost:8080/api/courses
curl "http://localhost:8080/api/courses?level=ADVANCED"
curl "http://localhost:8080/api/courses?q=design"
curl http://localhost:8080/api/courses/CS2110
```

Course update payload:

```json
{
  "title": "Object-Oriented Design",
  "description": "Updated course description.",
  "credits": 15
}
```

Validation:

- Title is required and limited to 200 characters
- Description is limited to 2000 characters
- Credits are required and must be between 5 and 60

API errors use JSON responses. Missing courses return `COURSE_NOT_FOUND`; invalid update payloads return `VALIDATION_FAILED` with field-level errors.

## Build and test

Run all backend tests:

```bash
cd backend
mvn test
```

Run a single backend test class:

```bash
cd backend
mvn -Dtest=CourseControllerTest test
```

Run a single backend test method:

```bash
cd backend
mvn -Dtest=LegacyCourseCodeParserTest#mapsLowBandsToFoundation test
```

Build the frontend:

```bash
cd frontend
npm run build
```

There is no frontend test or lint script configured in `frontend/package.json`.

## Code review agent

The workspace includes a read-only **LMS Code Reviewer** custom agent in
`.github/agents/lms-code-reviewer.agent.md`. It uses the
`.github/skills/lms-code-review/SKILL.md` skill for project-specific review,
including architecture, reuse, tests, and documentation. The independently
applied `.github/instructions/lms-review-skill-usage.instructions.md` requires
the review to verify and report how existing code was considered for reuse.
