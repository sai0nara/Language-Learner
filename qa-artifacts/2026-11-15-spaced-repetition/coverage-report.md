# Coverage Report — Spaced Repetition (SM-2)

**Run:** `2026-11-15-spaced-repetition`
**Scope:** `SpacedRepetitionAlgorithm`, `SrsDao`, `CurriculumRepository` SRS methods, `SpacedRepetitionScreen`, and `CurriculumScreen` SRS launcher/badge.

---

## 1. Requirements Traceability Matrix (RTM)

| Element ID | Description | Technique(s) | Test Case IDs | Status |
|---|---|---|---|---|
| `P1` | `quality` parameter (`0..5` clamped, `< 3` fail / `>= 3` pass) | EP, BVA | `TC-001`..`TC-008` | Covered |
| `P2` | `easeFactor` parameter (`>= 1.3f` floor, default `2.5f`) | EP, BVA | `TC-002`, `TC-005`, `TC-008` | Covered |
| `P3` | `repetition` parameter (`0`, `1`, `>= 2` branches) | EP, BVA | `TC-004`..`TC-007`, `TC-009` | Covered |
| `P4` | `nextReviewTimestamp <= currentTimestamp` due-query boundary | EP, BVA, Error Guessing | `TC-010`..`TC-012`, `TC-036`, `TC-038` | Covered |
| `P5` | `dueCount` (`0` vs `> 0`) badge & launcher enablement | EP, BVA | `TC-010`, `TC-011`, `TC-013` | Covered |
| `R1` | SM-2 interval & repetition progression rule (`8 -> 4` columns) | Decision Table, Error Guessing | `TC-001`..`TC-007`, `TC-038` | Covered |
| `R2` | Prompt & `secondaryText` display rule (`4 -> 4` columns) | Decision Table, Error Guessing | `TC-014`..`TC-017`, `TC-034`, `TC-035` | Covered |
| `S1` | `Due_Unrevealed` flashcard state | State Transition | `TC-018`..`TC-022` | Covered |
| `S2` | `Due_Revealed` flashcard state | State Transition | `TC-018`, `TC-019`, `TC-021` | Covered |
| `S3` | `Queue_Empty` completion state | State Transition | `TC-018`, `TC-023` | Covered |
| `T1` | Legal: `S1 -> S2` (tap `show_answer_button`, play audio) | State Transition | `TC-018` | Covered |
| `T2` | Legal: `S1 -> S1` (tap speaker `IconButton`) | State Transition | `TC-020` | Covered |
| `T3` | Legal: `S2 -> S1` (grade with remaining due items) | State Transition, Error Guessing | `TC-019`, `TC-037` | Covered |
| `T4` | Legal: `S2 -> S3` (grade last due item) | State Transition, Error Guessing | `TC-018`, `TC-037` | Covered |
| `T5` | Legal: `S3 -> Exit` (tap `srs_done_back_button`) | State Transition | `TC-018` | Covered |
| `T6` | Legal: `S1/S2 -> Exit` (tap `back_from_srs`) | State Transition | `TC-021` | Covered |
| `T7` | Illegal: `S1 -> Grade` (submit grade while unrevealed) | State Transition | `TC-022` | Covered |
| `T8` | Illegal: `S3 -> Reveal/Grade` (interact with empty queue) | State Transition | `TC-023` | Covered |
| `C1` | `ReviewGrade` (4) × `ScriptMode` (2) × `ItemType` (2) | Pairwise | `TC-026`..`TC-033` | Covered |
| `U1` | End-to-end SRS review, enrollment, audio, & error handling | Use Case, Error Guessing | `TC-018`, `TC-020`, `TC-023`..`TC-025`, `TC-034`..`TC-038` | Covered |
| `G1` | Sentence auto-enrollment not implemented in `CurriculumRepository` | Gap | — | Gap (blocks auto sentence enrollment) |
| `G2` | `ReviewGrade.HARD` static `"2d"` label vs `1d` SM-2 reset | Gap | — | Gap (non-blocking discrepancy) |
| `G3` | ViewModel flow `System.currentTimeMillis()` staleness across day boundary | Gap | — | Gap (non-blocking) |

