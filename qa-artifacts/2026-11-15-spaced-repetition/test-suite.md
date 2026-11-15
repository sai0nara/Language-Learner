# Test Suite — Spaced Repetition (SM-2)

**Run:** `2026-11-15-spaced-repetition`
**Total Cases:** `38` (`P0: 14`, `P1: 13`, `P2: 11`)

---

## 1. Test Cases

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | Quality below 0 is clamped to 0 and resets SM-2 | EP, BVA, Decision Table | `SrsReviewEntity(repetition=2, intervalDays=6, easeFactor=2.5f)` | `quality = -1` | `lastQuality == 0`, `repetition == 0`, `intervalDays == 1`, `easeFactor == 1.70f` | P0 | `P1, R1` |
| `TC-002` | Quality 0 at easeFactor floor 1.3f preserves floor | EP, BVA, Decision Table | `SrsReviewEntity(repetition=0, intervalDays=1, easeFactor=1.3f)` | `quality = 0` | `lastQuality == 0`, `repetition == 0`, `intervalDays == 1`, `easeFactor == 1.3f` (never `< 1.3f`) | P0 | `P1, P2, R1` |
| `TC-003` | Quality 1 (AGAIN) resets repetition 1 to 0 | EP, BVA, Decision Table | `SrsReviewEntity(repetition=1, intervalDays=1, easeFactor=2.5f)` | `quality = 1` (`AGAIN`) | `lastQuality == 1`, `repetition == 0`, `intervalDays == 1`, `easeFactor == 1.96f` | P0 | `P1, R1` |
| `TC-004` | Quality 2 (HARD) resets repetition 3 and sets intervalDays=1 | EP, BVA, Decision Table | `SrsReviewEntity(repetition=3, intervalDays=15, easeFactor=2.5f)` | `quality = 2` (`HARD`) | `lastQuality == 2`, `repetition == 0`, `intervalDays == 1` (not `2`), `easeFactor == 2.18f` | P0 | `P1, P3, R1` |
| `TC-005` | Quality 3 pass threshold at repetition 0 and easeFactor 1.4f | EP, BVA, Decision Table | `SrsReviewEntity(repetition=0, intervalDays=1, easeFactor=1.4f)` | `quality = 3` | `lastQuality == 3`, `repetition == 1`, `intervalDays == 1`, `easeFactor == 1.3f` (floored) | P0 | `P1, P2, P3, R1` |
| `TC-006` | Quality 4 (GOOD) at repetition 1 schedules 6-day interval | EP, BVA, Decision Table | `SrsReviewEntity(repetition=1, intervalDays=1, easeFactor=2.5f)` | `quality = 4` (`GOOD`) | `lastQuality == 4`, `repetition == 2`, `intervalDays == 6`, `easeFactor == 2.5f` | P0 | `P1, P3, R1` |
| `TC-007` | Quality 5 (EASY) at repetition 2 multiplies interval by newEaseFactor | EP, BVA, Decision Table | `SrsReviewEntity(repetition=2, intervalDays=6, easeFactor=2.5f)` | `quality = 5` (`EASY`) | `lastQuality == 5`, `repetition == 3`, `easeFactor == 2.6f`, `intervalDays == 16` | P0 | `P1, P3, R1` |
| `TC-008` | Quality above 5 clamped to 5 and easeFactor below 1.3f floored | EP, BVA | `SrsReviewEntity(repetition=2, intervalDays=1, easeFactor=1.2f)` | `quality = 6` | `lastQuality == 5`, `easeFactor == 1.3f`, `intervalDays == 1`, `repetition == 3` | P0 | `P1, P2` |
| `TC-009` | Negative repetition falls into else branch safely | EP, BVA | `SrsReviewEntity(repetition=-1, intervalDays=1, easeFactor=2.5f)` | `quality = 4` | `intervalDays == 3`, `repetition == 0`, `easeFactor == 2.5f` | P0 | `P3` |
| `TC-010` | Card 1ms in future is excluded from due items and disables launcher | EP, BVA | `srs_reviews` has 1 card with `nextReviewTimestamp = now + 1` | `currentTimestamp = now` | `getDueItems(now)` empty; `getDueCount(now) == 0`; `tab_srs` badge hidden; `start_srs_review_button` disabled | P2 | `P4, P5` |
| `TC-011` | Card due at exact timestamp is included and enables launcher | EP, BVA | `srs_reviews` has 1 card with `nextReviewTimestamp = now` | `currentTimestamp = now` | `getDueItems(now)` returns 1 item; `getDueCount(now) == 1`; `tab_srs` badge `"1"`; `start_srs_review_button` enabled | P2 | `P4, P5` |
| `TC-012` | Overdue card (1ms past) is returned ordered ascending | EP, BVA | `srs_reviews` has cards at `now - 1` and `now` | `currentTimestamp = now` | `getDueItems(now)` returns `[now - 1, now]` in ascending `nextReviewTimestamp` order; `getDueCount(now) == 2` | P2 | `P4` |
| `TC-013` | Negative dueCount keeps badge hidden and launcher disabled | EP, BVA | `CurriculumScreen` rendered with `dueCount = -1` | `dueCount = -1` | `tab_srs` badge not rendered; `start_srs_review_button` disabled | P2 | `P5` |
| `TC-014` | Latin mode with non-blank secondaryText shows secondaryText as prompt | Decision Table | Due card `prompt="салам"`, `secondaryText="salam"` | `isLatin = true` | `srs_prompt_text` displays `"salam"`; secondary line below prompt not rendered | P2 | `R2` |
| `TC-015` | Latin mode with blank secondaryText falls back to prompt | Decision Table | Due card `prompt="салам"`, `secondaryText=""` | `isLatin = true` | `srs_prompt_text` displays `"салам"`; secondary line not rendered | P2 | `R2` |
| `TC-016` | Non-Latin mode with non-blank secondaryText shows both | Decision Table | Due card `prompt="салам"`, `secondaryText="salam"` | `isLatin = false` | `srs_prompt_text` displays `"салам"`; `"salam"` renders below prompt | P2 | `R2` |
| `TC-017` | Non-Latin mode with blank secondaryText hides secondary line | Decision Table | Due card `prompt="салам"`, `secondaryText=""` | `isLatin = false` | `srs_prompt_text` displays `"салам"`; secondary line not rendered | P2 | `R2` |
| `TC-018` | End-to-end happy path review of single due card to empty queue | Use Case, State Transition | 1 due card in `srs_reviews` (`dueCount = 1`) | Tap `start_srs_review_button` -> `show_answer_button` -> `grade_good` -> `srs_done_back_button` | `S1`: `show_answer_button` (`"Показать перевод"`) visible -> `S2`: `srs_answer_text` & `"Интервал: ..."` visible, `playAudio(..., true)` called -> `S3`: `srs_reviews` updated, `"Очередь чиста!"` shown -> returns to lessons | P1 | `U1, S1, S2, S3, T1, T4, T5` |
| `TC-019` | Grading first of two due cards resets reveal and advances queue | State Transition | 2 due cards in `srs_reviews`, `S2` revealed on index `0` | Tap `grade_easy` | `recordReview` persists card 0 in Room; `isRevealed` resets to `false` (`S1`); `currentIndex` advances to next card | P1 | `T3` |
| `TC-020` | Speaker IconButton replays audio without revealing answer | Use Case, State Transition | `S1 (Due_Unrevealed)` | Tap speaker `IconButton` | `playAudio(promptText, isTargetLanguage = true)` called; `isRevealed` remains `false`; `srs_answer_text` not shown | P2 | `T2, U1` |
| `TC-021` | Top-bar back button exits review without grading | State Transition | `S1` or `S2` active | Tap `back_from_srs` | Navigates back; `recordReview` not called; `srs_reviews` unchanged | P2 | `T6` |
| `TC-022` | Illegal transition: grade buttons absent while card unrevealed | State Transition | `S1 (Due_Unrevealed)` | Attempt to find/click `grade_again`..`grade_easy` | `grade_again`..`grade_easy` do not exist in UI tree; `recordReview` cannot be triggered | P1 | `T7` |
| `TC-023` | Illegal transition: reveal and grade absent when queue is empty | Use Case, State Transition | `S3 (Queue_Empty)`, `dueItems = emptyList()` | Attempt to find/click `show_answer_button` or `grade_good` | `show_answer_button` & `GradeButton`s absent; `"Очередь чиста!"` & `srs_done_back_button` displayed | P1 | `T8, U1` |
| `TC-024` | Idempotent enrollInSrs preserves existing SRS progress | Use Case | `srs_reviews` contains `vocab.id` with `rep=3, ef=2.6f` | `repository.enrollInSrs(vocab)` | Existing row in `srs_reviews` is not overwritten (`rep == 3`, `ef == 2.6f`) | P0 | `U1` |
| `TC-025` | Failing recall (AGAIN) workflow resets card and updates Room | Use Case | 1 due card with `rep=2, intervalDays=6, ef=2.5f` in `S2` | Tap `grade_again` | `recordReview(item, AGAIN)` updates Room: `rep=0`, `intervalDays=1`, `lastQuality=1`, `ef=1.96f` | P1 | `U1` |
| `TC-026` | Pairwise config 1: AGAIN + Latin + VOCAB | Pairwise | Due `VOCAB` card, `isLatin=true`, `S2` | Tap `grade_again` | `srs_prompt_text` used `secondaryText`; Room updated with `lastQuality=1, rep=0, intervalDays=1` | P1 | `C1` |
| `TC-027` | Pairwise config 2: AGAIN + TargetScript + SENTENCE | Pairwise | Due `SENTENCE` card, `isLatin=false`, `S2` | Tap `grade_again` | `srs_prompt_text` used `prompt`; Room updated with `lastQuality=1, rep=0, intervalDays=1` | P1 | `C1` |
| `TC-028` | Pairwise config 3: HARD + Latin + SENTENCE | Pairwise | Due `SENTENCE` card, `isLatin=true`, `S2` | Tap `grade_hard` | `srs_prompt_text` used `secondaryText`; Room updated with `lastQuality=2, rep=0, intervalDays=1` | P1 | `C1` |
| `TC-029` | Pairwise config 4: HARD + TargetScript + VOCAB | Pairwise | Due `VOCAB` card, `isLatin=false`, `S2` | Tap `grade_hard` | `srs_prompt_text` used `prompt`; Room updated with `lastQuality=2, rep=0, intervalDays=1` | P1 | `C1` |
| `TC-030` | Pairwise config 5: GOOD + Latin + VOCAB | Pairwise | Due `VOCAB` card, `isLatin=true`, `S2` | Tap `grade_good` | `srs_prompt_text` used `secondaryText`; Room updated with `lastQuality=4, rep+=1` | P1 | `C1` |
| `TC-031` | Pairwise config 6: GOOD + TargetScript + SENTENCE | Pairwise | Due `SENTENCE` card, `isLatin=false`, `S2` | Tap `grade_good` | `srs_prompt_text` used `prompt`; Room updated with `lastQuality=4, rep+=1` | P1 | `C1` |
| `TC-032` | Pairwise config 7: EASY + Latin + SENTENCE | Pairwise | Due `SENTENCE` card, `isLatin=true`, `S2` | Tap `grade_easy` | `srs_prompt_text` used `secondaryText`; Room updated with `lastQuality=5, rep+=1` | P1 | `C1` |
| `TC-033` | Pairwise config 8: EASY + TargetScript + VOCAB | Pairwise | Due `VOCAB` card, `isLatin=false`, `S2` | Tap `grade_easy` | `srs_prompt_text` used `prompt`; Room updated with `lastQuality=5, rep+=1` | P1 | `C1` |
| `TC-034` | Empty prompt, answer, and secondaryText render safely | Error Guessing | Due card with `prompt=""`, `answer=""`, `secondaryText=""` | Render `S1` -> tap `show_answer_button` | No crash; empty `srs_prompt_text` & `srs_answer_text` render; Room record intact | P2 | `R2, U1` |
| `TC-035` | Multi-byte Unicode (CJK, emoji, RTL) round-trips in Room and UI | Error Guessing | Due card with `prompt="مرحبا 🇯🇵"`, `answer="привет 😊"` | Insert -> `getDueItems` -> render `S1`/`S2` | Exact UTF-8 strings preserved in `srs_reviews` and displayed in `srs_prompt_text` / `srs_answer_text` | P0 | `R2, U1` |
| `TC-036` | SQL injection string in id/prompt/lessonId handled safely by Room | Error Guessing | Card with `id="' OR '1'='1; DROP TABLE srs_reviews;--"` | `insertOrUpdate` -> `getDueItems` -> `getSrsItemById` | Literal string stored and queried safely; `srs_reviews` table intact | P0 | `P4, U1` |
| `TC-037` | Rapid double-tap on grade button does not crash or corrupt index | Error Guessing | 1 due card in `S2` | Rapidly tap `grade_good` twice | No `IndexOutOfBoundsException`; `currentIndex` stays `0`; transitions cleanly to `S3` | P0 | `T3, T4, U1` |
| `TC-038` | DST / midnight clock boundary uses UTC epoch ms consistently | Error Guessing | `now` at `23:59:59.999` / spring-forward `02:30` | `calculateNextReview(item, 4)` -> `getDueItems(now + ONE_DAY_MS)` | `nextReviewTimestamp == now + 86_400_000L`; due query at `now + ONE_DAY_MS` includes card | P0 | `P4, R1` |

