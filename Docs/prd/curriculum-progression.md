# Curriculum Progression & Mastery Unlocking — Product Requirements

> Provides a structured lesson dashboard with rolling 100-answer mastery scoring, automatic lesson unlocking, script toggling (Cyrillic/Latin), and theme switching.

Last updated: 2026-11-14

## Overview

Learners progress through ordered lessons seeded from `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` or imported decks. `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt`, `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt`, and `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` present lesson status, mastery scores, script preferences (Serbian Cyrillic vs. Latin), and entry points into drills and grammar guides.

## Goals / Non-goals

**Goals**

- Seed an offline 6-lesson Serbian-for-Russian-speakers B1 curriculum (`orderIndex` `1..6`) on first launch.
- Track rolling accuracy over the last 100 answers per lesson and calculate a `0.0f..5.0f` mastery grade.
- Unlock the next sequential lesson (`orderIndex + 1`) automatically once the learner reaches a mastery score of `4.5f` or higher (`90/100` correct).
- Let learners toggle between Cyrillic and Latin script and between dark and light Material 3 themes during their session.

**Non-goals**

- Server-side leaderboards or remote user profile synchronization.
- Cross-process persistence of script/theme toggles (held in-memory in `LanguageLearnerViewModel`).

## User stories

- As a learner, I want Lesson 1 (`orderIndex = 1`) unlocked out of the box and subsequent lessons (`orderIndex = 2..6`) unlocked sequentially as I master each topic (`>= 4.5f` grade) so that I follow a clear progression.
- As a learner of Serbian, I want to toggle between Cyrillic and Latin script across the app so that I can practice reading both alphabets.
- As a learner, I want to open a lesson's detail screen and choose between sentence construction, vocabulary drills, and the 3x3 grammar matrix.

## Functional requirements

