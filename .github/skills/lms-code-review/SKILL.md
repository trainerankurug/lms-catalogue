---
name: lms-code-review
description: Review changes to the LMS course catalogue for behavior, architecture fit, reusable code, tests, and documentation. Use for code reviews in this repository.
---

# LMS code review

Use this skill when reviewing changes in this LMS repository. Review only; do
ot modify files.

## Review workflow

1. Establish the change under review from the supplied diff, files, or request.
   If the changed scope is unclear, state what could not be established rather
   than assuming unrelated files are part of the change.
2. Read the relevant implementation and its callers/callees to understand the
   behavior and architecture before judging the change.
3. Check tests that exercise the changed behavior and whether important new
   behavior or failure cases lack coverage.
4. Check whether relevant documentation still describes the API, behavior, and
   supported commands accurately.
5. Report only substantiated, actionable findings. Include a file and line,
   impact, and a focused fix or test. Do not report style, formatting, or
   comment preferences.

## LMS architecture and invariants

- Backend request flow: `CourseController` -> `CourseService` ->
  `CourseRepository` -> `Course`. Controllers map entities to response DTOs
  through `CourseMapper`; do not expose persistence entities as API responses.
- Catalogue read and error auditing goes through `CatalogAuditLogger`.
- `CourseService.listActive()` returns active courses; code detail lookup has
  different semantics and may return an inactive course.
- Course code lookups are case-insensitive. Public course levels are
  `FOUNDATION`, `INTERMEDIATE`, and `ADVANCED`.
- The backend uses Spring Boot, Java 21, H2 in MySQL compatibility mode, and
  Flyway migrations. Schema changes require a numbered migration and must
  remain compatible with schema validation.
- Frontend API calls are centralized in `frontend/src/api/catalog.js` and use
  relative `/api/courses` URLs. `App.jsx` owns catalogue state;
  `constants.js` owns display-level labels; `LevelFilter` uses `null` for all
  levels.
- The root `README.md` documents the current architecture, API, and build/test
  commands. Check it when a change alters any of these.

## Reuse review

Before calling for new logic or abstractions, search for existing helpers,
service methods, mappers, repository queries, UI components, and shared
constants that already serve the same purpose. Trace their callers and compare
their behavior with the change.

Flag only meaningful duplication or bypasses of existing responsibilities,
such as reimplementing course-code parsing, DTO mapping, API access, level
labels, validation, or audit logging. Prefer extending an appropriate existing
component over introducing a parallel implementation. Do not recommend
abstractions for hypothetical reuse; a new abstraction is reasonable when it
removes actual duplication or establishes a clear existing boundary.

## Test and documentation context

Existing backend test classes cover controller responses
(`CourseControllerTest`), DTO mapping (`CourseMapperTest`), and legacy course
code parsing (`LegacyCourseCodeParserTest`). Backend tests run with `mvn test`
from `backend/`; the frontend currently has a production build command but no
test or lint script in `frontend/package.json`.

Assess coverage relevant to the changed behavior. Do not treat the absence of a
test in an unrelated area as a finding.
