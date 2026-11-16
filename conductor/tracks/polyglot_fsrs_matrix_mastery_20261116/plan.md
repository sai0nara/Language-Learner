# Implementation Plan: Polyglot FSRS v5, 3x3 Matrix Mastery Gates & Weak-Cell Remediation

## Phase 1: Room Schema, Matrix Models & 16-Lesson Seed Data
- [ ] Task: Write failing unit & Room tests for evolved `LessonEntity`, `SentenceEntity` (`matrixCoordinate`), `SrsReviewEntity` (FSRS v5 fields), `MatrixCellStat`, and 16-lesson `InitialCurriculumData`
    - [ ] Add DAO & entity serialization tests in `RoomPersistenceAndRepositoryTest.kt` for `matrixStatsJson`, `matrixCoordinate`, and FSRS fields
    - [ ] Add seed verification tests confirming `InitialCurriculumData` provides 16 ordered lessons with vocabulary, grammar guides, and `matrixCoordinate`-tagged sentences
- [ ] Task: Implement evolved Room entities, `MatrixCellStat`, DAOs, `AppDatabase` version bump, and 16-lesson `InitialCurriculumData`
    - [ ] Update `Entities.kt`, `Converters.kt`, `LessonDao.kt`, `SentenceDao.kt`, `SrsDao.kt`, and `AppDatabase.kt`
    - [ ] Expand `InitialCurriculumData.kt` to 16 lessons (`orderIndex` `1..16`) with `matrixCoordinate` tags
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Room Schema, Matrix Models & 16-Lesson Seed Data' (Protocol in workflow.md)

## Phase 2: FSRS v5 Scheduler, Implicit Rating Engine & 70/30 Daily Blended Queue
- [ ] Task: Write failing unit tests for FSRS v5 scheduling, retrievability `R`, implicit rating mapping (`1–4`), first-attempt guard, and 70/30 `DAILY_REVIEW` blending
    - [ ] Test FSRS v5 `NEW`/`LEARNING`/`REVIEW`/`RELEARNING` transitions, `stability`, `difficulty` (`1.0..10.0`), and retrievability sorting
    - [ ] Test Vocab (`<=2.5s`, `2.5–6.0s`, `>6.0s`, wrong) & Sentence (`<=6.0s` + `0` returns, `6.0–15.0s` + `<=2`, `>15.0s` or `>2`, wrong/drawer) implicit rating derivation
    - [ ] Test First-Attempt Scoring Guard and `DAILY_REVIEW` 20-item blend (`<=14` overdue + `>=6` `IN_PROGRESS`, `30`/day soft cap, non-blocking backlogs)
- [ ] Task: Implement FSRS v5 in `SpacedRepetitionAlgorithm.kt` and repository/ViewModel scheduling in `CurriculumRepository.kt` & `LanguageLearnerViewModel.kt`
    - [ ] Implement FSRS v5 state transitions, implicit rating mapper, and first-attempt session guard
    - [ ] Implement `DAILY_REVIEW` blended queue builder and `SpacedRepetitionScreen.kt` compatibility
- [ ] Task: Conductor - User Manual Verification 'Phase 2: FSRS v5 Scheduler, Implicit Rating Engine & 70/30 Daily Blended Queue' (Protocol in workflow.md)

## Phase 3: Two-Tier Progression Gates, Matrix Heatmap & Drill UI Enhancements
- [ ] Task: Write failing Robolectric Compose UI & repository tests for Two-Tier Gates, 3x3 Heatmap, Weak-Cell Remediation, `VocabDrillScreen`, and `SentencePracticeScreen`
    - [ ] Test `AVAILABLE` (`practiceScore >= 0.70`) and 4-part `MASTERED` gate (`vocabMastery >= 0.85`, 9/9 matrix cells `>= 3` attempts & `>= 80%`, `unresolvedLapses == 0`, `medianAssemblyMs <= 12000`)
    - [ ] Test `LessonDetailScreen` 3x3 matrix heatmap tokens (`#64748B`, `#EF4444`, `#6366F1`, `#10B981`) and `"Drill Weak Cells"` 6–8 item filtered launch
    - [ ] Test `VocabDrillScreen` (`500ms` auto-advance, coral shake + emerald reveal, latency capture) and `SentencePracticeScreen` (`Check Answer` UI lock, misplaced-token highlight, grammar drawer link & `AGAIN` penalty)
- [ ] Task: Implement Two-Tier Progression Gates, Heatmap UI, Weak-Cell Remediation, and Drill Screen enhancements
    - [ ] Update `CurriculumRepository.kt` and `LanguageLearnerViewModel.kt` for two-tier gates, `matrixStats` aggregation, and weak-cell session filtering
    - [ ] Update `CurriculumScreen.kt`, `LessonDetailScreen.kt`, `VocabDrillScreen.kt`, `SentencePracticeScreen.kt`, and `Theme.kt`/`Color.kt` tokens + `48dp` targets
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Two-Tier Progression Gates, Matrix Heatmap & Drill UI Enhancements' (Protocol in workflow.md)

## Phase 4: Docs, Wiki Sync & Full Verification
- [ ] Task: Sync `Docs/psd/*`, `Docs/tests/*`, `wiki/` pages, and `qa-artifacts/` test suites with the implemented FSRS v5 & 3x3 Matrix Mastery behavior
    - [ ] Run `./gradlew testDebugUnitTest` and `python3 wiki/lint.py` and verify 0 failures and exit code 0
- [ ] Task: Conductor - User Manual Verification 'Phase 4: Docs, Wiki Sync & Full Verification' (Protocol in workflow.md)