---

## 2. Gherkin Scenarios (Behavioral Cases)

```gherkin
Feature: Spaced Repetition Flashcard Review

  @TC-018
  Scenario: Complete happy-path review of a single due card
    Given 1 due card exists in srs_reviews and dueCount is 1
    When the learner taps start_srs_review_button
    And taps show_answer_button
    And taps grade_good
    And taps srs_done_back_button
    Then target-language audio plays on reveal
    And srs_reviews updates the card repetition and nextReviewTimestamp
    And the empty-state message "Очередь чиста!" is displayed before returning

  @TC-020
  Scenario: Replay audio from speaker icon without revealing answer
    Given the learner is viewing an unrevealed due card
    When the learner taps the card speaker IconButton
    Then playAudio is invoked with isTargetLanguage true
    And srs_answer_text remains hidden

  @TC-022
  Scenario: Grading controls are unavailable before answer reveal
    Given the learner is viewing an unrevealed due card
    Then grade_again, grade_hard, grade_good, and grade_easy are not rendered

  @TC-023
  Scenario: Empty queue prevents review actions and shows completion state
    Given no cards are due in srs_reviews
    When SpacedRepetitionScreen is displayed
    Then show_answer_button and grade buttons are not rendered
    And "Очередь чиста!" and srs_done_back_button are displayed
```

