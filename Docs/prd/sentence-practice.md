# Sentence Construction Practice — Product Requirements

> Interactive word-token sentence assembly drills with inner-case normalization, 1–2 distractors, grammar-rule error linking, matrixCoordinate tracking, and implicit FSRS v5 latency/return rating.

Last updated: 2026-11-16

## Overview

`app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` trains active sentence production by having learners assemble target-language sentences from randomized word pools (`wordTokens` + `1`–`2` `distractorTokens`) inside an Assembly Bay, while recording `matrixCoordinate` accuracy and assembly latency for FSRS v5 and 3x3 matrix mastery.

## Goals / Non-goals

**Goals**

- Normalize all pool and distractor tokens to lowercase without punctuation ("inner-case") so capitalization or punctuation never gives away word order.
- Enforce sequential Assembly Bay tile placement/removal, lock UI state on Check Answer, highlight misplaced tokens, reveal the target structure, and link directly to the associated grammar rule on error.
- Track `matrixCoordinate` (`tense`, `form`, `personGroup`), assembly latency, tile returns, and grammar-drawer opens for FSRS v5 rating and targeted weak-cell remediation.

**Non-goals**

- Free-form keyboard typing validation.

## User stories

- As a learner, I want to tap word tiles from the pool into the Assembly Bay and tap placed tiles to return them before checking my answer.
- As a learner, when I submit an incorrect sequence, I want misplaced tokens highlighted, the target structure revealed, and a direct link to the relevant grammar rule.
- As a learner, I want my speed and accuracy per `(tense, form)` coordinate tracked automatically without manual grading buttons.

## Functional requirements

- **FR-1 (Schema & Token Normalization):** Each `SentencePracticeItem` (`SentenceEntity`) carries `id`, `lessonId`, `matrixCoordinate` (`tense`: `PAST|PRESENT|FUTURE`, `form`: `AFFIRMATIVE|NEGATIVE|INTERROGATIVE`, `personGroup`: `1SG|2SG_PL|3SG`), `sourceSentence`, `targetSentence`, `wordTokens`, `1`–`2` `distractorTokens`, and `explanation` (`grammarHint`). Strip Unicode punctuation/symbols (`[\p{P}\p{S}]`) and lowercase all tokens before shuffling into the Tile Pool.
- **FR-2 (Assembly Bay & Tile Pool Workflow):**
  - Tapping an available tile in the Tile Pool moves it to the next open position in the Assembly Bay.
  - Tapping a tile in the Assembly Bay returns it to the Tile Pool and increments the attempt's `tileReturns` counter.
- **FR-3 (Evaluation & Error Handling):**
  - Pressing `Check Answer` locks the UI state for evaluation and validates the assembled token array against the expected target sequence.
  - On correct submission: display Success Emerald (`#10B981`) feedback, pronounce the target sentence, update `matrixStats` and lesson progress, and advance.
  - On incorrect submission: display Error Coral (`#EF4444`) feedback, highlight misplaced tokens, reveal the target structure, provide a direct link/drawer to the associated grammar rule (`explanation` / 3x3 matrix), trigger haptic feedback, and append the item to the tail of the session queue.
- **FR-4 (Implicit FSRS v5 Rating Signals):**
  - `1 — AGAIN`: Incorrect tile sequence submitted OR Grammar Guide drawer opened mid-attempt.
  - `2 — HARD`: Correct on first attempt with assembly time `> 15.0s` OR `> 2` tile returns from Assembly Bay.
  - `3 — GOOD`: Correct sequence assembled within `6.0s–15.0s` with `<= 2` tile returns.
  - `4 — EASY`: Rapid linear assembly (`<= 6.0s`) with `0` tile returns.
- **FR-5 (Targeted Remediation Mode):** Support launching a focused `6`–`8` item session filtered strictly by `WEAK` `matrixCoordinate` keys (e.g., `PAST_NEGATIVE`, `FUTURE_INTERROGATIVE`).

## Non-functional requirements

- **Offline-first:** All sentence prompts, `matrixCoordinate` metadata, tokens, distractors, and hints are stored locally in Room (`sentences` table).
- **Ergonomics:** Word tiles and action buttons maintain minimum `48dp x 48dp` touch targets.

## Acceptance criteria

Defined in `Docs/psd/sentence-practice.md` and verified in `Docs/tests/sentence-practice.md`.

## Out of scope

- Speech-to-text microphone input (deferred to Phase 3).

## Cross-references

- `Docs/psd/sentence-practice.md` — the specification that implements these requirements.
- `Docs/tests/sentence-practice.md` — the test cases that verify them.
- `Docs/prd/spaced-repetition.md` — FSRS v5 implicit rating and first-attempt guard.
- `Docs/prd/grammar-matrices.md` — 3x3 matrix rules linked on errors.
- [[ui-layer]] — where the Compose screens are explained.

## Sources

- `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` — sentence assembly UI, token cleaning, and validation.
- `app/src/main/java/com/example/data/local/SentenceDao.kt` — Room DAO for lesson sentences.
- `app/src/main/java/com/example/data/model/Entities.kt` — `SentenceEntity` schema.
- `Docs/psd/sentence-practice.md` — product specification.
- `Docs/tests/sentence-practice.md` — test cases.
