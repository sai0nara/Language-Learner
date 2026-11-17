# Curriculum Progression & Mastery Unlocking — Product Specification

> Reactive Room `Flow` streams in `CurriculumRepository` and `LanguageLearnerViewModel` drive `CurriculumScreen` and `LessonDetailScreen`, with 100-answer rolling window pruning in `LessonAnswerDao`.

Last updated: 2026-11-17

## Overview

Implements `Docs/prd/curriculum-progression.md` using a reactive MVVM architecture where `CurriculumRepository` manages initial 6-lesson data seeding (`orderIndex` `1..6`) and rolling mastery updates, and `LanguageLearnerViewModel` exposes `StateFlow` streams to Compose screens.

## Architecture

- **Navigation:** `AppNavigation` in `MainActivity.kt` maintains a Compose `mutableStateListOf<Screen>` back stack.
- **State holder:** `LanguageLearnerViewModel` exposes `allLessons`, `activeLesson`, and `isDarkTheme` (in-memory default `true`).
- **Persistence:** `LessonDao` (`ORDER BY orderIndex ASC`, `updateGradeStats`, `unlockLessonByOrder`) and `LessonAnswerDao` (`pruneOldAnswers`, `getRecentAnswers`) in `AppDatabase`.

## Data flow

1. On `LanguageLearnerViewModel` initialization, `repository.ensureInitialDataLoaded()` checks if `lessons` is empty and inserts the 6 lessons (`lesson_1`..`lesson_6`, `orderIndex` `1..6`, Lesson 1 `isUnlocked = true`, Lessons 2–6 `isUnlocked = false`), vocabulary, grammar guides, sentences, and SRS rows from `InitialCurriculumData`.
2. `CurriculumScreen` collects `allLessons`, `dueSrsCount`, and `isDarkTheme` via `collectAsStateWithLifecycle()`.
3. When a user completes an item in `SentencePracticeScreen` or `VocabDrillScreen`, `viewModel.recordAnswer(lessonId, isCorrect)` calls `CurriculumRepository.recordAnswer(lessonId, isCorrect)`.
4. `recordAnswer` inserts a `LessonAnswerEntity`, calls `LessonAnswerDao.pruneOldAnswers(lessonId)` (`DELETE FROM lesson_answers WHERE lessonId = :lessonId AND id NOT IN (SELECT id FROM lesson_answers WHERE lessonId = :lessonId ORDER BY timestamp DESC LIMIT 100)`), queries `getRecentAnswers(lessonId, limit = 100)`, recalculates `score = (correctCount.toFloat() / 100f) * 5.0f` and `isCompleted = score >= 4.5f`, updates `LessonEntity` stats via `LessonDao.updateGradeStats`, and calls `LessonDao.unlockLessonByOrder(current.orderIndex + 1)` when `isCompleted` is `true`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `MainActivity` / `AppNavigation` | `app/src/main/java/com/example/MainActivity.kt` | Hosts Compose content, theme state (`LanguageLearnerTheme(darkTheme = isDark)`), and screen back stack. |
| `LanguageLearnerViewModel` | `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` | Holds in-memory `isDarkTheme = true` `MutableStateFlow`, exposes `allLessons`, and triggers `recordAnswer`. |
| `CurriculumScreen` | `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` | Displays lessons tab, SRS tab, import tab, floating `"Продолжить: ${active.title}"` button, locked-lesson toast, and theme toggle. |
| `LessonDetailScreen` | `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` | Displays `"Lesson ${lesson.orderIndex}"` header, lesson progress, and launches sentence practice, vocab drills, or grammar matrix. |
| `LessonDao` & `LessonAnswerDao` | `app/src/main/java/com/example/data/local/LessonDao.kt` | Queries/updates lessons (`updateGradeStats`, `unlockLessonByOrder`) and prunes rolling 100-answer history. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| App launched multiple times after initial seed | `ensureInitialDataLoaded` checks `existing.isEmpty()` before inserting, preventing duplicate rows. |
| Answer count exceeds 100 for a lesson (including equal timestamps) | `LessonAnswerDao.pruneOldAnswers` retains 100 rows using `ORDER BY timestamp DESC LIMIT 100` (equal timestamps break ties via SQLite internal rowid/id scan order). |
| Final lesson (`orderIndex = 6` or max `orderIndex`) reaches `score >= 4.5f` | `updateGradeStats` marks the final lesson `isCompleted = true`, and `unlockLessonByOrder(current.orderIndex + 1)` executes safely as a 0-row SQL `UPDATE` no-op. |
| Subsequent wrong answers drop a mastered lesson below `4.5f` | `updateGradeStats` sets `isCompleted = false` on that lesson (`Mastered` → `Unlocked`), while any previously unlocked `orderIndex + 1` lesson remains `isUnlocked = true`. |
| Tapping a locked lesson card (`!lesson.isUnlocked`) | `CurriculumScreen` blocks navigation and displays `Toast.makeText(context, "Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}", Toast.LENGTH_SHORT)`. |
| App process restart after toggling theme | `isDarkTheme` (`true` / Dark) is an in-memory `MutableStateFlow` and resets to its default on cold start. |
| Schema version bump (`AppDatabase` v3 → v4) | While the app is unreleased, `AppDatabase` uses `fallbackToDestructiveMigration()` on version bumps to re-seed Latin-only initial curriculum data cleanly. |
| Imported lessons (`CurriculumRepository.importCurriculum`) | Uses 1-based `orderIndex` (`1..L`, `99`, or `100`), forces `idx == 0` in the imported batch to `isUnlocked = true` (`idx > 0` `isUnlocked = false`, `masteryScore = 0f`), and upserts via `OnConflictStrategy.REPLACE` (cascading delete of `lesson_answers` only for replaced `lessonId`s). |

## Dependencies

- Jetpack Compose Material 3 (`androidx.compose.material3`).
- Room (`androidx.room`) and Kotlin Coroutines `StateFlow`.

## Cross-references

- `Docs/prd/curriculum-progression.md` — the requirements this specification implements.
- `Docs/tests/curriculum-progression.md` — the test cases that verify this design.
- `Docs/psd/curriculum-import.md` — specification for imported lesson persistence.
- [[overview]] — high-level MVVM architecture.

## Sources

- `app/src/main/java/com/example/MainActivity.kt` — `Screen` sealed interface and `AppNavigation`.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `isDarkTheme`, `selectLesson`, and `recordAnswer`.
- `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` — curriculum tabs and lesson cards.
- `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` — lesson detail module rows.
- `app/src/main/java/com/example/data/local/LessonDao.kt` — lesson queries and grade updates.
- `app/src/main/java/com/example/data/local/LessonAnswerDao.kt` — 100-answer window queries and pruning.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `ensureInitialDataLoaded` and `recordAnswer`.
- `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` — 6-lesson offline seed data.
- `Docs/prd/curriculum-progression.md` — requirements this specification implements.
- `Docs/tests/curriculum-progression.md` — test cases.
