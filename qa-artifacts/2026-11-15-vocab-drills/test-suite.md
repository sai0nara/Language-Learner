# Test Suite — `vocab-drills`

**Scope:** `VocabDrillScreen` & `CurriculumRepository.recordAnswer`  
**Total Cases:** `19` (`P0`: `2`, `P1`: `4`, `P2`: `13`)

---

## 1. Test Case Table

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | Empty lesson renders completion view immediately | EP, BVA, State Transition, Use Case | Lesson exists in Room with 0 vocab items; `VocabDrillScreen` opened | `rawVocabList = emptyList()` | UI: Completion view shown immediately with `"0.0% (0 / 0)"` and `restart_drill_button`; `target_word_prompt` absent. Audio: `playAudio` not called. State: `initialTotal = 0`, `currentItem = null`. | `P2` | `P1, P4, S1, S5, T1, U1` |
| `TC-002` | Single-item happy path — Cyrillic auto-audio, correct choice, 40ms haptic, 500ms completion | EP, BVA, Decision Table, State Transition, Use Case | Lesson has 1 item; `isLatinScript = false`; `API >= 26` | `rawVocabList = [item1]` (`targetWord="здраво"`, `sourceTranslation="привет"`, `distractorsJson="[\"пока\"]"`) | UI: `target_word_prompt` shows `"здраво"`; tapping `option_card_привет` turns card `SuccessEmerald`, updates progress `"100.0% (1 / 1)"`, after 500ms shows completion view. Audio: `playAudio("здраво", audioUri, isTargetLanguage=true)` on load; NO Russian audio on correct. Haptic: 40ms one-shot. DB/State: `recordAnswer(lessonId, true)` called once; `enrollInSrs` NOT called. | `P0` | `P1, P4, R1, S2, S3, S5, T3, T6, U1` |
| `TC-003` | Multi-item happy path — shuffled queue and incremental progress | EP, BVA, State Transition, Use Case | Lesson has 2 items; `isLatinScript = false`; `API >= 26` | `rawVocabList = [item1, item2]` | UI: `drillQueue.size = 2`, `initialTotal = 2`; after 1st correct choice + 500ms, advances to 2nd item with `"50.0% (1 / 2)"`; after 2nd correct choice + 500ms, shows completion view `"100.0% (2 / 2)"`. DB/State: `recordAnswer(lessonId, true)` called twice; `currentIndex` advances `0 -> 1`. | `P1` | `P1, P4, S2, S3, S5, T5, U1` |
| `TC-004` | Incorrect choice — 120ms haptic, re-queue to end, 800ms advance, eventual mastery | Decision Table, State Transition, Use Case | Lesson has 2 items `[A, B]`; `API >= 26`; `A` is current item | Tap distractor `option_card_пока` on `A`, then answer `B` correctly, then re-queued `A` correctly | UI: `option_card_пока` animates `ErrorCoral`, `option_card_привет` animates `SuccessEmerald(0.8f)`; progress remains `"0.0% (0 / 2)"`; after 800ms advances to `B`, then re-presents `A`. Haptic: 120ms one-shot on incorrect. DB/State: `recordAnswer(lessonId, false)` called once; `drillQueue` becomes `[A, B, A]`; `correctCount` stays `0` until `A` answered correctly. | `P1` | `R1, S2, S4, T4, T7, U1` |
| `TC-005` | Correct and incorrect choices on API < 26 skip Vibrator | Decision Table | Lesson has 2 items; `Build.VERSION.SDK_INT < 26` | Tap incorrect option on item 1, then correct option on item 2 | UI: Feedback colors, 800ms/500ms delays, and queue re-append behave identically to API 26+. Haptic: `Vibrator.vibrate` is NOT invoked. DB/State: `recordAnswer(lessonId, false)` and `recordAnswer(lessonId, true)` recorded. | `P1` | `R1` |
| `TC-006` | Option taps ignored while `selectedOption != null` (T9, T10) | Decision Table, State Transition, Error Guessing | Lesson has 2 items; user taps an option entering `CorrectFeedback` (500ms) or `IncorrectFeedback` (800ms) | Rapid double-tap on `option_card_привет` or another option during delay window | UI: Second tap ignored; no extra color change. Haptic: Vibrator invoked only once. DB/State: `recordAnswer` called only once; `correctCount` incremented at most once; `drillQueue` not double-appended. | `P0` | `R1, S3, S4, T9, T10` |
| `TC-007` | Replay target audio via `vocab_pronounce_button` with Latin script | State Transition, Use Case | `isLatinScript = true`; `S2 (AwaitingAnswer)` | `item1` (`targetWordLatin="zdravo"`, `audioUri="file://a.mp3"`); tap `vocab_pronounce_button` | UI: `target_word_prompt` displays `"zdravo"`; remains in `S2`. Audio: `playAudio("zdravo", "file://a.mp3", isTargetLanguage=true)` called on load and again on button tap. | `P2` | `S2, T2, U1` |
| `TC-008` | Restart drill from Completed state on non-empty vs empty lesson (T8, T11) | State Transition | `S5 (Completed)` reached on (a) 2-item lesson and (b) 0-item lesson | Tap `restart_drill_button` | UI/State: (a) Non-empty: `drillQueue` cleared and repopulated with 2 shuffled items, `currentIndex=0`, `correctCount=0`, `isCompleted=false`, returns to `S2`. (b) Empty: `drillQueue` stays empty, remains in `S5` (`"0.0% (0 / 0)"`). | `P2` | `S5, T8, T11` |
| `TC-009` | Zero distractors (`"[]"`) renders 1 option card with odd-row `Spacer` | EP, BVA, Decision Table | Lesson has 1 item | `distractorsJson = "[]"` | UI: Only `option_card_<sourceTranslation>` rendered (1 card total); final row uses `Spacer(Modifier.weight(1f))`; no fallback to sibling vocab or defaults. | `P2` | `P2, R2` |
| `TC-010` | 4 and 5 distractors render 5 options (odd + Spacer) and 6 options (even, 3 full rows) | EP, BVA, Decision Table | Lesson has 1 item | Case A: 4 distractors; Case B: 5 distractors | UI: Case A renders 5 option cards (2+2+1+Spacer); Case B renders 6 option cards (2+2+2, no trailing Spacer). | `P2` | `P2, R2` |
| `TC-011` | 6 distractors (`>5`) truncated to 5 distractors (6 options max) | EP, BVA | Lesson has 1 item | `distractorsJson` with 6 distinct strings `["d1","d2","d3","d4","d5","d6"]` | UI: Exactly 6 option cards rendered (`sourceTranslation` + `distractors.take(5)`); 7th option never rendered. | `P2` | `P2` |
| `TC-012` | Malformed `distractorsJson` falls back to `emptyList()` without crash | EP, Use Case, Error Guessing | Lesson has 1 item | `distractorsJson = "{bad"` and `"null"` | UI: Exception caught; renders single `option_card_<sourceTranslation>` without crashing. | `P2` | `P2, U1` |
| `TC-013` | Duplicate distractor matching `sourceTranslation` rendered without `.distinct()` | Error Guessing | Lesson has 1 item (`sourceTranslation="привет"`) | `distractorsJson = "[\"привет\",\"пока\"]"` | UI: 3 option cards rendered (`"привет"` appears twice, `"пока"` once) because `.distinct()` is absent. | `P2` | `P2` |
| `TC-014` | Optional metadata (`partOfSpeech`, `exampleSentence`) blank vs non-blank with multi-byte Cyrillic | EP, Error Guessing | Lesson has 2 items | Item A: `partOfSpeech="   "`, `exampleSentence=""`; Item B: `partOfSpeech="сущ."`, `exampleSentence="Ђак чита књигу."` | UI: Item A hides `partOfSpeech` and `exampleSentence`; Item B renders `"сущ."` and `"„Ђак чита књигу.“"` with intact Serbian/Russian Unicode. | `P2` | `P3` |
| `TC-015` | Progress `coerceIn(0f, 1f)` clamps when `correctCount > initialTotal` | BVA | State initialized with `initialTotal = 2` | `correctCount = 3` | UI: Progress bar fraction clamped to `1.0f` (`"100.0% (3 / 2)"`), no crash or `>1f` indicator overflow. | `P2` | `P4` |
| `TC-016` | `CurriculumRepository.recordAnswer` mastery threshold (`89`, `90`, `91`) and 100-row pruning | EP, BVA | Room `AppDatabase` with lesson `L1` (`orderIndex=0`) and `L2` (`orderIndex=1`, locked) | 100 answers with `89`, `90`, and `91` correct; plus 101st answer | DB: `89` → `score=4.45f`, `L1.isCompleted=false`, `L2` locked; `90` → `score=4.50f`, `L1.isCompleted=true`, `L2` unlocked; `91` → `score=4.55f`, `L2` unlocked; `lesson_answers` pruned to 100 rows. | `P1` | `P5` |
| `TC-017` | Pairwise Configs 1 & 2 (`distractorCount=0`) | Pairwise | 1-item lesson | Config 1: `(0, Latin=true, API>=26, meta=present)`; Config 2: `(0, Latin=false, API<26, meta=blank)` | UI/Audio/Haptic: Config 1 shows `targetWordLatin`, metadata, 1 option, 40ms haptic on correct; Config 2 shows `targetWord`, no metadata, 1 option, no haptic. | `P2` | `C1` |
| `TC-018` | Pairwise Configs 3 & 4 (`distractorCount=3`) | Pairwise | 1-item lesson | Config 3: `(3, Latin=true, API<26, meta=blank)`; Config 4: `(3, Latin=false, API>=26, meta=present)` | UI/Audio/Haptic: Both render 4 options (2+2 grid); Config 3 uses Latin + no metadata + no haptic; Config 4 uses Cyrillic + metadata + 40ms/120ms haptic. | `P2` | `C1` |
| `TC-019` | Pairwise Configs 5 & 6 (`distractorCount=6`) | Pairwise | 1-item lesson | Config 5: `(6, Latin=true, API>=26, meta=blank)`; Config 6: `(6, Latin=false, API<26, meta=present)` | UI/Audio/Haptic: Both truncate to 6 options (2+2+2 grid); Config 5 uses Latin + no metadata + haptic; Config 6 uses Cyrillic + metadata + no haptic. | `P2` | `C1` |