---

## 2. Coverage Summary

| Metric | Value | Notes |
|---|---|---|
| Modeled testable elements (`elements_total`) | `20` | `P1..P5` (5), `R1..R2` (2), `S1..S3` (3), `T1..T8` (8), `C1` (1), `U1` (1) |
| Elements covered (`elements_covered`) | `20` | All 20 testable elements have `>= 1` traced test case |
| Declared gaps (`elements_gap`) | `3` | `G1`–`G3` (excluded from denominator) |
| **Coverage (`coverage_pct`)** | **100.0%** | `20 / 20` modeled testable elements (`elements_gap=3`) |
| Decision Table columns (`dt_columns_full` -> `dt_columns_reduced`) | `12 -> 8` | `R1`: `8 -> 4`; `R2`: `4 -> 4` |
| State Transition coverage | `3` states, `6/6` legal, `2/2` illegal | `100%` 0-switch + illegal transition coverage |
| Pairwise (`C1`) | `8` configs, `20/20` pairs (`0` missing) | `50.0%` reduction from `16` exhaustive configs |
| Use Case (`U1`) | `5/5` paths | `1` basic + `2` alternative + `2` exception |

---

## 3. Gap List

| Gap ID | Unresolved Question / Discrepancy | Impact / What It Blocks |
|---|---|---|
| `G1` | PRD Overview states SRS schedules vocabulary and sentence flashcards, and `SrsReviewEntity.itemType` supports `"SENTENCE"`, but `CurriculumRepository` only auto-enrolls `vocabItems` in `ensureInitialDataLoaded` and `importCurriculum`. | Blocks automated tests for automatic sentence SRS enrollment; manual/direct `SENTENCE` `SrsReviewEntity` grading & display remain covered via `C1`. |
| `G2` | `ReviewGrade.HARD` displays static `"2d"` (`intervalDescription`), whereas `gradeToQuality(HARD) = 2 < 3` resets `intervalDays = 1` in `calculateNextReview`. | Non-blocking for test execution (`intervalDays = 1` is verified in `TC-004`); flagged for product/UI alignment. |
| `G3` | `LanguageLearnerViewModel.dueSrsItems` and `dueSrsCount` evaluate `System.currentTimeMillis()` when the flows are created, with no stated refresh trigger if the app remains open past `nextReviewTimestamp`. | Non-blocking for DAO boundary tests (`P4`); long-lived ViewModel auto-refresh behavior across midnight is unstated. |

---

## 4. Reduction Ledger

| Stage | Count | Breakdown |
|---|---|---|
| **Exhaustive-equivalent** | `167` | `P1..P3 × R1` (`120`) + `P4` (`3`) + `P5` (`3`) + `R2` (`4`) + `S1..S3, T1..T8` (`11`) + `C1` (`16`) + `U1` (`5`) + Error Guessing (`5`) |
| **Post-reduction** (within-technique) | `59` | `P1..P3` (`16`) + `R1` (`4`) + `P4` (`3`) + `P5` (`3`) + `R2` (`4`) + `S1..S3, T1..T8` (`11`) + `C1` (`8`) + `U1` (`5`) + Error Guessing (`5`) |
| **Post-dedup** (cross-technique) | `38` | `TC-001`..`TC-038` (`P1..P3/R1` merged `20->9`; `P4/P5` merged `6->4`; `S/T/U1` merged `16->8`; `R2` `4`; `C1` `8`; Error Guessing `5`) |

```qa-summary
schema_version=1
gate_status=approved
elements_total=20
elements_covered=20
elements_gap=3
coverage_pct=100.0
cases_total=38
cases_p0=14
cases_p1=13
cases_p2=11
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
boundaries_tested=8,3,5,3,3
dt_columns_full=12
dt_columns_reduced=8
states_total=3
transitions_legal=6
transitions_illegal=2
transitions_illegal_tested=2
usecase_paths=5
usecase_paths_tested=5
configs=8
pairs_target=20
pairs_covered=20
pairs_missing=0
exhaustive_equivalent=167
post_reduction=59
post_dedup=38
```
