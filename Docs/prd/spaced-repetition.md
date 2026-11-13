# Spaced Repetition (SM-2) — Product Requirements

> Schedules vocabulary and sentence review flashcards using the SM-2 spaced repetition algorithm with 4-grade recall buttons (`Again`, `Hard`, `Good`, `Easy`).

Last updated: 2026-11-13

## Overview

`app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` and `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` provide long-term retention reviews. Due cards (`nextReviewTimestamp <= now`) are queued automatically, and learner ratings update each card's repetition count, ease factor, and next review timestamp in Room.

## Goals / Non-goals

**Goals**

- Schedule reviews using the SM-2 algorithm with a minimum ease factor floor (`1.3f`).
- Provide a clean flashcard review flow with prompt, answer reveal, audio replay, and 4 grading buttons (`Again`, `Hard`, `Good`, `Easy`).
- Display a due-card badge count in `CurriculumScreen` and a celebratory empty state when all due reviews are finished.

**Non-goals**

- Cloud-synced SRS decks across multiple devices.

## User stories

- As a learner, I want to see how many SRS cards are due today on the main screen and review them offline.
- As a learner, I want to reveal a flashcard's answer and rate my recall (`Again`, `Hard`, `Good`, `Easy`) so difficult words appear sooner and easy words are scheduled further out.

## Functional requirements

- FR-1: Map `ReviewGrade` choices (`AGAIN`, `HARD`, `GOOD`, `EASY`) to SM-2 quality scores (`1`, `2`, `4`, `5`).
- FR-2: Reset `repetition = 0` and `intervalDays = 1` whenever quality is below `3`; otherwise progress intervals (`1d` -> `6d` -> `round(intervalDays * easeFactor)`).
- FR-3: Clamp `quality` to `0..5` and keep `easeFactor >= 1.3f`.
- FR-4: Persist updated `SrsReviewEntity` records in `srs_reviews` and stream due items and due counts reactively.

## Non-functional requirements

- **Offline-first:** All SM-2 calculations and `srs_reviews` queries execute locally.

## Acceptance criteria

Defined in `Docs/psd/spaced-repetition.md` and verified in `Docs/tests/spaced-repetition.md`.

## Out of scope

- Custom user-configured SM-2 ease multipliers.

## Cross-references

- `Docs/psd/spaced-repetition.md` — the specification that implements these requirements.
- `Docs/tests/spaced-repetition.md` — the test cases that verify them.
- [[srs-and-audio]] — SRS and audio module overview.

## Sources

- `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` — SM-2 calculation and `ReviewGrade` enum.
- `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` — flashcard review UI.
- `app/src/main/java/com/example/data/local/SrsDao.kt` — due-card queries and updates.
- `Docs/psd/spaced-repetition.md` — product specification.
- `Docs/tests/spaced-repetition.md` — test cases.
