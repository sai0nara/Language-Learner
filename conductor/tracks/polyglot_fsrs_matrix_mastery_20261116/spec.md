# Track Specification: Polyglot FSRS v5, 3x3 Matrix Mastery Gates & Weak-Cell Remediation

## Overview
Upgrade the offline-first Language Learner app to implement the updated Polyglot 16 / FSRS v5 / 3x3 Matrix Mastery specifications across Room persistence, FSRS v5 scheduling, `VocabDrillScreen`, `SentencePracticeScreen`, `LessonDetailScreen`, `CurriculumScreen`, and a full 16-lesson `InitialCurriculumData` catalog.

## Functional Requirements
1. **Room Schema & 16-Lesson Curriculum (`AppDatabase`, `Entities.kt`, `InitialCurriculumData`):**
   - Evolve `LessonEntity` (`status`: `LOCKED`/`AVAILABLE`/`IN_PROGRESS`/`MASTERED`, `vocabMastery`, `practiceScore`, `unresolvedLapses`, `medianAssemblyMs`, `matrixStatsJson`), `SentenceEntity` (`matrixCoordinate`: `tense`, `form`, `personGroup`), and `SrsReviewEntity` (`state`: `NEW`/`LEARNING`/`REVIEW`/`RELEARNING`, `stability`, `difficulty`, `elapsedDays`, `scheduledDays`, `reps`, `lapses`, `dueAt`, `lastReviewedAt`).
   - Add `MatrixCellStat` (`attempts`, `correct`, `accuracy`, `avgLatencyMs`, `cellStatus`: `UNTESTED`/`WEAK`/`PROFICIENT`/`MASTERED`) and increment `AppDatabase` version with `fallbackToDestructiveMigration`.
   - Expand `InitialCurriculumData` to all 16 progressive Serbian-for-Russian-speakers lessons (`orderIndex` `1..16`), each with vocabulary items, 3x3 grammar guide, and `matrixCoordinate`-tagged sentences.
2. **FSRS v5 Scheduler & Implicit Rating Engine (`SpacedRepetitionAlgorithm`):**
   - Replace SM-2 calculations with FSRS v5 (`NEW`, `LEARNING`, `REVIEW`, `RELEARNING`, `stability`, `difficulty` `1.0..10.0`, retrievability `R = (1 + (19/81) * (t / S))^-0.5`).
   - Map Vocabulary (`2x2` grid) and Sentence (`Word Tiles`) interaction telemetry implicitly to ratings `1 (AGAIN)`, `2 (HARD)`, `3 (GOOD)`, `4 (EASY)`:
     - Vocab: `AGAIN` = wrong first attempt; `HARD` = `> 6.0s`; `GOOD` = `2.5s–6.0s`; `EASY` = `<= 2.5s`.
     - Sentence: `AGAIN` = wrong sequence or Grammar Guide drawer opened mid-attempt; `HARD` = `> 15.0s` or `> 2` tile returns; `GOOD` = `6.0s–15.0s` with `<= 2` tile returns; `EASY` = `<= 6.0s` with `0` tile returns.
   - **First-Attempt Scoring Guard:** Items rated `AGAIN` append to the active session tail for re-testing, while FSRS `stability`, `difficulty`, and `dueAt` update strictly from the first attempt in the session.
   - **Daily Blended Review Queue (`DAILY_REVIEW`):** Surface a 20-item review session (`<= 70%` / max 14 overdue items sorted by ascending retrievability `R`, `>= 30%` / min 6 active items from the current `IN_PROGRESS` lesson) with a 30 `REVIEW` items/day soft cap; review backlogs never lock new lessons.
   - Keep standalone `SpacedRepetitionScreen` functional on top of FSRS v5.
3. **Vocabulary Drill Enhancements (`VocabDrillScreen`):**
   - Highlight correct selection in Success Emerald (`#10B981`) with haptic pulse, affirmative audio, and `500ms` auto-advance.
   - Highlight incorrect selection in Error Coral (`#EF4444`) with lateral shake animation, highlight the correct card in Emerald, and append the missed item to the queue tail.
   - Measure first-attempt response latency and report implicit FSRS rating.
4. **Sentence Practice Enhancements (`SentencePracticeScreen`):**
   - Lock UI evaluation state on `Check Answer`, highlight misplaced tokens, reveal target structure, and provide a direct link/drawer to the associated grammar rule (opening the drawer mid-attempt forces `AGAIN`).
   - Track `matrixCoordinate`, assembly latency, and tile returns per attempt.
5. **Two-Tier Progression Gates, 3x3 Matrix Heatmap & Remediation (`CurriculumRepository`, `LessonDetailScreen`, `CurriculumScreen`):**
   - **Unlock Gate (`AVAILABLE` for Lesson `N+1`):** `practiceScore >= 0.70` (`>= 70%`).
   - **Mastery Gate (`MASTERED` for Lesson `N`):** `vocabMastery >= 0.85` (`stability >= 3` days), all `9/9` `matrixStats` cells `>= 3` attempts and `>= 80%` accuracy, `unresolvedLapses == 0`, and `medianAssemblyMs <= 12000`.
   - **Matrix Heatmap & Remediation:** Render the 3x3 heatmap on `LessonDetailScreen` using design tokens (`UNTESTED` `#64748B`, `WEAK` `#EF4444`, `PROFICIENT` `#6366F1`, `MASTERED` `#10B981`). When `>= 1` cell is `WEAK`, provide a `"Drill Weak Cells"` action launching a focused `6–8` item sentence session filtered by weak `matrixCoordinate` keys.

## Non-Functional Requirements
- Strictly offline-first Room persistence; `48dp x 48dp` minimum touch targets; Slate-neutral Material 3 design tokens (`#0F172A`, `#64748B`, `#F8FAFC`, `#10B981`, `#EF4444`, `#6366F1`).

## Acceptance Criteria
- Unit, integration, and Robolectric Compose UI tests verify FSRS v5 formulas, implicit rating mapping, first-attempt guard, 70/30 `DAILY_REVIEW` blending & 30/day cap, 16-lesson seeding, two-tier progression gates, 3x3 heatmap colors, `"Drill Weak Cells"` filtering, and `VocabDrillScreen` / `SentencePracticeScreen` feedback.
- `./gradlew testDebugUnitTest` and `python3 wiki/lint.py` pass with zero errors.

## Out of Scope
- Cloud sync, multi-user profiles, and Phase 3 speech-to-text oral grading.
