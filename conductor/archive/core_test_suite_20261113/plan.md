# Implementation Plan: Establish Core Test Suite for SRS, Curriculum Importer, and Room Persistence

## Phase 1: Spaced Repetition Algorithm Tests
- [x] Task: Audit `SpacedRepetitionAlgorithm.kt` and existing test setup in `app/src/test`
    - [x] Review `SpacedRepetitionAlgorithm.kt` inputs, outputs, and edge cases
    - [x] Verify `./gradlew testDebugUnitTest` baseline execution
- [x] Task: Add unit tests for `SpacedRepetitionAlgorithm`
    - [x] Write failing/verification tests for initial card scheduling, ease factor bounds, and interval progression
    - [x] Write tests for lapse/failure resets and boundary ratings
    - [x] Fix any discovered edge-case defects and confirm all SRS tests pass
- [x] Task: Conductor - User Manual Verification 'Phase 1: Spaced Repetition Algorithm Tests' (Protocol in workflow.md)

## Phase 2: Curriculum Importer Tests
- [x] Task: Audit `CurriculumImporter.kt` for XML, CSV, and JSON parsing paths
    - [x] Identify supported schemas, required fields, and fallback/error handling
- [x] Task: Add unit tests for XML, CSV, and JSON import
    - [x] Write tests for valid XML, CSV, and JSON curriculum imports
    - [x] Write tests for malformed inputs, missing optional fields, and empty payloads
    - [x] Address any parser edge-case issues and confirm all importer tests pass
- [x] Task: Conductor - User Manual Verification 'Phase 2: Curriculum Importer Tests' (Protocol in workflow.md)

## Phase 3: Room Persistence & Repository Tests
- [x] Task: Audit `AppDatabase.kt`, DAOs, `Converters.kt`, and `CurriculumRepository.kt`
    - [x] Verify Robolectric and `kotlinx-coroutines-test` configuration for in-memory Room testing
- [x] Task: Add Robolectric integration tests for DAOs and `CurriculumRepository`
    - [x] Write tests for `LessonDao`, `VocabDao`, `SentenceDao`, `GrammarDao`, `SrsDao`, and `LessonAnswerDao`
    - [x] Write tests for `CurriculumRepository` reactive flows and persistence operations
    - [x] Confirm full test suite (`./gradlew testDebugUnitTest`) passes
- [x] Task: Conductor - User Manual Verification 'Phase 3: Room Persistence & Repository Tests' (Protocol in workflow.md)
