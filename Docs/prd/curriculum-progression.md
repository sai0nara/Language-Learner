# Curriculum Progression & Mastery Unlocking — Product Requirements

> Provides a structured lesson dashboard with rolling 100-answer mastery scoring, automatic lesson unlocking, script toggling (Cyrillic/Latin), and theme switching.

Last updated: 2026-11-13

## Overview

Learners progress through ordered lessons seeded from `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` or imported decks. `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` and `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` present lesson status, mastery scores, script preferences (Serbian Cyrillic vs. Latin), and entry points into drills and grammar guides.

## Goals / Non-goals

**Goals**

- Seed an offline Serbian-for-Russian-speakers B1 curriculum on first launch.
- Track rolling accuracy over the last 100 answers per lesson and calculate a 0.0–5.0 mastery grade.
- Unlock the next sequential lesson automatically once the learner reaches a mastery score of 4.5 or higher (90/100 correct).
- Let learners toggle between Cyrillic and Latin script and between dark and light Material 3 themes at any time.

**Non-goals**

- Server-side leaderboards or remote user profile synchronization.

## User stories

- As a learner, I want Lesson 1 unlocked out of the box and subsequent lessons unlocked as I master each topic (`>= 4.5` grade) so that I follow a clear progression.
- As a learner of Serbian, I want to toggle between Cyrillic and Latin script across the app so that I can practice reading both alphabets.
- As a learner, I want to open a lesson's detail screen and choose between sentence construction, vocabulary drills, and the 3x3 grammar matrix.

## Functional requirements

- FR-1: Seed initial lessons, vocabulary, grammar guides, sentences, and SRS items idempotently when the database is empty.
- FR-2: Record each drill/practice answer in `lesson_answers`, prune history per lesson to the 100 most recent answers, and compute `masteryScore = (correctCount / 100f) * 5.0f`.
- FR-3: Mark a lesson completed and unlock `orderIndex + 1` when `masteryScore >= 4.5f`.
- FR-4: Provide top-bar toggles in `CurriculumScreen` for Cyrillic/Latin script (`isLatinScript`) and dark/light theme (`isDarkTheme`).

## Non-functional requirements

- **Offline-first:** Initial seeding and mastery calculations run locally in Room.
- **Zero-friction UX:** Lesson cards clearly indicate locked, unlocked, or mastered states.

## Acceptance criteria

Defined in `Docs/psd/curriculum-progression.md` and verified in `Docs/tests/curriculum-progression.md`.

## Out of scope

- Multi-user profile switching.

## Cross-references

- `Docs/psd/curriculum-progression.md` — the specification that implements these requirements.
- `Docs/tests/curriculum-progression.md` — the test cases that verify them.
- [[ui-layer]] — where the screens and ViewModel state are explained.

## Sources

- `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` — main dashboard with tabs, script/theme toggles, and lesson cards.
- `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` — per-lesson hub linking to sentence practice, vocab drills, and grammar matrix.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — initial seeding and rolling 100-answer mastery calculation.
- `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` — built-in offline curriculum seed data.
- `Docs/psd/curriculum-progression.md` — product specification.
- `Docs/tests/curriculum-progression.md` — test cases.
