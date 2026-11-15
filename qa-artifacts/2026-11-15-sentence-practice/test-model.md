# Test Model — `sentence-practice`

Scope: `SentencePracticeScreen.kt` (token normalization, JSON parsing, answer validation, mastery/progress state, help dialog, and session lifecycle).

## 1. Modeled Elements

### Parameters (`P<n>`)

- **`P1` (`cleanToken(raw: String)`)** — String parameter normalized via `raw.trim().lowercase().replace(Regex("[\\p{P}\\p{S}]"), "")` and filtered by `isNotBlank()` -> **EP, Error Guessing**
  - Valid classes: lowercase alphanumeric word; mixed/uppercase Cyrillic or Latin word; word with leading/trailing whitespace; word containing Unicode punctuation/symbols (`[\p{P}\p{S}]`).
  - Invalid/filtered classes: empty string `""`; whitespace-only `"   "`; punctuation/symbol-only `".,!?—«»"`.
- **`P2` (`LessonEntity.masteryScore`)** — Float `0.0f..5.0f` inclusive (`(correctAnswersInLast100 / 100f) * 5.0f`), completion & next-lesson unlock threshold `>= 4.5f` -> **EP, BVA**
  - Boundaries (`0.1f` display precision via `Locale.US` `"%.1f"`):
    - Lower bound `0.0f`: `-0.1f` (invalid), `0.0f` (valid min), `0.1f` (valid)
    - Completion/unlock threshold `4.5f`: `4.4f` (valid, `isCompleted=false`, next lesson locked), `4.5f` (valid, `isCompleted=true`, next lesson unlocked, unlock banner shown), `4.6f` (valid, `isCompleted=true`, next lesson unlocked)
    - Upper bound `5.0f`: `4.9f` (valid), `5.0f` (valid max), `5.1f` (invalid)
- **`P3` (`rawSentences.size`)** — Int `0..N` -> **EP, BVA**
  - Boundary `0` / `1`: `0` (empty-state summary), `1` (single-sentence drill), `2` (multi-sentence drill).

### Business Rules (`R<n>`)

- **`R1` (Answer Validation & Side Effects)** -> **Decision Table** (`2^3 = 8` full columns -> `5` reduced columns)
  - Conditions:
    - `C1`: `placedTokens.isNotEmpty()` (`T` / `F`)
    - `C2`: `isLatin` (`F` = Cyrillic `targetSentence`, `T` = Latin `targetSentenceLatin`)
    - `C3`: `userTokens == expectedTokens` after `cleanToken` (`T` / `F`)
  - Actions:
    - `A1`: `check_answer_button` enabled
    - `A2`: `validationResult` (`true` / `false` / `null`)
    - `A3`: `feedbackMessage` (`"Правильно!"` / `"Правильный ответ: $currentTarget"` / `""`)
    - `A4`: `viewModel.recordAnswer(lesson.id, isCorrect)` called
    - `A5`: Haptic (`50 ms` correct / `120 ms` wrong if `SDK_INT >= O`) & `viewModel.playAudio` (correct only)
    - `A6`: Queue progression (`delay(1200)` advance on correct; append `currentSentence` to end of `sentenceQueue` on wrong)

| Rule | `C1` (`placedTokens.isNotEmpty()`) | `C2` (`isLatin`) | `C3` (`user == expected`) | `A1` Enabled | `A2` Result | `A3` Feedback | `A4` `recordAnswer` | `A5` Haptic/Audio | `A6` Queue |
|---|---|---|---|---|---|---|---|---|---|
| Col 1 | `F` | `—` | `—` | `F` | `null` | `""` | No | None | No change |
| Col 2 | `T` | `F` (Cyrillic) | `T` | `T` | `true` | `"Правильно!"` | `isCorrect=true` | 50 ms + `playAudio` | Advance after 1200 ms |
| Col 3 | `T` | `F` (Cyrillic) | `F` | `T` | `false` | `"Правильный ответ: $targetSentence"` | `isCorrect=false` | 120 ms only | Append to `sentenceQueue` |
| Col 4 | `T` | `T` (Latin) | `T` | `T` | `true` | `"Правильно!"` | `isCorrect=true` | 50 ms + `playAudio` | Advance after 1200 ms |
| Col 5 | `T` | `T` (Latin) | `F` | `T` | `false` | `"Правильный ответ: $targetSentenceLatin"` | `isCorrect=false` | 120 ms only | Append to `sentenceQueue` |

### States (`S<n>`) & Transitions (`T<n>`) -> **State Transition**

