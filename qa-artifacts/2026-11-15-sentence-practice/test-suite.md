# Test Suite — `sentence-practice`

## 1. Test Cases

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | `cleanToken` normalizes uppercase, whitespace, and Unicode punctuation/symbols | EP, Error Guessing | `SentencePracticeScreen` initialized with Cyrillic and Latin tokens | `tokensJson = ["  Привет! ", "«Мир»—", "Čaša?"]` | UI: `tile_pool` renders `pool_chip_привет`, `pool_chip_мир`, `pool_chip_čaša` in lowercase with punctuation/symbols stripped; Backend: `cleanToken` produces `["привет", "мир", "čaša"]` | `P2` | `P1` |
| `TC-002` | `cleanToken` filters out empty, whitespace-only, and punctuation-only tokens | EP, Error Guessing | `SentencePracticeScreen` initialized with degenerate tokens | `tokensJson = ["", "   ", ".,!?—", "да"]` | UI: `tile_pool` renders only `pool_chip_да`; Backend: blank-after-clean tokens are filtered out via `filter { it.isNotBlank() }` | `P2` | `P1` |
| `TC-003` | `masteryScore` at `0.0f` and `0.1f` displays formatted score without unlock | EP, BVA | Lesson exists in Room `lessons` table | `masteryScore = 0.0f` (then `0.1f`), `correctAnswersInLast100 = 0` (then `2`) | UI: top bar displays `"0.0"` (then `"0.1"`), summary hides unlock banner; Backend: `isCompleted == false`, next lesson `isUnlocked == false` | `P1` | `P2` |
| `TC-004` | `masteryScore` below lower bound (`-0.1f`) is rejected/clamped | EP, BVA | Lesson mastery calculation invoked with out-of-range negative score | `masteryScore = -0.1f` | Backend: invalid partition; rolling formula `(correctAnswersInLast100 / 100f) * 5.0f` never produces negative values and `isCompleted == false` | `P1` | `P2` |
| `TC-005` | `masteryScore` at `4.4f` (just below `4.5f` threshold) keeps lesson incomplete | EP, BVA | Lesson has `88` correct in last `100` answers | `masteryScore = 4.4f` | UI: top bar displays `"4.4"`, summary view omits unlock banner; Backend: `isCompleted == false`, next lesson `orderIndex + 1` remains `isUnlocked == false` | `P1` | `P2` |
| `TC-006` | `masteryScore` at `4.5f` and `4.6f` marks lesson complete and unlocks next lesson | EP, BVA | Lesson has `90` (then `92`) correct in last `100` answers | `masteryScore = 4.5f` (then `4.6f`) | UI: top bar displays `"4.5"` (then `"4.6"`), summary view renders unlock banner; Backend: `isCompleted == true`, next lesson `orderIndex + 1` has `isUnlocked == true` | `P1` | `P2` |
| `TC-007` | `masteryScore` at `4.9f` and `5.0f` (valid upper bound) | EP, BVA | Lesson has `98` (then `100`) correct in last `100` answers | `masteryScore = 4.9f` (then `5.0f`) | UI: top bar displays `"4.9"` (then `"5.0"`), summary view renders unlock banner; Backend: `isCompleted == true` | `P1` | `P2` |
| `TC-008` | `masteryScore` above upper bound (`5.1f`) is invalid | EP, BVA | Lesson mastery calculation evaluated above max | `masteryScore = 5.1f` | Backend: invalid partition; rolling 100-answer formula caps at `5.0f` (`100 / 100f * 5.0f`) | `P1` | `P2` |
| `TC-009` | Empty `rawSentences` list renders immediate summary/empty-state view | EP, BVA, State Transition, Use Case | `SentenceDao` returns `emptyList()` for lesson | `rawSentences = []` | UI: `"Упражнение завершено!"` and `finish_practice_button` (`"Вернуться к уроку"`) rendered immediately; `assembly_bay` and `tile_pool` absent | `P2` | `P3`, `S1`, `U1` |
| `TC-010` | Single-sentence session completes directly to summary after one correct check | EP, BVA, State Transition | `rawSentences` has 1 sentence; `S2` | `rawSentences.size = 1`, correct tokens placed, tap `check_answer_button` | UI: `"Правильно!"` shown, after `1200 ms` transitions to `S1` (`"Упражнение завершено!"`); Backend: `isFinished == true`, `recordAnswer(lesson.id, true)` called once | `P1` | `P3`, `S1`, `S4`, `T6` |
| `TC-011` | `check_answer_button` disabled when `assembly_bay` is empty (DT Col 1 / Illegal `T12`) | Decision Table, State Transition | `S2` (`placedTokens.isEmpty()`), `rawSentences` non-empty | Attempt click on `check_answer_button` | UI: `check_answer_button` is disabled, `validationResult == null`, `feedbackMessage == ""`; Backend: `viewModel.recordAnswer` and `playAudio` not called | `P0` | `R1`, `S2`, `T12` |
| `TC-012` | Correct Cyrillic answer check triggers feedback, `recordAnswer(true)`, haptic, audio, and auto-advance (DT Col 2) | Decision Table, State Transition, Pairwise, Use Case | `S3`, `isLatin = false`, `tokensJson` valid, `distractorTokensJson` non-empty, `grammarHint` non-blank, `sentenceQueue.size >= 2` | Place all Cyrillic target tokens in order, tap `check_answer_button` | UI: `feedbackMessage == "Правильно!"`, `correctCount` +1, `SegmentedProgressBar` advances after `1200 ms` to next sentence (`S2`); Backend: `recordAnswer(lesson.id, true)`, 50 ms vibration (`SDK_INT >= O`), `playAudio(targetSentence, audioUri, true)` | `P0` | `R1`, `S3`, `S4`, `T4`, `T5`, `C1`, `U1` |
| `TC-013` | Incorrect Cyrillic answer check reveals target, records `false`, vibrates 120 ms, and re-queues sentence (DT Col 3) | Decision Table, State Transition, Use Case | `S3`, `isLatin = false` | Place distractor or wrong-order Cyrillic tokens, tap `check_answer_button` | UI: `feedbackMessage == "Правильный ответ: $targetSentence"`, `wrongCount` +1, stays on sentence; Backend: `recordAnswer(lesson.id, false)`, 120 ms vibration, `playAudio` NOT called, `currentSentence` appended to end of `sentenceQueue` | `P0` | `R1`, `S3`, `S5`, `T7`, `U1` |
| `TC-014` | Correct Latin answer check validates against `targetSentenceLatin` (DT Col 4) | Decision Table | `S3`, `isLatin = true`, `tokensJson` contains Latin tokens | Place Latin target tokens matching `targetSentenceLatin`, tap `check_answer_button` | UI: `feedbackMessage == "Правильно!"`, `correctCount` +1; Backend: `recordAnswer(lesson.id, true)`, `playAudio(targetSentenceLatin, audioUri, true)` | `P0` | `R1`, `S4`, `T4` |
| `TC-015` | Incorrect Latin answer check reveals `targetSentenceLatin` and records `false` (DT Col 5) | Decision Table | `S3`, `isLatin = true` | Place mismatched Latin tokens, tap `check_answer_button` | UI: `feedbackMessage == "Правильный ответ: $targetSentenceLatin"`, `wrongCount` +1; Backend: `recordAnswer(lesson.id, false)`, `currentSentence` appended to `sentenceQueue` | `P0` | `R1`, `S5`, `T7` |
| `TC-016` | Tapping chips moves tokens between `tile_pool` and `assembly_bay` (`S2 -> S3 -> S3 -> S2`) | State Transition, Use Case | `S2` with tokens `"ја"` and `"сам"` in `tile_pool` | Tap `pool_chip_ја`, tap `pool_chip_сам`, tap `placed_chip_сам`, tap `placed_chip_ја` | UI: `check_answer_button` enables on first tap (`S3`), remains enabled while 1 token stays (`S3`), disables when last token returns to `tile_pool` (`S2`); Backend: `playAudio` NOT called on chip taps | `P2` | `S2`, `S3`, `T1`, `T2`, `T3`, `U1` |
| `TC-017` | Modifying tokens after incorrect validation clears error feedback (`S5 -> S3 -> S2`) | State Transition | `S5` (`validationResult == false`, `feedbackMessage` visible, 1 token in `assembly_bay`) | Tap `pool_chip_$token` (to `S3`), then tap both `placed_chip_*` tokens back to `tile_pool` (to `S2`) | UI: `validationResult` resets to `null` and `feedbackMessage` clears to `""` on first tap (`S3`); `check_answer_button` disables when `assembly_bay` empties (`S2`) | `P2` | `S2`, `S3`, `S5`, `T8`, `T9` |
| `TC-018` | Help dialog opens with `grammarHint` and dismisses via `"Понятно"` or `onDismissRequest` | State Transition, Use Case | `S2` or `S3`, non-blank `grammarHint` | Tap `sentence_hint_button`, verify dialog, tap `"Понятно"` (repeat with `onDismissRequest`) | UI: `AlertDialog` opens (`S6`) showing `grammarHint` (without target sentence), closes on `"Понятно"` / `onDismissRequest` returning to `S2`/`S3` without altering `placedTokens` | `P2` | `S6`, `T10`, `T11`, `U1` |
| `TC-019` | Drilling controls are inaccessible in Finished/Empty summary state (Illegal `T13`) | State Transition | `S1` (`isFinished == true` or `rawSentences.isEmpty()`) | Query `assembly_bay`, `tile_pool`, `check_answer_button` | UI: `assembly_bay`, `tile_pool`, and `check_answer_button` do not exist in semantics tree; only `finish_practice_button` is actionable | `P2` | `S1`, `T13` |
| `TC-020` | Pairwise Config 2: `Cyrillic`, `Malformed tokensJson`, `Empty distractors`, `Non-blank hint` | Pairwise | `SentencePracticeScreen` initialized with Config 2 | `tokensJson = "{bad"`, `distractorTokensJson = "[]"`, `grammarHint = "Hint"` | UI: `tile_pool` populated from `targetSentence.split(" ")` fallback without crash; `AlertDialog` shows `"Hint"` | `P2` | `C1` |
| `TC-021` | Pairwise Config 3: `Cyrillic`, `Valid tokensJson`, `Malformed distractors`, `Blank hint` | Pairwise | `SentencePracticeScreen` initialized with Config 3 | `distractorTokensJson = "bad_json"`, `grammarHint = "   "` | UI: `tile_pool` contains only valid `tokensJson` tokens (empty distractor fallback); `AlertDialog` shows default fallback hint string | `P2` | `C1` |
| `TC-022` | Pairwise Config 4: `Latin`, `Malformed tokensJson`, `Non-empty distractors`, `Blank hint` | Pairwise | `SentencePracticeScreen` initialized with Config 4 | `isLatin = true`, `tokensJson = "{bad"`, `grammarHint = ""` | UI: screen renders without crash using `targetSentence.split(" ")` fallback + distractors; `AlertDialog` shows default fallback hint string | `P2` | `C1` |
| `TC-023` | Pairwise Config 5: `Latin`, `Valid tokensJson`, `Empty distractors`, `Blank hint` | Pairwise | `SentencePracticeScreen` initialized with Config 5 | `isLatin = true`, `distractorTokensJson = "[]"`, `grammarHint = ""` | UI: `tile_pool` contains only cleaned `tokensJson` tokens; `AlertDialog` shows default fallback hint string | `P2` | `C1` |
| `TC-024` | Pairwise Config 6: `Latin`, `Malformed tokensJson`, `Malformed distractors`, `Non-blank hint` | Pairwise | `SentencePracticeScreen` initialized with Config 6 | `isLatin = true`, `tokensJson = "["`, `distractorTokensJson = "["`, `grammarHint = "Latin hint"` | UI: screen recovers via `try/catch` fallbacks without crash; `AlertDialog` shows `"Latin hint"` | `P2` | `C1` |
| `TC-025` | Speaker `IconButton` plays target sentence audio without recording an answer | Use Case | `S2`, `source_sentence_text` visible | Tap speaker `IconButton` | Backend: `viewModel.playAudio(currentTarget, currentSentence.audioUri, isTargetLanguage = true)` called; `recordAnswer` NOT called | `P2` | `U1` |
| `TC-026` | Top bar back button exits practice without recording unanswered attempt | Use Case | `S2` or `S3` | Tap `back_from_sentence_practice` | UI: navigates back from `SentencePracticeScreen`; Backend: `recordAnswer` NOT called for unsubmitted sentence | `P2` | `U1` |
| `TC-027` | SQL injection / special characters in `sourceSentence` and `tokensJson` handled safely | Error Guessing | Room `sentences` table populated with adversarial strings | `sourceSentence = "'; DROP TABLE sentences;--"`, `tokensJson = ["SELECT*", "1=1"]` | UI: `source_sentence_text` renders literal text safely; `tile_pool` strips `*=`; Backend: `sentences` table intact, no Room/JSON exception | `P0` | `P1` |
| `TC-028` | Duplicate identical tokens in `tokensJson` move individually by index | Error Guessing | `S2` with two `"и"` tokens in `tokensJson` | Tap first `pool_chip_и` | UI: one `"и"` moves to `placed_chip_и` in `assembly_bay`, second `"и"` remains in `tile_pool` | `P2` | `S2`, `S3` |

