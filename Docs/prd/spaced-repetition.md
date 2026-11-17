# Spaced Repetition (FSRS v5) — Product Requirements

> Schedules vocabulary and sentence reviews using FSRS v5 with implicit accuracy/latency rating mapping (1–4), first-attempt scoring guard, and 70/30 daily blended review queues.

Last updated: 2026-11-16

## Overview

`app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` and `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` manage long-term memory scheduling. Rather than forcing manual self-grading buttons (`Again / Hard / Good / Easy`) during drills, the scheduler derives FSRS v5 ratings (`1–4`) implicitly from interaction accuracy, response latency, tile returns, and grammar-drawer usage across Vocabulary and Sentence drills, and surfaces a daily blended review session on the Main Menu.

## Goals / Non-goals

**Goals**

- Schedule items using FSRS v5 (`UserItemMemory` states: `NEW`, `LEARNING`, `REVIEW`, `RELEARNING`) with `stability`, `difficulty` (`1.0–10.0`), `elapsedDays`, `scheduledDays`, `reps`, `lapses`, and `dueAt`.
- Map Vocabulary (`2x2` grid) and Sentence (`Word Tiles`) interaction telemetry automatically to FSRS ratings `1 — AGAIN`, `2 — HARD`, `3 — GOOD`, and `4 — EASY`.
- Enforce the First-Attempt Scoring Guard so same-session re-tests of `AGAIN` items do not inflate long-term stability or intervals.
- Blend overdue reviews (`70%`) with active `IN_PROGRESS` lesson items (`30%`) in a `20`-item `DAILY_REVIEW` session capped at `30` `REVIEW` items/day without ever locking new lessons.

**Non-goals**

- Manual self-grading buttons during active Vocabulary/Sentence drills or cloud-synced multi-device SRS state in Phase 1.

## User stories

- As a learner, I want my review intervals calculated automatically from my speed and accuracy so that I never have to interrupt my flow to self-grade.
- As a learner, I want missed items to repeat at the end of my current session for immediate reinforcement without artificially inflating my FSRS stability.
- As a learner, I want a daily blended review queue that prioritizes items closest to forgetting while keeping my current lesson moving forward, without locking new lessons if I have a backlog.

## Functional requirements

- **FR-1 (Implicit UI-to-FSRS Rating Mapping):** Derive ratings `1–4` automatically:
  - `1 — AGAIN`: Vocab = incorrect card on first attempt; Sentence = incorrect tile sequence OR Grammar Guide drawer opened mid-attempt. Increments `lapses`, resets `stability`, transitions to `RELEARNING`, appends to session tail.
  - `2 — HARD`: Vocab = correct on first attempt with latency `> 6.0s`; Sentence = correct on first attempt with assembly time `> 15.0s` OR `> 2` tile returns. Applies reduced stability growth (`1.2x`) and shorter scheduled interval.
  - `3 — GOOD`: Vocab = correct on first attempt within `2.5s–6.0s`; Sentence = correct sequence within `6.0s–15.0s` with `<= 2` tile returns. Standard FSRS stability and difficulty recalculation.
  - `4 — EASY`: Vocab = instantaneous correct selection (`<= 2.5s`); Sentence = rapid linear assembly (`<= 6.0s`) with `0` tile returns. Elevated stability bonus and decreased difficulty rating.
- **FR-2 (First-Attempt Scoring Guard):** Items rated `AGAIN` (`1`) append to the active session queue tail for immediate reinforcement, but FSRS parameters (`stability`, `difficulty`, `dueAt`) update strictly from the **first attempt** within that session.
- **FR-3 (Daily Blended Review Queue `DAILY_REVIEW`):** When unlocked lessons contain items with `dueAt <= now()`, the Main Menu surfaces a `20`-item review session:
  - Up to `70%` slots (max `14` items): Overdue items sorted by ascending retrievability `R = (1 + (19/81) * (t / S))^-0.5` so items closest to the forgetting threshold appear first.
  - At least `30%` slots (min `6` items): Active items drawn from the learner's current `IN_PROGRESS` lesson.
- **FR-4 (Daily Load Soft Cap & Non-Blocking Backlogs):** Default soft cap of `30` `REVIEW` items per day. Overdue review backlogs never lock access to new unlocked lessons.
- **FR-5 (`UserItemMemory` Persistence):** Persist `id`, `userId`, `lessonId`, `itemType` (`VOCAB|SENTENCE`), `itemId`, `state`, `stability`, `difficulty`, `elapsedDays`, `scheduledDays`, `reps`, `lapses`, `dueAt`, and `lastReviewedAt` locally.

## Non-functional requirements

- **Offline-first:** All FSRS v5 calculations, retrievability sorting, and `UserItemMemory` persistence run locally.

## Acceptance criteria

Defined in `Docs/psd/spaced-repetition.md` and verified in `Docs/tests/spaced-repetition.md`.

## Out of scope

- Server-side sync of `UserItemMemory` across devices.

## Cross-references

- `Docs/psd/spaced-repetition.md` — the specification that implements these requirements.
- `Docs/tests/spaced-repetition.md` — the test cases that verify them.
- `Docs/prd/curriculum-progression.md` — `vocabMastery >= 0.85` (`stability >= 3` days) and `unresolvedLapses == 0` mastery gates.
- [[srs-and-audio]] — SRS and audio module overview.

## Sources

- `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` — FSRS/SRS scheduling and grade mapping.
- `app/src/main/java/com/example/ui/screens/SpacedRepetitionScreen.kt` — review UI.
- `app/src/main/java/com/example/data/local/SrsDao.kt` — due-card queries and updates.
- `Docs/psd/spaced-repetition.md` — product specification.
- `Docs/tests/spaced-repetition.md` — test cases.
