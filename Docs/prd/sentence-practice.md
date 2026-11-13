# Sentence Construction Practice — Product Requirements

> Interactive word-token sentence assembly drills with inner-case normalization, distractor tokens, contextual grammar hints, and dual-audio feedback.

Last updated: 2026-11-13

## Overview

`app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` trains active sentence production. Learners translate a source prompt (Russian) into the target language (Serbian Cyrillic or Latin) by tapping lowercase, punctuation-stripped word tokens from a shuffled pool containing both correct words and distractors.

## Goals / Non-goals

**Goals**

- Normalize all pool and distractor tokens to lowercase without punctuation ("inner-case") so capitalization or punctuation never gives away word order.
- Provide immediate visual, haptic, and audio feedback when a sentence is checked.
- Offer an on-demand help dialog displaying the grammar hint and target sentence when the learner is stuck.
- Feed every attempt into the lesson's rolling 100-answer mastery score.

**Non-goals**

- Free-form keyboard typing validation.

## User stories

- As a learner, I want all candidate word tokens shown in lowercase without punctuation so that I must rely on grammar rules rather than capital letters or periods to find the first and last words.
- As a learner, I want to tap placed tokens to return them to the pool before checking my answer.
- As a learner, I want to hear the target sentence spoken aloud and see my lesson grade update as I practice.

## Functional requirements

- FR-1: Strip Unicode punctuation/symbols (`[\p{P}\p{S}]`) and lowercase all target and distractor tokens before shuffling them into the token pool.
- FR-2: Allow moving tokens between the available pool and the assembly bay by tapping.
- FR-3: Validate the assembled token sequence against the cleaned target token sequence, record the result via `viewModel.recordLessonAnswer`, and pronounce the sentence on check.
- FR-4: Provide a help dialog (`showHelpDialog`) with the sentence's `grammarHint` and full target sentence.

## Non-functional requirements

- **Offline-first:** All sentence prompts, tokens, distractors, and hints are stored locally in Room (`sentences` table).

## Acceptance criteria

Defined in `Docs/psd/sentence-practice.md` and verified in `Docs/tests/sentence-practice.md`.

## Out of scope

- Speech-to-text microphone input.

## Cross-references

- `Docs/psd/sentence-practice.md` — the specification that implements these requirements.
- `Docs/tests/sentence-practice.md` — the test cases that verify them.
- [[ui-layer]] — where the Compose screens are explained.

## Sources

- `app/src/main/java/com/example/ui/screens/SentencePracticeScreen.kt` — sentence assembly UI, token cleaning, and validation.
- `app/src/main/java/com/example/data/local/SentenceDao.kt` — Room DAO for lesson sentences.
- `app/src/main/java/com/example/data/model/Entities.kt` — `SentenceEntity` schema.
- `Docs/psd/sentence-practice.md` — product specification.
- `Docs/tests/sentence-practice.md` — test cases.
