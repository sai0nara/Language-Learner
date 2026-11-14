# Test Suite — Curriculum Progression & Mastery Unlocking

> **Model:** `qa-artifacts/2026-11-14-curriculum-progression/test-model.md` (`gate_status=approved`)
> **Scope:** Curriculum Progression & Mastery Unlocking (`Docs/prd/curriculum-progression.md`, `Docs/psd/curriculum-progression.md`)
> **Note:** Gap-blocked expectations are marked `[UNSTATED / G<n>]` without inventing rules.

---

## 1. Test Cases

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | First-launch empty database seeds initial curriculum | Decision Table, Use Case | Room `lessons` table is empty (`existing.isEmpty() == true`) | App launches; `LanguageLearnerViewModel` initializes and calls `ensureInitialDataLoaded()` | UI: `CurriculumScreen` displays Lesson 1 `Unlocked` and subsequent lessons `Locked`.<br>Backend: `InitialCurriculumData` inserted into `lessons`, vocabulary, grammar guides, sentences, and SRS tables. | `P0` | `R1`, `U1` |
| `TC-002` | Subsequent launch with existing database skips seeding | Decision Table, Use Case | Room `lessons` table already contains seeded lessons and user progress | App relaunches; `ensureInitialDataLoaded()` runs | UI: `CurriculumScreen` displays existing lessons and preserved mastery scores.<br>Backend: No new rows inserted; no duplicate lessons, vocab, grammar, sentences, or SRS items. | `P0` | `R1`, `U1` |
| `TC-003` | Reaching 90/100 correct answers (`4.50f`) masters lesson and unlocks next lesson | EP, BVA, Decision Table, State Transition, Use Case | Lesson 1 is `Unlocked` (`S2`) with `89` correct out of `99` answers in `lesson_answers`; Lesson 2 is `Locked` (`S1`) | Open Lesson 1 -> `LessonDetailScreen` -> `SentencePracticeScreen` -> submit `isCorrect = true` (90th correct, 100th answer) | UI: Lesson 1 shows `Mastered` (`4.50` / `5.0`); Lesson 2 transitions to `Unlocked` and is tappable.<br>Backend: `LessonAnswerEntity` inserted; `masteryScore = 4.5f`; Lesson 1 marked completed; `unlockLessonByOrder(orderIndex + 1)` unlocks Lesson 2. | `P1` | `P1`, `P3`, `P4`, `R2`, `R3`, `S2`, `S3`, `T1`, `T2`, `U2` |
| `TC-004` | Below mastery threshold: 89/100 correct (`4.45f`) keeps lesson unlocked | EP, BVA, Decision Table, State Transition | Lesson 1 is `Unlocked` (`S2`) with `88` correct out of `99` answers; Lesson 2 is `Locked` (`S1`) | Submit `isCorrect = true` (89th correct, 100th answer) or `isCorrect = false` after 89 correct (`89/100`) | UI: Lesson 1 remains `Unlocked` (not `Mastered`) showing `4.45`; Lesson 2 remains `Locked`.<br>Backend: `LessonAnswerEntity` inserted; `masteryScore = 4.45f`; Lesson 1 not completed; Lesson 2 remains locked. | `P1` | `P1`, `P3`, `P4`, `R2`, `S2`, `T3` |
| `TC-005` | Above mastery threshold: 91/100 correct (`4.55f`) masters lesson and unlocks next | EP, BVA, Decision Table, State Transition | Lesson 1 is `Unlocked` (`S2`) with `90` correct out of `99` answers | Submit `isCorrect = true` (91st correct, 100th answer) | UI: Lesson 1 shows `Mastered` (`4.55`); Lesson 2 is `Unlocked`.<br>Backend: `masteryScore = 4.55f`; Lesson 1 completed; Lesson 2 unlocked. | `P1` | `P1`, `P3`, `P4`, `R2`, `T2` |
| `TC-006` | Lower boundary: 0 answers / 0 correct renders `0.0f` without error | EP, BVA, Error Guessing | Lesson 1 is `Unlocked` with `0` rows in `lesson_answers` | Open `LessonDetailScreen` for Lesson 1 (`correctCount = 0`, `answerCount = 0`) | UI: Lesson progress displays `0.0` mastery without crash or NaN.<br>Backend: `0` rows in `lesson_answers`; `masteryScore = 0.0f`. | `P2` | `P1`, `P2`, `Error Guessing` |
| `TC-007` | Lower boundary +1: 1st correct answer computes `0.05f` | EP, BVA | Lesson 1 is `Unlocked` with `0` rows in `lesson_answers` | Submit `isCorrect = true` (`correctCount = 1`, `answerCount = 1`) | UI: Lesson 1 remains `Unlocked` showing `0.05`.<br>Backend: `1` row in `lesson_answers`; `masteryScore = 0.05f`. | `P1` | `P1`, `P2`, `P4` |
| `TC-008` | Upper boundary -1: 99/99 correct computes `4.95f` without pruning | EP, BVA, Decision Table | Lesson 1 has `98` correct out of `98` answers in `lesson_answers` | Submit `isCorrect = true` (`correctCount = 99`, `answerCount = 99`) | UI: Lesson 1 `Mastered` showing `4.95`.<br>Backend: `99` rows retained (`pruneOldAnswers` deletes `0` rows); `masteryScore = 4.95f`. | `P1` | `P1`, `P2`, `R2` |
| `TC-009` | Upper boundary: 100/100 correct computes `5.00f` and stays mastered | EP, BVA, State Transition | Lesson 1 is `Mastered` (`S3`) with `99` correct out of `99` answers | Submit `isCorrect = true` (`correctCount = 100`, `answerCount = 100`) | UI: Lesson 1 remains `Mastered` showing `5.00`.<br>Backend: `100` rows retained (`0` pruned); `masteryScore = 5.0f`; state remains `S3`. | `P1` | `P1`, `P2`, `S3`, `T4` |
| `TC-010` | Rolling window prune at 101st answer with `masteryScore < 4.5f` | EP, BVA, Decision Table, Use Case | Lesson 1 has `100` rows (`50` correct, `50` incorrect; oldest row is `isCorrect = true`) | Submit `isCorrect = false` (101st answer) in `VocabDrillScreen` | UI: Lesson 1 remains `Unlocked` with updated lower score (`2.45`).<br>Backend: `pruneOldAnswers` deletes the 1 oldest row leaving `100` rows; `correctCount = 49`; `masteryScore = 2.45f`. | `P1` | `P2`, `P4`, `R2`, `U2` |
| `TC-011` | Rolling window prune at 101st answer pushes out old wrong answer to reach `4.50f` | EP, BVA, Decision Table, State Transition, Use Case | Lesson 1 is `Unlocked` with `100` rows (`89` correct, `11` incorrect; oldest row is `isCorrect = false`); Lesson 2 is `Locked` | Submit `isCorrect = true` (101st answer) | UI: Lesson 1 transitions to `Mastered` (`4.50`); Lesson 2 transitions to `Unlocked`.<br>Backend: `pruneOldAnswers` deletes oldest `false` row leaving `100` rows (`90` correct); `masteryScore = 4.5f`; Lesson 1 completed; Lesson 2 unlocked. | `P1` | `P2`, `P4`, `R2`, `T1`, `T2`, `U2` |
| `TC-012` | Invalid partition: negative `correctCount` (`-1`) / `masteryScore < 0.0f` guarded | EP, BVA | Lesson 1 exists in `lessons` | Attempt to compute/persist `correctCount = -1` (or `masteryScore < 0.0f`) | UI: No negative mastery score displayed.<br>Backend: Negative count/score rejected or clamped to `>= 0.0f`; no corrupted `LessonEntity` state persisted. | `P0` | `P1`, `P3` |
| `TC-013` | Invalid partition: `correctCount = 101` / `masteryScore > 5.0f` guarded | EP, BVA | Lesson 1 exists in `lessons` | Attempt to compute/persist `correctCount = 101` (or `masteryScore > 5.0f`) | UI: Mastery score never exceeds `5.0`.<br>Backend: Count > 100 / score > 5.0f rejected or prevented by 100-row rolling cap. | `P0` | `P1`, `P3` |
| `TC-014` | Invalid partition: negative `answerCount` (`-1`) guarded | EP, BVA | Lesson 1 exists in `lessons` | Attempt query/pruning with `answerCount = -1` | UI: No crash.<br>Backend: Negative count rejected/guarded; `lesson_answers` integrity preserved. | `P0` | `P2` |
| `TC-015` | Tapping a `Locked` lesson card shows toast and blocks navigation | Decision Table, State Transition, Use Case | Lesson 2 is `Locked` (`S1`) on `CurriculumScreen` | Tap Lesson 2 card | UI: Toast displayed explaining 4.5 grade on previous lesson is required (`[UNSTATED / G6]` exact copy); remains on `CurriculumScreen` (no navigation to `LessonDetailScreen`).<br>Backend: Lesson 2 remains `Locked` (`S1`). | `P2` | `R3`, `S1`, `T5`, `U2` |
| `TC-016` | Illegal transition: direct transition from `Locked` (`S1`) to `Mastered` (`S3`) blocked | State Transition | Lesson 2 is `Locked` (`S1`) | Attempt to record answers or mark Lesson 2 `Mastered` (`S3`) while `isUnlocked == false` | UI: Lesson 2 cannot be opened for drills.<br>Backend: `S1 -> S3` rejected/blocked; Lesson 2 remains `Locked` (`S1`). | `P0` | `S1`, `T6` |
| `TC-017` | Launch 3x3 Grammar Matrix from `LessonDetailScreen` | Use Case | Lesson 1 is `Unlocked` (`S2`) | Open Lesson 1 -> `LessonDetailScreen` -> tap 3x3 Grammar Matrix entry point | UI: Navigates to `GrammarMatrixScreen` for Lesson 1.<br>Backend: `activeLesson` remains Lesson 1; no unintended `lesson_answers` mutation. | `P2` | `U2` |
| `TC-018` | Toggle Cyrillic/Latin script and Dark/Light theme in `CurriculumScreen` | Use Case | `CurriculumScreen` is displayed | Tap script toggle (`isLatinScript`) and theme toggle (`isDarkTheme`) in top bar | UI: Screen re-renders in toggled script (Cyrillic <-> Latin) and Material 3 theme (Light <-> Dark).<br>Backend: `isLatinScript` and `isDarkTheme` `StateFlow` values update (`[UNSTATED / G5]` cold-start persistence). | `P2` | `U3` |
| `TC-019` | Pairwise UI config matrix across script, theme, and lesson states (6 configs) | Pairwise, State Transition, Error Guessing | Lessons in `Locked` (`S1`), `Unlocked` (`S2`), and `Mastered` (`S3`) states exist | Render `CurriculumScreen` for 6 pairwise configs:<br>1) `(Cyrillic, Light, Locked)`<br>2) `(Latin, Dark, Locked)`<br>3) `(Cyrillic, Dark, Unlocked)`<br>4) `(Latin, Light, Unlocked)`<br>5) `(Cyrillic, Light, Mastered)`<br>6) `(Latin, Dark, Mastered)` | UI: All 6 configs render legible Material 3 cards, multi-byte Serbian Cyrillic / Latin text without encoding corruption, and clear `Locked` / `Unlocked` / `Mastered` indicators.<br>Backend: `StateFlow` state matches rendered config. | `P2` | `C1`, `S1`, `S2`, `S3` |
| `TC-020` | Error Guessing: rapid double-submit of `recordLessonAnswer` at 100-row / 90-correct boundary | Error Guessing, BVA, Decision Table | Lesson 1 has `99` rows (`89` correct) | Rapidly invoke `recordLessonAnswer(true)` twice concurrently | UI: Displays accurate final mastery score without duplicate/stale state.<br>Backend: Exactly `100` rows retained (or `100` after prune if 2nd triggers 101->100), no race-condition corruption, `masteryScore` matches persisted `lesson_answers`. | `P0` | `Error Guessing`, `P2`, `R2` |
| `TC-021` | Error Guessing: `pruneOldAnswers` when >100 rows share identical timestamps | Error Guessing, BVA | Lesson 1 has `100` rows with identical timestamp `T`, plus 1 new row with timestamp `T` | Submit 101st answer with identical timestamp `T` | UI: Score updates without crash.<br>Backend: Exactly `100` rows retained in `lesson_answers`; deterministic tie-breaking behavior `[UNSTATED / G1]`. | `P1` | `Error Guessing`, `P2` |

