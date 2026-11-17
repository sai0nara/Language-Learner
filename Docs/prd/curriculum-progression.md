# Curriculum Progression & Mastery Unlocking — Product Requirements

> Polyglot 16-inspired structured lesson dashboard (Lessons 1–16) with two-tier progression gates, 3x3 matrix heatmap tracking, targeted remediation micro-sessions, and client state machine.

Last updated: 2026-11-17

## Overview

`app/src/main/java/com/example/ui/screens/CurriculumScreen.kt`, `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt`, and `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` present a vertical curriculum stream from conceptual grammar understanding through passive vocabulary recognition to active sentence synthesis.

## Goals / Non-goals

**Goals**

- Present a progressive, numbered curriculum (Phase 1 MVP: Lessons 1–4; Phase 2 full catalog: Lessons 1–16) with clear grammatical focus tags, completion statuses (`LOCKED`, `AVAILABLE`, `IN_PROGRESS`, `MASTERED`), and direct shortcuts to Vocabulary Primer, Grammar Guide, and Sentence Practice.
- Enforce two-tier progression gates: `>= 70%` lesson completion score to unlock Lesson `N+1` (`AVAILABLE`), and a 4-condition `MASTERED` gate combining lexical stability, 9/9 3x3 matrix coverage, zero unresolved lapses, and reflex latency `<= 12000ms`.
- Visualize 3x3 matrix mastery via a heatmap (`UNTESTED`, `WEAK`, `PROFICIENT`, `MASTERED`) and launch targeted 6–8 item remediation micro-sessions for `WEAK` coordinates.
- Support Cyrillic/Latin script toggling and Slate-neutral Material 3 design tokens with `48dp x 48dp` minimum touch targets.

**Non-goals**

- Gamified streaks, leaderboards, or disconnected phrasebook memorization.
- Blocking access to new unlocked lessons due to overdue spaced-repetition review backlogs.

## User stories

- As a learner, I want Lesson 1 available immediately and Lesson `N+1` unlocked once I finish Lesson `N` with `>= 70%` accuracy so that I maintain momentum without artificial bottlenecks.
- As a learner, I want a 3x3 matrix heatmap on the Lesson Detail modal and Summary screen so that I can spot `WEAK` tense/form coordinates and launch a focused "Drill Weak Cells" micro-session.
- As a learner, I want a clear `MASTERED` badge awarded only when my vocabulary stability, 9/9 matrix accuracy, unresolved lapses, and assembly speed all meet fluency targets.

## Functional requirements

- **FR-1 (Main Menu & Lesson Item Metadata):** Each lesson card in the vertical curriculum stream renders:
  - Identification: Lesson number (`orderIndex`) and primary focus `title` (e.g., `Lesson 1: The Core Verb Matrix`).
  - Grammatical Focus Tag: Categorization pill (`grammarFocus`).
  - Status & Progress: `LOCKED`, `AVAILABLE`, `IN_PROGRESS`, or `MASTERED` indicator alongside aggregate completion percentage / `practiceScore`.
  - Interactions: Progress bar view; tapping a Lesson Card opens the Lesson Detail modal (objectives, estimated time, 3x3 matrix heatmap, sub-module navigation); Quick Action shortcuts jump directly to Vocabulary Primer, Grammar Guide, or Sentence Practice if unlocked.
- **FR-2 (Two-Tier Progression Gates):**
  - **Lesson Unlock Gate (`AVAILABLE` for Lesson `N+1`):** Completing the core lesson cycle for Lesson `N` with `practiceScore >= 0.70` (`>= 70%`) unlocks Lesson `N+1`.
  - **Lesson Mastery Gate (`MASTERED` for Lesson `N`):** Awarded only when all four conditions hold simultaneously:
    1. Lexical stability: `vocabMastery >= 0.85` (at least 85% of lesson vocabulary in `REVIEW` state with `stability >= 3` days).
    2. Full 9/9 matrix coverage: Every cell in `matrixStats` has `attempts >= 3` and `accuracy >= 0.80` (zero `UNTESTED` or `WEAK` cells).
    3. Zero unresolved lapses: `unresolvedLapses == 0` (all `AGAIN`-rated items cleared).
    4. Reflex latency threshold: `medianAssemblyMs <= 12000`.
- **FR-3 (3x3 Matrix Heatmap & Targeted Remediation):**
  - Render `matrixStats` across all 9 `(tense × form)` coordinates using design tokens: `UNTESTED` (`attempts < 3`) `#64748B`, `WEAK` (`accuracy < 70%`) `#EF4444`, `PROFICIENT` (`accuracy 70%–84%` or `avgLatencyMs > 12000`) `#6366F1`, `MASTERED` (`accuracy >= 85%` and `avgLatencyMs <= 12000`) `#10B981`.
  - Whenever `>= 1` cell is `WEAK`, surface a `"Drill Weak Cells"` action launching a focused `6`–`8` item sentence construction session filtered strictly by those `matrixCoordinate` keys.
- **FR-4 (Client State Machine & Data Model):**
  - Enforce transitions: `IDLE -> DAILY_REVIEW -> (VOCAB_PRACTICE / SENTENCE_PRACTICE) -> SUMMARY_RESULT`, or `IDLE -> VOCAB_PRACTICE -> GRAMMAR_VIEW -> SENTENCE_PRACTICE -> SUMMARY_RESULT -> REPEAT / EXIT`.
  - Track `Lesson` (`id`, `orderIndex`, `title`, `grammarFocus`, `prerequisites`) and `UserProgress` (`userId`, `lessonId`, `vocabMastery`, `practiceScore`, `unresolvedLapses`, `medianAssemblyMs`, `matrixStats`, `status`, `lastAttemptedAt`).
- **FR-5 (Script & Theme Toggles):** Support Cyrillic/Latin script toggling and Slate-neutral light/dark theme switching (`#0F172A`, `#64748B`, `#F8FAFC`, `#10B981`, `#EF4444`, `#6366F1`).

## Non-functional requirements

- **Offline-first (Phase 2):** Curriculum catalog, progress tracking, and matrix stats persist locally for offline sessions.
- **Ergonomics & Adaptability:** All interactive elements maintain a minimum touch target of `48dp x 48dp`, optimized for single-handed mobile use and responsive multi-column desktop layouts.

## Acceptance criteria

Defined in `Docs/psd/curriculum-progression.md` and verified in `Docs/tests/curriculum-progression.md`.

## Out of scope

- Multi-user cloud leaderboards or speech-to-text oral grading (deferred to Phase 3).

## Cross-references

- `Docs/psd/curriculum-progression.md` — the specification that implements these requirements.
- `Docs/tests/curriculum-progression.md` — the test cases that verify them.
- `Docs/prd/spaced-repetition.md` — FSRS v5 scheduling and `DAILY_REVIEW` blending.
- `Docs/prd/curriculum-import.md` — custom curriculum import rules.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` — main curriculum dashboard.
- `app/src/main/java/com/example/ui/screens/LessonDetailScreen.kt` — per-lesson detail and sub-module hub.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — ViewModel state and progression actions.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — local persistence and progression orchestration.
- `app/src/main/java/com/example/data/local/LessonDao.kt` — Room DAO for lessons.
- `app/src/main/java/com/example/data/local/LessonAnswerDao.kt` — Room DAO for lesson answers.
- `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` — offline seed curriculum.
- `Docs/psd/curriculum-progression.md` — product specification.
- `Docs/tests/curriculum-progression.md` — test cases.
