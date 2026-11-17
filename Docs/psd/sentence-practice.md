# Sentence Construction Practice — Product Specification

> Token-pool state machine in `SentencePracticeScreen` backed by `SentenceEntity` JSON token lists and `CurriculumRepository` mastery recording.

Last updated: 2026-11-17

## Overview

Implements `Docs/prd/sentence-practice.md` inside `SentencePracticeScreen` using Compose state lists (`placedTokens`, `poolTokens`), Moshi JSON deserialization of token arrays, and Unicode regex token normalization.

## Architecture

- **UI State:** `SentencePracticeScreen` observes `viewModel.activeSentences` and `viewModel.activeLesson`.
- **Token Normalization:** `cleanToken(raw)` applies `raw.trim().lowercase().replace(Regex("[\\p{P}\\p{S}]"), "")`.
- **Persistence:** `SentenceDao` supplies `SentenceEntity` rows for the active lesson; `CurriculumRepository.recordAnswer` records each check result.

## Data flow

1. When entering `SentencePracticeScreen`, `rawSentences` for the active lesson are shuffled into `sentenceQueue`.
2. `setupCurrentSentence` deserializes `tokensJson` and `distractorTokensJson` via Moshi, normalizes every token with `cleanToken`, and populates `poolTokens` with the shuffled union.
3. Tapping a chip in `poolTokens` moves it to `placedTokens` (and speaks the token via `viewModel.playAudio`); tapping a chip in `placedTokens` returns it to `poolTokens`.
4. Tapping Check compares `placedTokens` to the cleaned target token list, updates `correctCount` or `wrongCount`, triggers `viewModel.recordLessonAnswer(isCorrect)`, and plays audio for the full target sentence.

## Components

| Component | File | Responsibility |
|---|---|---|
| `SentencePracticeScreen` | `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` | Token assembly bay, shuffled token pool, answer checking, help dialog, and session summary. |
| `SentenceDao` | `app/src/main/java/com/example/data/local/SentenceDao.kt` | Queries and inserts `SentenceEntity` items per lesson. |
| `LanguageLearnerViewModel` | `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` | Streams `activeSentences` and records lesson answers. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| Malformed `tokensJson` in `SentenceEntity` | Falls back to `sentence.targetSentence.split(" ")` without crashing. |
| Lesson has no sentences | Displays an empty-state message with a button to return to the lesson. |
| Punctuation or quotes attached to tokens (e.g. `„Књигу“!`) | `cleanToken` strips all Unicode punctuation/symbols so comparison is purely lexical. |

## Dependencies

- Jetpack Compose `FlowRow` (`ExperimentalLayoutApi`) for wrapping token chips.
- Moshi for deserializing `tokensJson` and `distractorTokensJson`.

## Cross-references

- `Docs/prd/sentence-practice.md` — the requirements this specification implements.
- `Docs/tests/sentence-practice.md` — the test cases that verify this design.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` — screen implementation and `cleanToken`.
- `app/src/main/java/com/example/data/local/SentenceDao.kt` — sentence persistence queries.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `activeSentences` and `recordLessonAnswer`.
- `Docs/prd/sentence-practice.md` — requirements this specification implements.
- `Docs/tests/sentence-practice.md` — test cases.