## 2. Gherkin Scenarios

```gherkin
Feature: Sentence Construction Practice

  @TC-001
  Scenario: cleanToken normalizes uppercase, whitespace, and Unicode punctuation
    Given the sentence practice screen loads tokens ["  Привет! ", "«Мир»—", "Čaša?"]
    When the token pool is initialized
    Then "tile_pool" displays "pool_chip_привет", "pool_chip_мир", and "pool_chip_čaša"

  @TC-002
  Scenario: cleanToken filters out empty and punctuation-only tokens
    Given the sentence practice screen loads tokens ["", "   ", ".,!?—", "да"]
    When the token pool is initialized
    Then "tile_pool" displays only "pool_chip_да"

  @TC-003
  Scenario: Mastery score at 0.0f and 0.1f displays formatted grade without unlock
    Given a lesson has masteryScore 0.0f
    When the sentence practice screen is displayed
    Then the top bar grade shows "0.0"
    And "isCompleted" remains false and the next lesson remains locked

  @TC-004
  Scenario: Mastery score below 0.0f is outside valid domain
    Given the mastery score calculation runs on rolling 100 answers
    When an invalid negative score -0.1f is evaluated
    Then the rolling formula rejects negative output and "isCompleted" is false

  @TC-005
  Scenario: Mastery score at 4.4f keeps lesson incomplete
    Given a lesson has 88 correct answers in the last 100 attempts with masteryScore 4.4f
    When the summary screen is displayed
    Then the top bar grade shows "4.4"
    And the unlock banner is not displayed and the next lesson remains locked

  @TC-006
  Scenario: Mastery score at 4.5f and 4.6f completes lesson and unlocks next lesson
    Given a lesson has 90 correct answers in the last 100 attempts with masteryScore 4.5f
    When the summary screen is displayed
    Then the top bar grade shows "4.5"
    And "isCompleted" is true, the next lesson is unlocked, and the unlock banner is shown

  @TC-007
  Scenario: Mastery score at 4.9f and 5.0f is valid and completed
    Given a lesson has 100 correct answers in the last 100 attempts with masteryScore 5.0f
    When the summary screen is displayed
    Then the top bar grade shows "5.0"
    And "isCompleted" is true and the unlock banner is shown

  @TC-008
  Scenario: Mastery score above 5.0f is outside valid domain
    Given the mastery score calculation runs on rolling 100 answers
    When an invalid score 5.1f is evaluated
    Then the rolling formula bounds the maximum at 5.0f

  @TC-009
  Scenario: Empty sentence list renders immediate summary state
    Given the lesson has 0 sentences in Room
    When the sentence practice screen opens
    Then "Упражнение завершено!" and "finish_practice_button" are displayed
    And "assembly_bay" and "tile_pool" are not displayed

  @TC-010
  Scenario: Single-sentence session finishes directly to summary on correct check
    Given the lesson has 1 sentence and all correct tokens are placed in "assembly_bay"
    When the user taps "check_answer_button"
    Then "Правильно!" is displayed and "recordAnswer" is called with true
    And after 1200 ms the screen transitions to "Упражнение завершено!"

  @TC-011
  Scenario: Check button is disabled when assembly bay is empty
    Given the user is in "S2" with an empty "assembly_bay"
    When the user attempts to tap "check_answer_button"
    Then "check_answer_button" is disabled
    And "recordAnswer" and "playAudio" are not called

  @TC-012
  Scenario: Correct Cyrillic answer triggers feedback, audio, haptic, and auto-advance
    Given the user is drilling in Cyrillic mode with valid tokens, distractors, and hint
    And the user places the exact cleaned Cyrillic target tokens into "assembly_bay"
    When the user taps "check_answer_button"
    Then "Правильно!" is displayed and "correctCount" increments by 1
    And "recordAnswer" is called with true, 50 ms vibration triggers, and "playAudio" is called
    And after 1200 ms the next sentence loads

  @TC-013
  Scenario: Incorrect Cyrillic answer reveals target, records false, vibrates 120 ms, and re-queues
    Given the user is drilling in Cyrillic mode
    And the user places an incorrect token sequence into "assembly_bay"
    When the user taps "check_answer_button"
    Then "Правильный ответ: " followed by "targetSentence" is displayed and "wrongCount" increments by 1
    And "recordAnswer" is called with false, 120 ms vibration triggers, and "playAudio" is not called
    And the current sentence is appended to the end of "sentenceQueue"

  @TC-014
  Scenario: Correct Latin answer validates against targetSentenceLatin
    Given the user is drilling in Latin mode with Latin tokens
    And the user places tokens matching "targetSentenceLatin" into "assembly_bay"
    When the user taps "check_answer_button"
    Then "Правильно!" is displayed and "recordAnswer" is called with true
    And "playAudio" is called with "targetSentenceLatin"

  @TC-015
  Scenario: Incorrect Latin answer reveals targetSentenceLatin and records false
    Given the user is drilling in Latin mode
    And the user places tokens not matching "targetSentenceLatin" into "assembly_bay"
    When the user taps "check_answer_button"
    Then "Правильный ответ: " followed by "targetSentenceLatin" is displayed
    And "recordAnswer" is called with false and the sentence is appended to "sentenceQueue"

  @TC-016
  Scenario: Tapping chips moves tokens between tile_pool and assembly_bay
    Given "tile_pool" contains "pool_chip_ја" and "pool_chip_сам"
    When the user taps "pool_chip_ја", taps "pool_chip_сам", then taps "placed_chip_сам" and "placed_chip_ја"
    Then "check_answer_button" is enabled while tokens are placed and disabled when "assembly_bay" is empty
    And "playAudio" is not called on any chip tap

  @TC-017
  Scenario: Modifying tokens after wrong validation clears feedback
    Given the screen is in "S5" with "Правильный ответ:" displayed and 1 placed token
    When the user taps a chip in "tile_pool" and then returns all placed tokens to "tile_pool"
    Then "feedbackMessage" is cleared and "check_answer_button" becomes disabled

  @TC-018
  Scenario: Help dialog opens with grammarHint and dismisses cleanly
    Given the user is drilling a sentence with a non-blank "grammarHint"
    When the user taps "sentence_hint_button"
    Then an AlertDialog displays "grammarHint" without the target sentence
    When the user taps "Понятно"
    Then the AlertDialog closes and "placedTokens" is preserved

  @TC-019
  Scenario: Drilling controls are absent in Finished summary state
    Given the session is in "S1" finished state
    When the UI tree is inspected
    Then "assembly_bay", "tile_pool", and "check_answer_button" do not exist
    And only "finish_practice_button" is actionable

  @TC-020
  Scenario: Pairwise Config 2 (Cyrillic, Malformed tokensJson, Empty distractors, Non-blank hint)
    Given a Cyrillic sentence with malformed "tokensJson", "[]" distractors, and non-blank "grammarHint"
    When the sentence loads
    Then "tile_pool" falls back to "targetSentence.split(' ')" without crashing
    And the help dialog displays the provided "grammarHint"

  @TC-021
  Scenario: Pairwise Config 3 (Cyrillic, Valid tokensJson, Malformed distractors, Blank hint)
    Given a Cyrillic sentence with valid "tokensJson", malformed "distractorTokensJson", and blank "grammarHint"
    When the sentence loads
    Then "tile_pool" loads with empty distractors fallback
    And the help dialog displays the default fallback hint string

  @TC-022
  Scenario: Pairwise Config 4 (Latin, Malformed tokensJson, Non-empty distractors, Blank hint)
    Given a Latin sentence with malformed "tokensJson", non-empty distractors, and blank "grammarHint"
    When the sentence loads
    Then the screen renders via "targetSentence.split(' ')" fallback without crashing
    And the help dialog displays the default fallback hint string

  @TC-023
  Scenario: Pairwise Config 5 (Latin, Valid tokensJson, Empty distractors, Blank hint)
    Given a Latin sentence with valid "tokensJson", "[]" distractors, and blank "grammarHint"
    When the sentence loads
    Then "tile_pool" contains only cleaned "tokensJson" tokens
    And the help dialog displays the default fallback hint string

  @TC-024
  Scenario: Pairwise Config 6 (Latin, Malformed tokensJson, Malformed distractors, Non-blank hint)
    Given a Latin sentence with malformed "tokensJson", malformed "distractorTokensJson", and non-blank "grammarHint"
    When the sentence loads
    Then both JSON fallbacks recover without crashing
    And the help dialog displays the provided "grammarHint"

  @TC-025
  Scenario: Speaker IconButton plays target audio without mutating lesson score
    Given the user is viewing a sentence prompt
    When the user taps the speaker "IconButton"
    Then "playAudio" is called with the target sentence and "recordAnswer" is not called

  @TC-026
  Scenario: Back button exits practice without recording an answer
    Given the user is on the sentence practice screen
    When the user taps "back_from_sentence_practice"
    Then navigation returns to the previous screen and "recordAnswer" is not called

  @TC-027
  Scenario: SQL injection and special characters in sentence data are handled safely
    Given a sentence with "'; DROP TABLE sentences;--" in "sourceSentence" and ["SELECT*", "1=1"] in "tokensJson"
    When the sentence loads
    Then "source_sentence_text" renders the literal string and "sentences" table remains intact

  @TC-028
  Scenario: Duplicate identical tokens move individually by index
    Given "tile_pool" contains two identical "и" tokens
    When the user taps the first "pool_chip_и"
    Then one "placed_chip_и" appears in "assembly_bay" and one "pool_chip_и" remains in "tile_pool"
```

