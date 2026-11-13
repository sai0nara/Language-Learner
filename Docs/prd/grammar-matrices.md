# 3x3 Grammar Matrices & Guides — Product Requirements

> Interactive 3x3 verb conjugation matrix (Affirmative / Negative / Interrogative across Past / Present / Future) with grammar explanations and audio examples.

Last updated: 2026-11-13

## Overview

`app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` and `app/src/main/java/com/example/data/model/GrammarMatrix.kt` provide structured grammar reference for each lesson. Learners inspect a 3x3 grid of conjugation cells (`Affirmative`, `Negative`, `Interrogative` × `Past`, `Present`, `Future`) and tap any cell to inspect its formula, notes, and bilingual example sentence with audio playback.

## Goals / Non-goals

**Goals**

- Display a lesson's grammar explanation alongside an interactive 3x3 verb conjugation grid.
- Support both Cyrillic and Latin script rendering inside matrix cells and example cards.
- Let learners tap any cell in the 3x3 grid to view its full conjugation formula, bilingual example, and grammar note, and listen to the Serbian example aloud.

**Non-goals**

- Editable grammar tables within the mobile UI (custom matrices are imported via JSON).

## User stories

- As a learner, I want to see Past, Present, and Future forms across Affirmative, Negative, and Interrogative rows in a single 3x3 table so that I can compare patterns visually.
- As a learner, I want to tap a cell in the 3x3 matrix to read a full example sentence and hear its pronunciation.

## Functional requirements

- FR-1: Render the lesson's `GrammarGuideEntity` title, summary, and explanation text.
- FR-2: Deserialize `matrixJson` into `ConjugationMatrixData` and display a 3x3 table organized by `formType` (`Affirmative`, `Negative`, `Interrogative`) and `tense` (`Past`, `Present`, `Future`).
- FR-3: Highlight the selected `ConjugationCell` and render a detail card showing `formula`, `exampleSerbian` (or `exampleSerbianLatin` when Latin script is active), `exampleRussian`, `notes`, and an audio playback button.

## Non-functional requirements

- **Offline-first:** All grammar guides and 3x3 matrices are stored locally in `grammar_guides`.

## Acceptance criteria

Defined in `Docs/psd/grammar-matrices.md` and verified in `Docs/tests/grammar-matrices.md`.

## Out of scope

- Server-rendered markdown or webview grammar pages.

## Cross-references

- `Docs/psd/grammar-matrices.md` — the specification that implements these requirements.
- `Docs/tests/grammar-matrices.md` — the test cases that verify them.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` — 3x3 matrix UI and cell detail card.
- `app/src/main/java/com/example/data/model/GrammarMatrix.kt` — `ConjugationMatrixData` and `ConjugationCell` models.
- `app/src/main/java/com/example/data/local/GrammarDao.kt` — Room DAO for grammar guides.
- `Docs/psd/grammar-matrices.md` — product specification.
- `Docs/tests/grammar-matrices.md` — test cases.
