# Vocabulary Matching Drills — Product Requirements

> Multiple-choice 2x2 lexical drills (30–50 core words per lesson) with 500ms auto-advance, shake/color feedback, missed-item re-queuing, and implicit FSRS v5 latency rating.

Last updated: 2026-11-16

## Overview

`app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt` establishes a lexical base of `30` to `50` core words per lesson before sentence construction. Each card displays a source/target prompt at top-center, a `2x2` grid of `4` distinct translation options, and a top progress bar (`completed / queue`), feeding accuracy and response latency into FSRS v5.

## Goals / Non-goals

**Goals**

- Present shuffled `VocabularyItem` cards (`30`–`50` core words per lesson) with a `2x2` grid of `4` distinct translation options.
- Deliver immediate visual, haptic, and audio feedback on selection without manual SRS grading buttons.
- Re-append missed items to the end of the active session queue while guarding FSRS updates to the first attempt only.

**Non-goals**

- Free-text keyboard spelling tests or image-based flashcards.

## User stories

- As a learner, I want immediate color, haptic, and audio feedback when I tap one of the 4 translation cards so that correct associations are reinforced rapidly.
- As a learner, I want missed vocabulary terms appended to the end of the session queue so that I complete the drill only after answering every word correctly.

## Functional requirements

- **FR-1 (Queue & Interface Layout):**
  - Load `VocabularyItem` records (`id`, `lessonId`, `sourceWord`, `targetWord`, `partOfSpeech`, `distractorsList`) into the active `drillQueue`.
  - Render a top progress bar (`completed / total`), prominent target/source term at top-center, and a `2x2` grid of `4` distinct translation options (`sourceTranslation` + `3` distractors).
- **FR-2 (Correct Selection Feedback):**
  - Highlight the selected option card in Success Emerald (`#10B981`).
  - Trigger a success haptic pulse and affirmative audio cue.
  - Automatically transition to the next term after a `500ms` delay.
- **FR-3 (Incorrect Selection Feedback & Re-queueing):**
  - Highlight the selected incorrect option in Error Coral (`#EF4444`) with a lateral shake animation, and immediately highlight the correct card in Success Emerald (`#10B981`).
  - Append the missed item to the end of the active session queue for immediate re-testing.
- **FR-4 (Implicit FSRS v5 Latency Mapping & First-Attempt Guard):**
  - Derive FSRS v5 rating (`1–4`) from first-attempt accuracy and response latency:
    - `1 — AGAIN`: Incorrect card selected on the first attempt in the session.
    - `2 — HARD`: Correct on first attempt with response latency `> 6.0s`.
    - `3 — GOOD`: Correct on first attempt within `2.5s–6.0s`.
    - `4 — EASY`: Instantaneous correct selection (`<= 2.5s`).
  - Update FSRS parameters (`stability`, `difficulty`, `dueAt`) strictly from the first attempt in the session so same-session re-tests do not inflate intervals.

## Non-functional requirements

- **Offline-first:** All `VocabularyItem` records and distractors load from local storage (`vocab_items`).
- **Ergonomics:** All 4 option cards in the `2x2` grid maintain at least `48dp x 48dp` touch targets.

## Acceptance criteria

Defined in `Docs/psd/vocab-drills.md` and verified in `Docs/tests/vocab-drills.md`.

## Out of scope

- Image-based flashcards or free-text typing.

## Cross-references

- `Docs/psd/vocab-drills.md` — the specification that implements these requirements.
- `Docs/tests/vocab-drills.md` — the test cases that verify them.
- `Docs/prd/spaced-repetition.md` — FSRS v5 scheduling and first-attempt scoring guard.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt` — vocabulary drill UI and re-queue logic.
- `app/src/main/java/com/example/data/local/VocabDao.kt` — Room DAO for vocabulary items.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `recordAnswer` and `enrollInSrs`.
- `Docs/psd/vocab-drills.md` — product specification.
- `Docs/tests/vocab-drills.md` — test cases.
