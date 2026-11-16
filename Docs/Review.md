# Code Review Register

## RV-1 — Track `polyglot_fsrs_matrix_mastery_20261116` (2026-11-16)

- **Scope:** Polyglot FSRS v5, 3x3 Matrix Mastery Gates & Weak-Cell Remediation — commits `208a0a8..db1cd62` (25 files, +1458/−176). No task SHAs were recorded in the track plan; range derived from git history.
- **Plan:** `conductor/tracks/polyglot_fsrs_matrix_mastery_20261116/plan.md` · **Spec:** `conductor/tracks/polyglot_fsrs_matrix_mastery_20261116/spec.md`
- **Verdict:** Resolved (`2026-11-16`). Findings `RV-1.1`–`RV-1.12` fixed and verified with `./gradlew testDebugUnitTest` (`208` tests, `0` failures) and `python3 wiki/lint.py` (exit `0`).

### Verification

| Check | Result |
|---|---|
| Plan compliance | Partial — data model, FSRS helpers, heatmap, weak-cell filter exist; DAILY_REVIEW UI, sentence lock/misplaced highlight/grammar drawer, vocab shake, CurriculumScreen/Theme/Color tokens, and Phase 4 doc/wiki sync missing |
| Style compliance | Fail — duplicated parsing, hardcoded tokens, stale docs |
| New tests | Yes — `app/src/test/java/com/example/qa/PolyglotFsrsAndMatrixMasteryTest.kt` (4) and 5 new cases in `app/src/test/java/com/example/srs/SpacedRepetitionAlgorithmTest.kt` |
| Test coverage | Partial — no tests for retry/double-tap/concurrency/seed coverage scenarios behind the findings below |
| Test results | `./gradlew testDebugUnitTest`: 206 tests, 0 failures. Wiki lint exit 0 with ~50 staleness `REVIEW` warnings |

### Findings

| ID | Severity | Status | Finding |
|---|---|---|---|
| RV-1.1 | Critical | Resolved | Seed data makes MASTERED unreachable and matrix tags are wrong |
| RV-1.2 | High | Resolved | Unlock gate has no minimum sample size |
| RV-1.3 | High | Resolved | Standalone SRS "Hard" recorded as a lapse; standalone scheduling still SM-2 |
| RV-1.4 | High | Resolved | DAILY_REVIEW blended queue is never surfaced |
| RV-1.5 | High | Resolved | Check Answer not locked on wrong answer; spec'd sentence/vocab feedback missing |
| RV-1.6 | High | Resolved | Phase 4 doc/wiki sync marked done but not performed |
| RV-1.7 | Medium | Resolved | Retries inflate matrix accuracy and practice score |
| RV-1.8 | Medium | Resolved | Lost-update race on `matrixStatsJson` |
| RV-1.9 | Medium | Resolved | Daily queue can exceed the 14-overdue cap; daily cap counts distinct items |
| RV-1.10 | Medium | Resolved | "FSRS v5" is a heuristic approximation, not FSRS-5 |
| RV-1.11 | Low | Resolved | Duplicated Moshi parsing and inline color tokens |
| RV-1.12 | Low | Resolved | `isCompleted` can diverge from `status`; status stored as free string |
| RV-1.13 | Low | Open | Commit traceability: all phases in one commit, no SHAs in plan |

#### RV-1.1 — Critical — Seed data makes MASTERED unreachable; matrix tags wrong
- **File:** `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` (`getInitialSentences`, `getInitialVocab`, `getInitialGrammarGuides`)
- **Context:** The mastery gate requires all 9 cells with ≥ 3 attempts, but no lesson has sentences covering 9 coordinates (lesson 1: 4 sentences, lessons 2–3: 2, lessons 4–16: 1). Lesson 1–3 sentences use the default `PRESENT_AFFIRMATIVE` tag even when negative ("Я не говорю…"), interrogative ("…в офисе?"), past ("вчера…") or future ("Завтра…"). Lessons 4–16 are placeholders: the same present-affirmative sentence ("Ми данас учимо српски језик.") tagged by `idx % 3` (e.g. as past-negative), one vocab item, and a grammar guide with an empty `matrixJson`. Weak-cell drills therefore serve mismatched content.
- **Suggestion:** Author ≥ 1 correctly tagged sentence per cell (9+ per lesson), tag lessons 1–3 explicitly, and add a seed test asserting 9/9 coordinate coverage per lesson.

#### RV-1.2 — High — Unlock gate has no minimum sample
- **File:** `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` (`evaluateTwoTierGatesInternal`)
- **Context:** `practiceScore = correct / recent.size` has no minimum count, so a single correct answer yields 1.0 ≥ 0.70 and unlocks lesson N+1. The spec does not define a minimum sample (gap G1 — needs a product decision).
- **Suggestion:**
```diff
- val unlockNext = practiceScore >= 0.70f || lesson.masteryScore >= 4.5f
+ val unlockNext = (recent.size >= MIN_PRACTICE_SAMPLE && practiceScore >= 0.70f) || lesson.masteryScore >= 4.5f
```

#### RV-1.3 — High — "Hard" in standalone SRS counts as a lapse; scheduling still SM-2
- **Files:** `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` (`calculateNextReview`, `gradeToQuality`), `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` (`recordSrsResult`)
- **Context:** `gradeToQuality(HARD) = 2`, and `calculateNextReview` maps quality < 3 to `AGAIN`, moving the card to `RELEARNING` and incrementing `lapses` — which raises `unresolvedLapses` and blocks mastery. `nextReviewTimestamp` is still derived from SM-2, contrary to "Replace SM-2 calculations with FSRS v5".
- **Suggestion:**
```diff
 suspend fun recordSrsResult(item: SrsReviewEntity, grade: ReviewGrade) = withContext(Dispatchers.IO) {
-    val quality = SpacedRepetitionAlgorithm.gradeToQuality(grade)
-    val updated = SpacedRepetitionAlgorithm.calculateNextReview(item, quality)
+    val updated = SpacedRepetitionAlgorithm.calculateFsrsNextReview(item, grade)
     db.srsDao().insertOrUpdate(updated)
 }
```