---

## 2. Risk Register

| Test Case ID | Impact (1–5) | Impact Criterion | Probability Signals Counted (Threshold Met) | Unassessable Signals | Probability | Priority |
|---|---|---|---|---|---|---|
| `TC-001` | 5 | Data loss/corruption (initial seed) | None (`0`) | `novelty` (not marked new/changed) | Low | `P0` |
| `TC-002` | 5 | Data corruption (duplicate seed rows) | None (`0`) | `novelty` | Low | `P0` |
| `TC-003` | 4 | Data mutation (`LessonEntity` + unlock) | None (`0`; `90` is 1 boundary) | `novelty` | Low | `P1` |
| `TC-004` | 4 | Data mutation (`LessonEntity` stats) | None (`0`; `89` adjacent to 1 boundary) | `novelty` | Low | `P1` |
| `TC-005` | 4 | Data mutation (`LessonEntity` + unlock) | None (`0`; `91` adjacent to 1 boundary) | `novelty` | Low | `P1` |
| `TC-006` | 3 | Core workflow (display progress) | `boundary density` (`P1=0` & `P2=0`, 2 boundaries -> `1` signal) | `novelty` | Low | `P2` |
| `TC-007` | 4 | Data mutation (`LessonAnswerEntity`) | `boundary density` (`P1=1` & `P2=1` adjacent to `0` -> `1` signal) | `novelty` | Low | `P1` |
| `TC-008` | 4 | Data mutation (`LessonAnswerEntity`) | `boundary density` (`P1=99` & `P2=99` adjacent to `100` -> `1` signal) | `novelty` | Low | `P1` |
| `TC-009` | 4 | Data mutation (`LessonAnswerEntity`) | `boundary density` (`P1=100` & `P2=100` -> `1` signal) | `novelty` | Low | `P1` |
| `TC-010` | 4 | Data mutation (prune + stats) | None (`0`; `101` adjacent to 1 boundary `100`) | `novelty` | Low | `P1` |
| `TC-011` | 4 | Data mutation (prune + unlock) | `boundary density` (`P2=101` adj. `100` & `P1=90` on `90` -> `1` signal) | `novelty` | Low | `P1` |
| `TC-012` | 5 | Data corruption (invalid negative score) | None (`0`; `-1` adjacent to 1 boundary `0`) | `novelty` | Low | `P0` |
| `TC-013` | 5 | Data corruption (invalid >5.0 score) | None (`0`; `101` adjacent to 1 boundary `100`) | `novelty` | Low | `P0` |
| `TC-014` | 5 | Data corruption (invalid negative count) | None (`0`; `-1` adjacent to 1 boundary `0`) | `novelty` | Low | `P0` |
| `TC-015` | 3 | Core workflow (locked lesson guard) | `state-machine depth` (`T5` illegal transition -> `1` signal) | `novelty` | Low | `P2` |
| `TC-016` | 5 | Progression bypass / data corruption | `state-machine depth` (`T6` illegal transition -> `1` signal) | `novelty` | Low | `P0` |
| `TC-017` | 3 | Core workflow (grammar navigation) | None (`0`) | `novelty` | Low | `P2` |
| `TC-018` | 2 | Secondary flow (UI preferences) | None (`0`) | `novelty` | Low | `P2` |
| `TC-019` | 1 | Display-only rendering | `known-fragile pattern` (Cyrillic multi-byte Unicode -> `1` signal) | `novelty` | Low | `P2` |
| `TC-020` | 5 | Data corruption (concurrent prune/score) | `boundary density` (`100` & `90`), `known-fragile pattern` (concurrency) -> `2` signals | `novelty` | Medium | `P0` |
| `TC-021` | 4 | Data mutation (prune tie-breaking) | `known-fragile pattern` (clock/timestamp boundary -> `1` signal) | `novelty` | Low | `P1` |

