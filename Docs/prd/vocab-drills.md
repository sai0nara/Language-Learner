# Vocabulary Matching Drills — Product Requirements

> Multiple-choice lexical drills with automatic target-word pronunciation, missed-item re-queuing, and SRS enrollment.

Last updated: 2026-11-13

## Overview

`app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt` helps learners acquire new lesson vocabulary before or alongside sentence construction. Each card presents a target word (in Serbian Cyrillic or Latin), plays its pronunciation automatically, and asks the learner to pick the correct Russian translation among distractors.

## Goals / Non-goals

**Goals**

- Present shuffled vocabulary items with up to 4 multiple-choice translation options.
- Automatically pronounce the target word when a card appears, and speak the translation or re-queue the item depending on the learner's choice.
- Re-append missed words to the end of the active drill queue until every word in the lesson is answered correctly.
- Record each answer toward lesson mastery and enroll mastered words into SRS.

**Non-goals**

- Free-text spelling tests.

## User stories

- As a learner, I want missed vocabulary cards to reappear later in the same drill session so that I finish the drill only after mastering every word.
- As a learner, I want to hear the Serbian word automatically when the card loads and tap the speaker icon to replay it at any time.

## Functional requirements

- FR-1: Shuffle the active lesson's vocabulary into `drillQueue` and display progress across the queue.
- FR-2: Build 4 answer choices per card from `sourceTranslation` plus up to 3 distractors (from `distractorsJson` or other lesson vocabulary).
- FR-3: On a correct choice, play the source translation audio, record a correct lesson answer, enroll the item in SRS, and advance.
- FR-4: On an incorrect choice, trigger haptic feedback, record an incorrect lesson answer, and re-append the missed `VocabItemEntity` to the end of `drillQueue`.

## Non-functional requirements

- **Offline-first:** All vocabulary items and distractors are loaded from local Room storage (`vocab_items`).

## Acceptance criteria

Defined in `Docs/psd/vocab-drills.md` and verified in `Docs/tests/vocab-drills.md`.

## Out of scope

- Image-based flashcards.

## Cross-references

- `Docs/psd/vocab-drills.md` — the specification that implements these requirements.
- `Docs/tests/vocab-drills.md` — the test cases that verify them.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt` — vocabulary drill UI and re-queue logic.
- `app/src/main/java/com/example/data/local/VocabDao.kt` — Room DAO for vocabulary items.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `recordAnswer` and `enrollInSrs`.
- `Docs/psd/vocab-drills.md` — product specification.
- `Docs/tests/vocab-drills.md` — test cases.
