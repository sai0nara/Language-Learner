# Data Layer

> Room database, entities, DAOs, initial curriculum seed, and XML/CSV/JSON importer.

Last updated: 2026-11-16

## Persistence & Repository

`AppDatabase` (version 3) hosts six Room entities (`LessonEntity`, `LessonAnswerEntity`, `VocabItemEntity`, `GrammarGuideEntity`, `SentenceEntity`, `SrsReviewEntity`) with cascading foreign keys from lessons to child items. `CurriculumRepository` exposes reactive `Flow` streams, seeds the 16-lesson initial curriculum (`InitialCurriculumData`, each lesson covering all 9 `matrixCoordinate` cells), tracks two-tier progression gates (`MIN_PRACTICE_SAMPLE = 5` at `>= 70%` to unlock; `MASTERED` 4-condition gate), aggregates `matrixStatsJson` under a `Mutex`, enforces the First-Attempt Scoring Guard, and builds the 70/30 `DAILY_REVIEW` blended queue.

## Cross-references

- [[overview]] — high-level MVVM data flow.
- [[srs-and-audio]] — SRS entity updates driven by `SpacedRepetitionAlgorithm`.
- [[unit-and-ui-tests]] — in-memory Room and `CurriculumImporter` test coverage.

## Sources

- `app/src/main/java/com/example/data/local/AppDatabase.kt` — Room database definition and singleton provider.
- `app/src/main/java/com/example/data/model/Entities.kt` — Room entity schemas and foreign-key relationships.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — repository operations and mastery unlocking.
- `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` — JSON, CSV, and XML curriculum parsers.

Back to [[overview]]