---

## 2. Gherkin Scenarios (Behavioral Cases)

```gherkin
Feature: Vocabulary Matching Drills

  @TC-001
  Scenario: Empty lesson renders completion view immediately
    Given a lesson exists with 0 vocabulary items
    When the learner opens VocabDrillScreen
    Then the completion view is displayed with progress "0.0% (0 / 0)"
    And the target_word_prompt is not displayed
    And playAudio is not invoked

  @TC-002
  Scenario: Single-item happy path completes drill without source audio or SRS enrollment
    Given a lesson with 1 vocabulary item "здраво" -> "привет" and 1 distractor "пока"
    And isLatinScript is false on Android API 26 or higher
    When the learner opens VocabDrillScreen
    Then target_word_prompt displays "здраво"
    And playAudio is called with "здраво" and isTargetLanguage true
    When the learner taps option_card_привет
    Then a 40ms haptic vibration is triggered
    And recordAnswer is called once with isCorrect true
    And enrollInSrs is not called
    And after 500ms the completion view is displayed with "100.0% (1 / 1)"

  @TC-003
  Scenario: Multi-item happy path advances queue and updates progress
    Given a lesson with 2 vocabulary items in Cyrillic mode
    When the learner answers the first item correctly and waits 500ms
    Then progress displays "50.0% (1 / 2)" and the second item is shown
    When the learner answers the second item correctly and waits 500ms
    Then the completion view displays "100.0% (2 / 2)"

  @TC-004
  Scenario: Incorrect choice re-queues missed item to end of drillQueue
    Given a lesson with 2 vocabulary items A and B
    When the learner taps an incorrect option on item A
    Then a 120ms haptic vibration is triggered
    And recordAnswer is called with isCorrect false
    And item A is re-appended to the end of drillQueue
    When 800ms elapses, item B is answered correctly, and re-queued item A is answered correctly
    Then the completion view is displayed with "100.0% (2 / 2)"

  @TC-006
  Scenario: Option taps are ignored while feedback delay is active
    Given the learner has just tapped an option card and selectedOption is non-null
    When the learner taps another option card before the 500ms or 800ms delay completes
    Then the second tap is ignored
    And recordAnswer and Vibrator are not invoked a second time

  @TC-007
  Scenario: Speaker button replays target pronunciation in Latin script
    Given isLatinScript is true and the current item has targetWordLatin "zdravo"
    When the learner taps vocab_pronounce_button
    Then target_word_prompt displays "zdravo"
    And playAudio is called with "zdravo" and isTargetLanguage true

  @TC-008
  Scenario: Restart drill resets non-empty lesson and keeps empty lesson completed
    Given the drill is in the Completed state
    When the learner taps restart_drill_button on a non-empty lesson
    Then drillQueue is reshuffled and currentIndex and correctCount reset to 0
    When the learner taps restart_drill_button on an empty lesson
    Then the screen remains in the Completed state with "0.0% (0 / 0)"

  @TC-012
  Scenario: Malformed distractorsJson falls back to empty list
    Given a vocabulary item with distractorsJson "{bad"
    When the learner opens VocabDrillScreen
    Then only the sourceTranslation option card is rendered without crashing
```

