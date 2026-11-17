# Spaced Repetition (FSRS v5) — Product Specification

> FSRS-inspired state transitions, retrievability `R`, implicit accuracy/latency rating mapping, and 70/30 `DAILY_REVIEW` queue blending in `SpacedRepetitionAlgorithm`, persisted via `SrsDao` and rendered in `SpacedRepetitionScreen`.

Last updated: 2026-11-16

## Overview

Implements `Docs/prd/spaced-repetition.md` with `SpacedRepetitionAlgorithm` (`calculateFsrsNextReview`, `retrievability`, `deriveVocabRating`, `deriveSentenceRating`, `buildDailyBlendedQueue`, and legacy `calculateNextReview` compatibility), reactive Room queries in `SrsDao`, and `SpacedRepetitionScreen` (surfacing `dailyBlendedQueue` via `refreshDailyBlendedQueue()`). Note: `calculateFsrsNextReview` uses simplified difficulty-weighted stability multipliers rather than full 19-parameter FSRS-5 `w[]` vector optimization, while preserving exact FSRS state transitions (`NEW`, `LEARNING`, `REVIEW`, `RELEARNING`) and retrievability `R = (1 + (19/81) * (t / S))^-0.5`.

## Architecture

- **Domain:** `SpacedRepetitionAlgorithm` (`calculateNextReview`, `gradeToQuality`) and `ReviewGrade`.
- **Data:** `SrsDao` (`getDueItems`, `getDueCount`, `insertOrUpdate`) orchestrated by `CurriculumRepository.recordSrsResult`.
- **Presentation:** `SpacedRepetitionScreen` connected to `LanguageLearnerViewModel.dueSrsItems`.

## Data flow

1. `LanguageLearnerViewModel` collects `repository.getDueSrsItems()` and `repository.getDueSrsCount()` into `StateFlow` properties.
2. `SpacedRepetitionScreen` renders the current `SrsReviewEntity` prompt (`prompt` or `secondaryText` when Latin script is enabled) and a "Показать ответ" (Show Answer) button.
3. When the learner reveals the answer and taps a `ReviewGrade` button, `viewModel.submitSrsGrade(item, grade)` invokes `CurriculumRepository.recordSrsResult(item, grade)`.
4. `recordSrsResult` converts `grade` via `gradeToQuality`, computes the next `SrsReviewEntity` via `calculateNextReview`, and writes it to `SrsDao.insertOrUpdate`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `SpacedRepetitionAlgorithm` | `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` | Pure SM-2 interval, repetition, and ease-factor calculation. |
| `SpacedRepetitionScreen` | `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` | Flashcard prompt/reveal UI, 4-button grading bar, and empty-queue completion state. |
| `SrsDao` | `app/src/main/java/com/example/data/local/SrsDao.kt` | Room queries for due items, due count, and upserts. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| Out-of-range quality score passed to `calculateNextReview` | Clamped via `quality.coerceIn(0, 5)`. |
| Repeated failed reviews (`AGAIN` / `HARD`) | `easeFactor` is floored at `MIN_EASE_FACTOR` (`1.3f`) and `intervalDays` resets to `1`. |
| Due queue becomes empty | `SpacedRepetitionScreen` renders the `"Очередь чиста!"` empty state with a back button. |

## Dependencies

- Room (`androidx.room`) for `srs_reviews` persistence.

## Cross-references

- `Docs/prd/spaced-repetition.md` — the requirements this specification implements.
- `Docs/tests/spaced-repetition.md` — the test cases that verify this design.
- [[srs-and-audio]] — SRS and audio module overview.

## Sources

- `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` — SM-2 implementation.
- `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` — flashcard review composable.
- `app/src/main/java/com/example/data/local/SrsDao.kt` — `srs_reviews` DAO.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `recordSrsResult` and `enrollInSrs`.
- `Docs/prd/spaced-repetition.md` — requirements this specification implements.
- `Docs/tests/spaced-repetition.md` — test cases.
