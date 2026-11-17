# Architecture Overview

> What Language Learner is and how study, import, and review flows move through MVVM.

Last updated: 2026-11-16

## System Purpose

Language Learner is an offline-first Android application for self-directed language study. Learners import custom curricula (JSON, CSV, XML) or study the built-in 16-lesson curriculum through vocabulary drills, 3x3 grammar matrices, sentence construction, weak-cell remediation, and FSRS-based spaced repetition.

## Data Flow

`MainActivity` wires `AppDatabase` into `CurriculumRepository` and `LanguageLearnerViewModel`. Compose screens observe `StateFlow` state exposed by the ViewModel and dispatch user actions back to the repository, SRS engine, and audio controller.

## Cross-references

- [[data-layer]] — local Room persistence, curriculum importer, and repository.
- [[srs-and-audio]] — SM-2 scheduling and dual-audio pronunciation playback.
- [[ui-layer]] — Compose screens, navigation, and Material 3 theme.
- [[unit-and-ui-tests]] — automated unit, Room, and Compose UI test suites.

## Sources

- `app/src/main/java/com/example/MainActivity.kt` — application entry point and top-level wiring.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — central UI state holder.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — repository coordinating Room DAOs and SRS.

Back to [[overview]]