---

## 3. Risk Register

| Test Case ID | Impact | Reason for Impact | Probability Signals Present | Unassessable Signals | Probability | Priority |
|---|---|---|---|---|---|---|
| `TC-001` | 3 | Core workflow blocked if empty crashes | `boundary density` (`size=0`, `initialTotal=0`) [1] | None | Low | `P2` |
| `TC-002` | 4 | Data mutation (`recordAnswer` in Room) | `boolean-logic complexity` (`R1`), `boundary density` (`size=1`, `1/1`) [2] | None | Medium | `P0` |
| `TC-003` | 4 | Data mutation (`recordAnswer` in Room) | `boundary density` (`size=2`, `1/2 -> 2/2`) [1] | None | Low | `P1` |
| `TC-004` | 4 | Data mutation (`recordAnswer` + queue) | `boolean-logic complexity` (`R1`) [1] | None | Low | `P1` |
| `TC-005` | 4 | Data mutation (`recordAnswer` in Room) | `boolean-logic complexity` (`R1`) [1] | None | Low | `P1` |
| `TC-006` | 4 | Data mutation (guards duplicate DB writes) | `boolean-logic complexity` (`R1`), `state-machine depth` (`T9`, `T10`), `known-fragile pattern` (timing/concurrency) [3] | None | Medium | `P0` |
| `TC-007` | 2 | Secondary flow (audio replay) | None [0] | None | Low | `P2` |
| `TC-008` | 3 | Core workflow reset | `state-machine depth` (`T11` illegal) [1] | None | Low | `P2` |
| `TC-009` | 3 | Core workflow (option rendering) | `boundary density` (`0` distractors, `1` option) [1] | None | Low | `P2` |
| `TC-010` | 3 | Core workflow (option rendering) | `boundary density` (`4`, `5` distractors) [1] | None | Low | `P2` |
| `TC-011` | 3 | Core workflow (option cap) | `boundary density` (`5/6` distractors) [1] | None | Low | `P2` |
| `TC-012` | 3 | Core workflow (JSON parse safety) | None [0] | None | Low | `P2` |
| `TC-013` | 2 | Degraded UI (duplicate option) | None [0] | None | Low | `P2` |
| `TC-014` | 1 | Display-only metadata | `known-fragile pattern` (Unicode) [1] | None | Low | `P2` |
| `TC-015` | 1 | Display-only progress clamp | `boundary density` (`correctCount > initialTotal`) [1] | None | Low | `P2` |
| `TC-016` | 4 | Data mutation (lesson mastery & unlock) | `boundary density` (`89`, `90`, `91`) [1] | None | Low | `P1` |
| `TC-017` | 3 | Core workflow (config matrix) | None [0] | None | Low | `P2` |
| `TC-018` | 3 | Core workflow (config matrix) | None [0] | None | Low | `P2` |
| `TC-019` | 3 | Core workflow (config matrix) | None [0] | None | Low | `P2` |

```qa-summary
schema_version=1
gate_status=approved
cases_total=19
cases_p0=2
cases_p1=4
cases_p2=13
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
```