---

## 3. Risk Register

| Test ID | Impact | Probability Signals Counted | Unassessable Signals | Probability | Priority |
|---|---|---|---|---|---|
| `TC-001` | 5 (data corruption) | `boolean-logic complexity` (1) | none | Low (1) | `P0` |
| `TC-002` | 5 (data corruption) | `boolean-logic complexity`, `boundary density` (2) | none | Medium (2) | `P0` |
| `TC-003` | 4 (data mutation) | `boolean-logic complexity`, `boundary density` (2) | none | Medium (2) | `P0` |
| `TC-004` | 4 (data mutation) | `boolean-logic complexity`, `boundary density` (2) | none | Medium (2) | `P0` |
| `TC-005` | 4 (data mutation) | `boolean-logic complexity`, `boundary density` (2) | none | Medium (2) | `P0` |
| `TC-006` | 4 (data mutation) | `boolean-logic complexity`, `boundary density` (2) | none | Medium (2) | `P0` |
| `TC-007` | 4 (data mutation) | `boolean-logic complexity`, `boundary density` (2) | none | Medium (2) | `P0` |
| `TC-008` | 5 (data corruption) | `boundary density` (1) | none | Low (1) | `P0` |
| `TC-009` | 5 (data corruption) | none (0) | none | Low (0) | `P0` |
| `TC-010` | 3 (core workflow) | none (0) | none | Low (0) | `P2` |
| `TC-011` | 3 (core workflow) | none (0) | none | Low (0) | `P2` |
| `TC-012` | 3 (core workflow) | none (0) | none | Low (0) | `P2` |
| `TC-013` | 2 (degraded/defensive) | none (0) | none | Low (0) | `P2` |
| `TC-014` | 1 (display-only) | none (0) | none | Low (0) | `P2` |
| `TC-015` | 1 (display-only) | none (0) | none | Low (0) | `P2` |
| `TC-016` | 1 (display-only) | none (0) | none | Low (0) | `P2` |
| `TC-017` | 1 (display-only) | none (0) | none | Low (0) | `P2` |
| `TC-018` | 4 (data mutation) | none (0) | none | Low (0) | `P1` |
| `TC-019` | 4 (data mutation) | none (0) | none | Low (0) | `P1` |
| `TC-020` | 2 (secondary flow) | none (0) | none | Low (0) | `P2` |
| `TC-021` | 3 (workflow navigation) | none (0) | none | Low (0) | `P2` |
| `TC-022` | 4 (state integrity) | `state-machine depth` (1) | none | Low (1) | `P1` |
| `TC-023` | 4 (state integrity) | `state-machine depth` (1) | none | Low (1) | `P1` |
| `TC-024` | 5 (data corruption) | none (0) | none | Low (0) | `P0` |
| `TC-025` | 4 (data mutation) | none (0) | none | Low (0) | `P1` |
| `TC-026`..`TC-033` | 4 (data mutation) | none (0) | none | Low (0) | `P1` |
| `TC-034` | 3 (crash resilience) | none (0) | none | Low (0) | `P2` |
| `TC-035` | 5 (data corruption) | `known-fragile pattern` (1) | none | Low (1) | `P0` |
| `TC-036` | 5 (data loss/corruption) | none (0) | none | Low (0) | `P0` |
| `TC-037` | 5 (crash/corruption) | `known-fragile pattern` (1) | none | Low (1) | `P0` |
| `TC-038` | 4 (data mutation) | `known-fragile pattern`, `boundary density` (2) | none | Medium (2) | `P0` |

```qa-summary
schema_version=1
gate_status=approved
cases_total=38
cases_p0=14
cases_p1=13
cases_p2=11
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
```