- **FR-1 (Idempotent 1-based initial seeding):** When the `lessons` table is empty on `LanguageLearnerViewModel` init, `CurriculumRepository.ensureInitialDataLoaded()` seeds 6 lessons (`lesson_1`..`lesson_6` with 1-based `orderIndex` `1..6`, where `lesson_1` has `isUnlocked = true` and `lesson_2`..`lesson_6` have `isUnlocked = false`, `isCompleted = false`, `masteryScore = 0f`), along with their vocabulary, grammar guides, sentences, and initial SRS cards. If `lessons` is non-empty, seeding is a no-op.
- **FR-2 (Rolling 100-answer window & mastery calculation):** Every drill/practice answer invokes `CurriculumRepository.recordAnswer(lessonId, isCorrect)`, which:
  1. Inserts a `LessonAnswerEntity(lessonId, isCorrect, timestamp = System.currentTimeMillis())` into `lesson_answers`.
  2. Executes `LessonAnswerDao.pruneOldAnswers(lessonId)` (`DELETE FROM lesson_answers WHERE lessonId = :lessonId AND id NOT IN (SELECT id FROM lesson_answers WHERE lessonId = :lessonId ORDER BY timestamp DESC LIMIT 100)`) and fetches the retained rows via `getRecentAnswers(lessonId, limit = 100)` (`ORDER BY timestamp DESC LIMIT 100`; equal-timestamp rows follow SQLite's internal rowid/id scan order).
  3. Computes `correctCount = recentAnswers.count { it.isCorrect }`, `totalCount = recentAnswers.size`, `masteryScore = (correctCount.toFloat() / 100f) * 5.0f`, and `isCompleted = masteryScore >= 4.5f` (`>= 90` correct out of 100), persisting all four fields to `lessons` via `LessonDao.updateGradeStats`.
- **FR-3 (Sequential unlocking, final-lesson boundary, and regression behavior):**
  - When `isCompleted` is `true` (`masteryScore >= 4.5f`), `recordAnswer` looks up the current lesson and invokes `LessonDao.unlockLessonByOrder(current.orderIndex + 1)` (`UPDATE lessons SET isUnlocked = 1 WHERE orderIndex = :orderIndex`).
  - If the mastered lesson is the final lesson (no row exists at `orderIndex + 1`), `unlockLessonByOrder` completes safely as a 0-row no-op while the final lesson is marked `isCompleted = true`.
  - If subsequent wrong answers in the rolling 100-answer window drop `masteryScore < 4.5f`, `updateGradeStats` reverts `isCompleted` to `false` on that lesson (`Mastered` → `Unlocked`), while any previously unlocked next lesson (`orderIndex + 1`) remains `isUnlocked = true` (never re-locked).
  - For imported curricula (specified in `Docs/prd/curriculum-import.md`), imported lessons use 1-based `orderIndex` (`1..L`, `99`, or `100`), force the first imported lesson (`idx == 0`) to `isUnlocked = true` (`idx > 0` `isUnlocked = false`, `masteryScore = 0f`), and upsert via `OnConflictStrategy.REPLACE` (cascading `ON DELETE CASCADE` on `lesson_answers` only when an existing `lessonId` is replaced).
- **FR-4 (Locked-lesson guard & `CurriculumScreen` / `LessonDetailScreen` UX):**
  - Tapping an unlocked lesson card (`lesson.isUnlocked == true`) selects the lesson in `LanguageLearnerViewModel` and navigates to `LessonDetailScreen`.
  - Tapping a locked lesson card (`lesson.isUnlocked == false`) blocks navigation and shows a `Toast.LENGTH_SHORT` with the exact text `"Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}"`, while the card itself renders `"Заблокировано (требуется оценка 4.5 в предыдущем уроке)"`.
  - The floating bottom button (`resume_active_lesson_button`, `"Продолжить: ${active.title}"`) targets the first unlocked incomplete lesson (`lessons.firstOrNull { it.isUnlocked && !it.isCompleted } ?: lessons.firstOrNull { it.isUnlocked } ?: lessons.first()`).
- **FR-5 (Script & theme toggles):**
  - `LanguageLearnerViewModel` initializes `isLatinScript = false` (Cyrillic default, displaying `"Српски језик (B1 • Ћирилица)"` in the top bar) and `isDarkTheme = true` (Dark theme default) in-memory.
  - Tapping `toggle_script_button` flips `isLatinScript` (updating the top-bar subtitle to `"Srpski jezik (B1 • Latinica)"` when `true` and toggling script in drills/practice); tapping `toggle_theme_button` flips `isDarkTheme` (`LanguageLearnerTheme(darkTheme = isDark)`). Toggles remain active for the `ViewModel` session and reset to defaults on cold process restart.

## Non-functional requirements

- **Offline-first:** Initial seeding and mastery calculations run locally in Room.
- **Zero-friction UX:** Lesson cards clearly indicate locked (lock icon), unlocked (formatted `0.0`–`4.4` grade badge), or mastered (`isCompleted == true`, emerald checkmark badge) states.

## Acceptance criteria

Defined in `Docs/psd/curriculum-progression.md` and verified in `Docs/tests/curriculum-progression.md`.

## Out of scope

- Multi-user profile switching.
- Persistent storage of `isLatinScript` / `isDarkTheme` across process death.

## Cross-references

- `Docs/psd/curriculum-progression.md` — the specification that implements these requirements.
- `Docs/tests/curriculum-progression.md` — the test cases that verify them.
- `Docs/prd/curriculum-import.md` — imported curriculum `orderIndex`, unlock, and `OnConflictStrategy.REPLACE` rules.
- [[ui-layer]] — where the screens and ViewModel state are explained.

## Sources

- `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` — main dashboard with tabs, script/theme toggles, locked-lesson toast, and lesson cards.
- `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` — per-lesson hub linking to sentence practice, vocab drills, and grammar matrix.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `isLatinScript` and `isDarkTheme` defaults/toggles, `allLessons`, and `recordAnswer`.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — initial seeding and rolling 100-answer mastery calculation.
- `app/src/main/java/com/example/data/local/LessonDao.kt` — `updateGradeStats` and `unlockLessonByOrder`.
- `app/src/main/java/com/example/data/local/LessonAnswerDao.kt` — `pruneOldAnswers` and `getRecentAnswers`.
- `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` — built-in 6-lesson offline curriculum seed data.
- `Docs/psd/curriculum-progression.md` — product specification.
- `Docs/tests/curriculum-progression.md` — test cases.
