# Data Layer

> Room database, entities, DAOs, initial curriculum seed, and XML/CSV/JSON importer.

Last updated: 2026-11-13

## Persistence & Repository

`AppDatabase` hosts six Room entities (`LessonEntity`, `LessonAnswerEntity`, `VocabItemEntity`, `GrammarGuideEntity`, `SentenceEntity`, `SrsReviewEntity`) with cascading foreign keys from lessons to child items. `CurriculumRepository` exposes reactive `Flow` streams, seeds initial data idempotently, tracks rolling 100-answer mastery scores to unlock subsequent lessons, and persists imported curricula.

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