- `S1`: **Empty / Finished Summary** (`rawSentences.isEmpty() || isFinished == true`) — shows `"Упражнение завершено!"`, mastery score, rolling 100-answer count, optional unlock banner (`masteryScore >= 4.5f`), `finish_practice_button`.
- `S2`: **Drilling — Empty Assembly Bay** (`placedTokens.isEmpty()`, `check_answer_button` disabled, `validationResult == null`).
- `S3`: **Drilling — Tokens Placed** (`placedTokens.isNotEmpty()`, `check_answer_button` enabled, `validationResult == null`).
- `S4`: **Validated — Correct** (`validationResult == true`, `feedbackMessage == "Правильно!"`).
- `S5`: **Validated — Incorrect** (`validationResult == false`, `feedbackMessage == "Правильный ответ: $currentTarget"`).
- `S6`: **Help Dialog Open** (`showHelpDialog == true`).

| Transition | From | Event | To | Type |
|---|---|---|---|---|
| `T1` | `S2` | Tap `pool_chip_$token` in `tile_pool` | `S3` | Legal |
| `T2` | `S3` | Tap `pool_chip_$token` or non-last `placed_chip_$token` | `S3` | Legal |
| `T3` | `S3` | Tap last `placed_chip_$token` in `assembly_bay` | `S2` | Legal |
| `T4` | `S3` | Tap `check_answer_button` (`userTokens == expectedTokens`) | `S4` | Legal |
| `T5` | `S4` | `delay(1200)` elapsed (`currentIndex + 1 < sentenceQueue.size`) | `S2` | Legal |
| `T6` | `S4` | `delay(1200)` elapsed (`currentIndex + 1 == sentenceQueue.size`) | `S1` | Legal |
| `T7` | `S3` | Tap `check_answer_button` (`userTokens != expectedTokens`) | `S5` | Legal |
| `T8` | `S5` | Tap `pool_chip_$token` or non-last `placed_chip_$token` | `S3` | Legal |
| `T9` | `S5` | Tap last `placed_chip_$token` | `S2` | Legal |
| `T10` | `S2` / `S3` | Tap `sentence_hint_button` | `S6` | Legal |
| `T11` | `S6` | Tap `"Понятно"` or `onDismissRequest` | `S2` / `S3` | Legal |
| `T12` | `S2` | Attempt tap on `check_answer_button` (`placedTokens.isEmpty()`) | `S4` / `S5` | **Illegal** |
| `T13` | `S1` | Attempt drilling interaction in summary/empty state | `S2` / `S3` | **Illegal** |

### Configuration Matrix (`C1`) -> **Pairwise**

Dimensions (`2 × 2 × 3 × 2 = 24` exhaustive; `30` pairs covered in `6` configurations):
- `scriptMode`: `Cyrillic (false)`, `Latin (true)`
- `tokensJsonState`: `Valid`, `Malformed (fallback to targetSentence.split(" "))`
- `distractorJsonState`: `Non-empty`, `Empty ([])`, `Malformed (fallback to emptyList())`
- `grammarHintState`: `Non-blank`, `Blank (fallback string)`

| Config | `scriptMode` | `tokensJsonState` | `distractorJsonState` | `grammarHintState` |
|---|---|---|---|---|
| 1 | `Cyrillic (false)` | `Valid` | `Non-empty` | `Non-blank` |
| 2 | `Cyrillic (false)` | `Malformed` | `Empty ([])` | `Non-blank` |
| 3 | `Cyrillic (false)` | `Valid` | `Malformed` | `Blank` |
| 4 | `Latin (true)` | `Malformed` | `Non-empty` | `Blank` |
| 5 | `Latin (true)` | `Valid` | `Empty ([])` | `Blank` |
| 6 | `Latin (true)` | `Malformed` | `Malformed` | `Non-blank` |

### Use Cases (`U1`) -> **Use Case**

- **Basic path:** Non-empty `rawSentences` -> place correct tokens -> tap `check_answer_button` -> correct feedback + audio + `recordAnswer(true)` -> auto-advance -> finish queue -> `S1` summary -> `finish_practice_button`.
- **Alt 1:** Tap `placed_chip_$token` to return token to `tile_pool` before checking.
- **Alt 2:** Tap `sentence_hint_button` -> view `AlertDialog` -> dismiss `"Понятно"`.
- **Alt 3:** Tap speaker `IconButton` to play target audio without checking.
- **Alt 4:** Tap `back_from_sentence_practice` to exit early.
- **Exc 1:** Submit incorrect tokens -> wrong feedback + `recordAnswer(false)` + 120 ms vibration + sentence appended to `sentenceQueue` for retry.
- **Exc 2:** Empty `rawSentences` -> immediate `S1` summary/empty-state screen.

### Gaps (`G<n>`)

- **`G1`:** Concurrent / double-tap behavior on `check_answer_button` during the `delay(1200)` auto-advance window is unspecified.
- **`G2`:** Mixed-script fallback when `isLatin == true` and `tokensJson` is malformed (falls back to Cyrillic `targetSentence.split(" ")` while validating against `targetSentenceLatin`).

```qa-summary
schema_version=1
gate_status=approved
elements_total=27
elements_covered=25
elements_gap=2
coverage_pct=100.0
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=8
dt_columns_reduced=5
states_total=6
transitions_legal=11
transitions_illegal=2
configs=24
pairs_target=30
usecase_paths=7
```