## 3. Risk Register

| Test Case ID | Impact | Probability Signals Present | Unassessable Signals | Probability | Priority |
|---|---|---|---|---|---|
| `TC-001` | 3 (core workflow) | `known-fragile pattern` (Unicode encoding) [1] | — | Low | `P2` |
| `TC-002` | 3 (core workflow) | `known-fragile pattern` (Unicode encoding) [1] | — | Low | `P2` |
| `TC-003` | 4 (data mutation) | None (single boundary) [0] | — | Low | `P1` |
| `TC-004` | 4 (data mutation) | None (single boundary) [0] | — | Low | `P1` |
| `TC-005` | 4 (data mutation) | None (single boundary) [0] | — | Low | `P1` |
| `TC-006` | 4 (data mutation) | None (single boundary) [0] | — | Low | `P1` |
| `TC-007` | 4 (data mutation) | None (single boundary) [0] | — | Low | `P1` |
| `TC-008` | 4 (data mutation) | None (single boundary) [0] | — | Low | `P1` |
| `TC-009` | 3 (core workflow) | None [0] | — | Low | `P2` |
| `TC-010` | 4 (data mutation) | `state-machine depth` (`S1` via 3 transitions) [1] | — | Low | `P1` |
| `TC-011` | 4 (prevents invalid mutation) | `boolean-logic complexity` (`R1` 3 conds), `state-machine depth` (illegal `T12`) [2] | — | Medium | `P0` |
| `TC-012` | 4 (data mutation) | `boolean-logic complexity` (`R1` 3 conds), `known-fragile pattern` (Cyrillic Unicode) [2] | — | Medium | `P0` |
| `TC-013` | 4 (data mutation) | `boolean-logic complexity` (`R1` 3 conds), `known-fragile pattern` (Cyrillic Unicode) [2] | — | Medium | `P0` |
| `TC-014` | 4 (data mutation) | `boolean-logic complexity` (`R1` 3 conds), `known-fragile pattern` (Latin diacritics) [2] | — | Medium | `P0` |
| `TC-015` | 4 (data mutation) | `boolean-logic complexity` (`R1` 3 conds), `known-fragile pattern` (Latin diacritics) [2] | — | Medium | `P0` |
| `TC-016` | 3 (core workflow) | None [0] | — | Low | `P2` |
| `TC-017` | 3 (core workflow) | `state-machine depth` (`S5` + exit >= 3 transitions) [1] | — | Low | `P2` |
| `TC-018` | 2 (secondary flow) | None [0] | — | Low | `P2` |
| `TC-019` | 3 (prevents corrupt UI state) | `state-machine depth` (illegal `T13`) [1] | — | Low | `P2` |
| `TC-020` | 3 (core workflow) | `known-fragile pattern` (Cyrillic encoding) [1] | — | Low | `P2` |
| `TC-021` | 3 (core workflow) | `known-fragile pattern` (Cyrillic encoding) [1] | — | Low | `P2` |
| `TC-022` | 3 (core workflow) | `known-fragile pattern` (Latin/Cyrillic encoding) [1] | — | Low | `P2` |
| `TC-023` | 3 (core workflow) | `known-fragile pattern` (Latin diacritics) [1] | — | Low | `P2` |
| `TC-024` | 3 (core workflow) | `known-fragile pattern` (Latin diacritics) [1] | — | Low | `P2` |
| `TC-025` | 2 (secondary flow) | None [0] | — | Low | `P2` |
| `TC-026` | 2 (secondary flow) | None [0] | — | Low | `P2` |
| `TC-027` | 5 (data corruption) | `known-fragile pattern` (special/Unicode encoding) [1] | — | Low | `P0` |
| `TC-028` | 3 (core workflow) | None [0] | — | Low | `P2` |

```qa-summary
schema_version=1
gate_status=approved
cases_total=28
cases_p0=6
cases_p1=7
cases_p2=15
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
transitions_illegal_tested=2
pairs_covered=30
pairs_missing=0
usecase_paths_tested=7
```