---

## 3. Gherkin Scenarios

```gherkin
Feature: Curriculum Progression & Mastery Unlocking

  @TC-001
  Scenario: First-launch empty database seeds initial curriculum
    Given the Room "lessons" table is empty
    When the app launches and LanguageLearnerViewModel initializes
    Then InitialCurriculumData is inserted into Room
    And CurriculumScreen displays Lesson 1 as Unlocked and subsequent lessons as Locked

  @TC-002
  Scenario: Subsequent launch with existing database skips seeding
    Given the Room "lessons" table already contains seeded lessons and progress
    When the app relaunches and ensureInitialDataLoaded runs
    Then no duplicate lessons, vocabulary, grammar guides, sentences, or SRS items are inserted
    And existing mastery scores are preserved

  @TC-003
  Scenario: Reaching 90 of 100 correct answers masters lesson and unlocks next lesson
    Given Lesson 1 is Unlocked with 89 correct out of 99 recorded answers
    And Lesson 2 is Locked
    When the learner submits a correct answer in SentencePracticeScreen
    Then Lesson 1 masteryScore becomes 4.50 and its state transitions to Mastered
    And Lesson 2 transitions to Unlocked

  @TC-004
  Scenario: Scoring 89 of 100 correct answers keeps lesson unlocked
    Given Lesson 1 is Unlocked with 89 correct out of 100 recorded answers
    And Lesson 2 is Locked
    When the mastery score is recalculated
    Then Lesson 1 masteryScore is 4.45 and remains Unlocked
    And Lesson 2 remains Locked

  @TC-005
  Scenario: Scoring 91 of 100 correct answers masters lesson and unlocks next
    Given Lesson 1 is Unlocked with 90 correct out of 99 recorded answers
    When the learner submits a correct answer
    Then Lesson 1 masteryScore becomes 4.55 and transitions to Mastered
    And Lesson 2 is Unlocked

  @TC-006
  Scenario: Lesson with zero recorded answers renders 0.0 mastery safely
    Given Lesson 1 is Unlocked with 0 rows in lesson_answers
    When the learner opens LessonDetailScreen for Lesson 1
    Then the mastery score is displayed as 0.0 without errors

  @TC-007
  Scenario: First correct answer computes 0.05 mastery score
    Given Lesson 1 is Unlocked with 0 rows in lesson_answers
    When the learner submits 1 correct answer
    Then lesson_answers contains 1 row and masteryScore is 0.05

  @TC-008
  Scenario: 99 of 99 correct answers computes 4.95 without pruning
    Given Lesson 1 has 98 correct out of 98 recorded answers
    When the learner submits 1 correct answer
    Then lesson_answers retains 99 rows without pruning and masteryScore is 4.95

  @TC-009
  Scenario: 100 of 100 correct answers computes 5.00 and remains Mastered
    Given Lesson 1 is Mastered with 99 correct out of 99 recorded answers
    When the learner submits 1 correct answer
    Then lesson_answers retains 100 rows without pruning and masteryScore is 5.00
    And Lesson 1 remains Mastered

  @TC-010
  Scenario: Rolling window prunes oldest row on 101st answer below mastery
    Given Lesson 1 has 100 answers (50 correct, oldest row is correct)
    When the learner submits an incorrect 101st answer in VocabDrillScreen
    Then pruneOldAnswers deletes the oldest row leaving 100 rows
    And masteryScore becomes 2.45

  @TC-011
  Scenario: Rolling window prune pushes out old wrong answer to reach 4.50 and unlock next lesson
    Given Lesson 1 is Unlocked with 100 answers (89 correct, oldest row is incorrect)
    And Lesson 2 is Locked
    When the learner submits a correct 101st answer
    Then pruneOldAnswers deletes the oldest incorrect row leaving 100 rows with 90 correct
    And Lesson 1 masteryScore becomes 4.50, transitions to Mastered, and unlocks Lesson 2

  @TC-012
  Scenario: Negative correctCount or masteryScore below 0.0 is guarded
    Given Lesson 1 exists in Room
    When a negative correctCount (-1) or masteryScore (< 0.0) is evaluated
    Then no negative masteryScore is persisted or displayed

  @TC-013
  Scenario: correctCount above 100 or masteryScore above 5.0 is guarded
    Given Lesson 1 exists in Room
    When a correctCount of 101 or masteryScore above 5.0 is evaluated
    Then no masteryScore above 5.0 is persisted or displayed

  @TC-014
  Scenario: Negative answerCount is guarded
    Given Lesson 1 exists in Room
    When an answerCount of -1 is evaluated
    Then the invalid count is rejected and lesson_answers integrity is preserved

  @TC-015
  Scenario: Tapping a Locked lesson card shows toast and blocks navigation
    Given Lesson 2 is Locked on CurriculumScreen
    When the learner taps the Lesson 2 card
    Then a toast explaining the 4.5 grade requirement is shown [UNSTATED / G6]
    And the app remains on CurriculumScreen without navigating to LessonDetailScreen

  @TC-016
  Scenario: Locked lesson cannot transition directly to Mastered
    Given Lesson 2 is Locked
    When an attempt is made to record answers or mark Lesson 2 Mastered directly
    Then the transition is blocked and Lesson 2 remains Locked

  @TC-017
  Scenario: Launching 3x3 Grammar Matrix from LessonDetailScreen
    Given Lesson 1 is Unlocked and LessonDetailScreen is open
    When the learner selects the 3x3 Grammar Matrix option
    Then the app navigates to GrammarMatrixScreen for Lesson 1

  @TC-018
  Scenario: Toggling Cyrillic/Latin script and Dark/Light theme
    Given CurriculumScreen is displayed
    When the learner toggles isLatinScript and isDarkTheme in the top bar
    Then the UI re-renders in the selected script and Material 3 theme [UNSTATED / G5]

  @TC-019
  Scenario: Pairwise UI rendering across script, theme, and lesson states
    Given lessons exist in Locked, Unlocked, and Mastered states
    When CurriculumScreen is rendered across the 6 pairwise configurations of (isLatinScript, isDarkTheme, lessonState)
    Then every configuration renders legible Material 3 cards, valid Cyrillic/Latin text, and clear state indicators

  @TC-020
  Scenario: Rapid double-submit at 100-row prune and 90-correct boundary
    Given Lesson 1 has 99 rows with 89 correct answers
    When recordLessonAnswer(true) is invoked twice concurrently
    Then lesson_answers retains at most 100 rows without corruption
    And masteryScore accurately reflects the persisted rows

  @TC-021
  Scenario: Pruning when more than 100 rows share identical timestamps
    Given Lesson 1 has 100 rows with identical timestamp T
    When a 101st answer is inserted with timestamp T
    Then pruneOldAnswers retains exactly 100 rows [UNSTATED / G1]
```

---

```qa-summary
schema_version=1
gate_status=approved
cases_total=21
cases_p0=7
cases_p1=9
cases_p2=5
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
boundaries_tested=0,90,100
dt_columns_full=8
dt_columns_reduced=8
states_total=3
transitions_legal=4
transitions_illegal=2
transitions_illegal_tested=2
usecase_paths=7
usecase_paths_tested=7
configs=6
pairs_target=16
pairs_covered=16
pairs_missing=0
```
