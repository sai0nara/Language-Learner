# Specification: Establish Core Test Suite for SRS, Curriculum Importer, and Room Persistence

## Overview
Establish a reliable automated unit and integration test suite for the core offline-first modules of **Language Learner**:
1. **Spaced Repetition Algorithm (`srs/SpacedRepetitionAlgorithm.kt`)**
2. **Curriculum Importer (`data/importer/CurriculumImporter.kt`)**
3. **Room Persistence & DAOs (`data/local/*`, `data/repository/CurriculumRepository.kt`)**

## Functional Requirements
- **FR1 — SRS Scheduling Verification:** Verify interval calculation, ease factor updates, repetition counts, and lapse/reset behavior across all review quality ratings.
- **FR2 — Curriculum Importer Verification:** Verify parsing and validation for XML, CSV, and JSON curriculum inputs, including malformed/edge-case inputs and silent fallback behavior.
- **FR3 — Room DAO & Repository Verification:** Verify CRUD operations, queries, type converters, and repository flows using an in-memory Room database under Robolectric.

## Non-Functional Requirements
- **NFR1 — Offline Execution:** All tests must run deterministically offline via `./gradlew testDebugUnitTest` without network access or external services.
- **NFR2 — Coverage Target:** Align with the project workflow target of >80% code coverage on the covered core modules.

## Acceptance Criteria
- Unit tests for `SpacedRepetitionAlgorithm` pass and cover normal, boundary, and lapse cases.
- Unit tests for `CurriculumImporter` pass for XML, CSV, and JSON formats as well as invalid/empty inputs.
- Robolectric integration tests for `AppDatabase`, DAOs, and `CurriculumRepository` pass using an in-memory Room database.
- `./gradlew testDebugUnitTest` completes cleanly with all tests passing.