#### RV-1.4 — High — DAILY_REVIEW is never surfaced
- **File:** `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` (`refreshDailyBlendedQueue`, `dailyBlendedQueue`)
- **Context:** Nothing calls `refreshDailyBlendedQueue()` and no screen collects `dailyBlendedQueue`; the spec's 20-item blended session is unreachable.
- **Suggestion:** Invoke it on entry to `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` and drive that screen's queue from the blended list.

#### RV-1.5 — High — Check Answer not locked on wrong; feedback features missing
- **Files:** `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` (Check Answer button), `app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt`
- **Context:** Only a correct result short-circuits the button. After a wrong answer each extra tap records another wrong attempt (matrix stats and 100-answer window) and re-appends the sentence to the queue. Misplaced-token highlighting, the post-check grammar drawer link, and the vocab lateral shake animation are not implemented although the plan marks them done.
- **Suggestion:**
```diff
- if (validationResult == true) return@Button
+ if (validationResult != null) return@Button
```
  Then implement misplaced-token highlighting, the grammar drawer link, and the shake animation with UI tests.

#### RV-1.6 — High — Phase 4 doc/wiki sync not performed
- **Context:** Only `Docs/prd/*` changed. Wiki lint flags `wiki/modules/data-layer.md`, `wiki/modules/srs-and-audio.md`, `wiki/modules/ui-layer.md`, `wiki/service/overview.md`, `wiki/tests/unit-and-ui-tests.md`, all `Docs/psd/*` and all `Docs/tests/*` as stale against the changed sources (violates "Agent owns the vault"). `CurriculumScreen.kt`, `Theme.kt` and `Color.kt` were listed in the plan but untouched.
- **Suggestion:** Update the flagged pages, implement the planned screen/token changes, and reopen the Phase 4 task until done.

#### RV-1.7 — Medium — Retries inflate matrix accuracy and practice score
- **File:** `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` (`recordImplicitSentenceAttempt`, `recordImplicitVocabAttempt`)
- **Context:** `isFirstAttemptInSession` guards only the FSRS update; `recordMatrixCellAttempt` and `recordAnswer` also count re-tests of missed items, so a miss followed by an easy retry still raises cell accuracy. Gate evaluation also runs redundantly (up to 3× per sentence attempt).
- **Suggestion:**
```diff
+ if (isFirstAttemptInSession) {
      repository.recordMatrixCellAttempt(...)
      repository.recordAnswer(sentence.lessonId, isCorrect)
+ }
- repository.evaluateTwoTierGates(sentence.lessonId)
```

#### RV-1.8 — Medium — Lost-update race on `matrixStatsJson`
- **File:** `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` (`recordMatrixCellAttempt`)
- **Context:** Each attempt launches an independent coroutine that reads, modifies and writes `matrixStatsJson` on `Dispatchers.IO` with no transaction or mutex; rapid attempts can drop updates.
- **Suggestion:** Wrap the read-modify-write in `db.withTransaction { }` or guard with a repository-level `Mutex`.

#### RV-1.9 — Medium — Daily queue can exceed the 14-overdue cap
- **File:** `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` (`buildDailyBlendedQueue`)
- **Context:** When the active lesson has fewer than 6 items, overdue items backfill to 20, exceeding the spec's max 14. `countReviewedSince` in `app/src/main/java/com/example/data/local/SrsDao.kt` counts distinct items rather than reviews and includes drill-touched items.
- **Suggestion:**
```diff
- val maxOverdueSlots = (targetSize - minActiveSlots).coerceAtLeast(0)
+ val maxOverdueSlots = minOf((targetSize * 7) / 10, targetSize - minActiveSlots).coerceAtLeast(0)
```

#### RV-1.10 — Medium — "FSRS v5" is a heuristic approximation
- **File:** `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` (`calculateFsrsNextReview`)
- **Context:** Stability/difficulty updates are hand-tuned multipliers without FSRS-5 weights (`w`) or retrievability-dependent stability growth; only the R formula matches.
- **Suggestion:** Implement the FSRS-5 equations, or document the deviation explicitly in `Docs/psd/spaced-repetition.md`.

#### RV-1.11 — Low — Duplicated parsing and inline tokens
- **File:** `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` (`MatrixHeatmapSection`)
- **Context:** Builds its own Moshi adapter instead of reusing the repository's matrix-stats parser; PROFICIENT and UNTESTED colors are inline literals rather than tokens in `app/src/main/java/com/example/ui/theme/Color.kt`.

#### RV-1.12 — Low — `isCompleted` / `status` divergence
- **File:** `app/src/main/java/com/example/data/model/Entities.kt`, `app/src/main/java/com/example/data/repository/CurriculumRepository.kt`
- **Context:** The legacy `recordAnswer` path sets `isCompleted` (score ≥ 4.5) independently, so a lesson can be completed without `MASTERED` status. `status` is persisted as a free string rather than the `LessonStatus` enum.

#### RV-1.13 — Low — Commit traceability
- **Context:** Phases 1–4 landed in a single `feat(data)` commit (`aaec59f`) and the track plan records no per-task SHAs, contrary to `conductor/workflow.md`.
