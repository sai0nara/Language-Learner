# Curriculum Progression & Mastery Unlocking — Product Specification

> Reactive Room `Flow` streams in `CurriculumRepository` and `LanguageLearnerViewModel` drive `CurriculumScreen` and `LessonDetailScreen`, with 100-answer rolling window pruning in `LessonAnswerDao`.

Last updated: 2026-11-13

## Overview

Implements `Docs/prd/curriculum-progression.md` using a reactive MVVM architecture where `CurriculumRepository` manages initial data seeding and rolling mastery updates, and `LanguageLearnerViewModel` exposes `StateFlow` streams to Compose screens.

## Architecture

- **Navigation:** `AppNavigation` in `MainActivity.kt` maintains a Compose `mutableStateListOf<Screen>` back stack.
- **State holder:** `LanguageLearnerViewModel` exposes `allLessons`, `activeLesson`, `isLatinScript`, and `isDarkTheme`.
- **Persistence:** `LessonDao` and `LessonAnswerDao` in `AppDatabase`.

## Data flow

1. On `LanguageLearnerViewModel` initialization, `repository.ensureInitialDataLoaded()` checks if `lessons` is empty and inserts `InitialCurriculumData` if needed.
2. `CurriculumScreen` collects `allLessons`, `dueSrsCount`, `isLatinScript`, and `isDarkTheme` via `collectAsStateWithLifecycle()`.
3. When a user completes an item in `SentencePracticeScreen` or `VocabDrillScreen`, `viewModel.recordLessonAnswer(isCorrect)` calls `CurriculumRepository.recordAnswer(lessonId, isCorrect)`.
4. `recordAnswer` inserts a `LessonAnswerEntity`, calls `LessonAnswerDao.pruneOldAnswers(lessonId)` to keep only the latest 100 rows, recalculates `score = (correctCount / 100f) * 5.0f`, updates `LessonEntity` stats, and calls `LessonDao.unlockLessonByOrder(current.orderIndex + 1)` when `score >= 4.5f`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `MainActivity` / `AppNavigation` | `app/src/main/java/com/example/MainActivity.kt` | Hosts Compose content, theme state, and screen back stack. |
| `CurriculumScreen` | `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` | Displays lessons tab, SRS tab, import tab, and script/theme toggles. |
| `LessonDetailScreen` | `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` | Displays lesson progress and launches sentence practice, vocab drills, or grammar matrix. |
| `LessonDao` & `LessonAnswerDao` | `app/src/main/java/com/example/data/local/LessonDao.kt` | Queries/updates lessons and prunes rolling 100-answer history. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| App launched multiple times after initial seed | `ensureInitialDataLoaded` checks `existing.isEmpty()` before inserting, preventing duplicate rows. |
| Answer count exceeds 100 for a lesson | `LessonAnswerDao.pruneOldAnswers` deletes rows outside the 100 most recent timestamps. |
| Tapping a locked lesson card | `CurriculumScreen` shows a toast explaining that a 4.5 grade on the previous lesson is required and does not navigate. |

## Dependencies

- Jetpack Compose Material 3 (`androidx.compose.material3`).
- Room (`androidx.room`) and Kotlin Coroutines `StateFlow`.

## Cross-references

- `Docs/prd/curriculum-progression.md` — the requirements this specification implements.
- `Docs/tests/curriculum-progression.md` — the test cases that verify this design.
- [[overview]] — high-level MVVM architecture.

## Sources

- `app/src/main/java/com/example/MainActivity.kt` — `Screen` sealed interface and `AppNavigation`.
- `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` — curriculum tabs and lesson cards.
- `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` — lesson detail module rows.
- `app/src/main/java/com/example/data/local/LessonDao.kt` — lesson queries and grade updates.
- `app/src/main/java/com/example/data/local/LessonAnswerDao.kt` — 100-answer window queries and pruning.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `ensureInitialDataLoaded` and `recordAnswer`.
- `Docs/prd/curriculum-progression.md` — requirements this specification implements.
- `Docs/tests/curriculum-progression.md` — test cases.
