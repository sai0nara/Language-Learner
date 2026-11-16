# 3x3 Grammar Matrices & Guides — Product Requirements

> Interactive 3x3 verb conjugation matrix (Affirmative / Negative / Interrogative × Past / Present / Future) with auxiliary automation, pronoun alignment, contextual pairings, and matrix mastery heatmap integration.

Last updated: 2026-11-16

## Overview

`app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` and `app/src/main/java/com/example/data/model/GrammarMatrix.kt` serve as the structural grammar blueprint for each lesson. Learners inspect the 3x3 verb conjugation matrix (`Affirmative (+)`, `Negative (-)`, `Interrogative (?)` × `Past`, `Present`, `Future`) before sentence synthesis and as contextual support during drills.

## Goals / Non-goals

**Goals**

- Display a lesson's grammar explanation (`GrammarGuide.contentMarkdown`) alongside an interactive 3x3 verb conjugation grid (`conjugationTablesJSON` / `ConjugationMatrixData`).
- Highlight auxiliary verb shifts (`did`, `do`, `does`, `will` / target-language equivalents), pronoun alignment tables (`1SG`, `2SG_PL`, `3SG`), and side-by-side bilingual contextual pairings.
- Support Cyrillic and Latin script rendering, cell detail inspection with audio playback, and 3x3 matrix mastery heatmap states (`UNTESTED`, `WEAK`, `PROFICIENT`, `MASTERED`).

**Non-goals**

- Editable grammar tables within the mobile UI (custom matrices are imported via JSON).

## User stories

- As a learner, I want to see Past, Present, and Future forms across Affirmative, Negative, and Interrogative rows in a single 3x3 table so that I can internalize structural patterns visually.
- As a learner, I want to tap any cell in the 3x3 matrix to inspect its conjugation formula, pronoun alignment, bilingual example, and audio pronunciation, or open the grammar guide directly from a failed sentence drill.

## Functional requirements

- **FR-1 (Grammar Guide Content):** Render the lesson's `GrammarGuide` (`id`, `lessonId`, `title`, `summary`, `contentMarkdown` / `explanationMarkdown`) covering auxiliary automation rules, pronoun/number agreement tables, and contextual usage edge cases.
- **FR-2 (3x3 Core Verb Matrix):** Deserialize `matrixJson` (`conjugationTablesJSON`) into `ConjugationMatrixData` and display a 3x3 table crossing `formType` (`Affirmative`, `Negative`, `Interrogative`) and `tense` (`Past`, `Present`, `Future`).
- **FR-3 (Cell Selection, Detail Card & Audio):** Highlight the selected `ConjugationCell` and render a detail card showing `formula`, `exampleSerbian` (or `exampleSerbianLatin` when Latin script is active, falling back to `exampleSerbian` when blank), `exampleRussian`, `notes`, and an audio playback button.
- **FR-4 (Matrix Mastery Heatmap & Remediation Link):** Support 9-cell `matrixStats` tracking (`UNTESTED` `#64748B`, `WEAK` `#EF4444`, `PROFICIENT` `#6366F1`, `MASTERED` `#10B981`) and direct navigation from incorrect sentence practice items to the corresponding grammar rule.

## Non-functional requirements

- **Offline-first:** All grammar guides and 3x3 matrices are stored locally in `grammar_guides`.
- **Ergonomics:** Matrix cells and audio controls meet the `48dp x 48dp` minimum touch target.

## Acceptance criteria

Defined in `Docs/psd/grammar-matrices.md` and verified in `Docs/tests/grammar-matrices.md`.

## Out of scope

- Server-rendered markdown or webview grammar pages.

## Cross-references

- `Docs/psd/grammar-matrices.md` — the specification that implements these requirements.
- `Docs/tests/grammar-matrices.md` — the test cases that verify them.
- `Docs/prd/curriculum-progression.md` — 9/9 matrix mastery gate and weak-cell remediation.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` — 3x3 matrix UI and cell detail card.
- `app/src/main/java/com/example/data/model/GrammarMatrix.kt` — `ConjugationMatrixData` and `ConjugationCell` models.
- `app/src/main/java/com/example/data/local/GrammarDao.kt` — Room DAO for grammar guides.
- `Docs/psd/grammar-matrices.md` — product specification.
- `Docs/tests/grammar-matrices.md` — test cases.
