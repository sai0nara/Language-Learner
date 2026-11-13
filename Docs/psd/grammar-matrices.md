# 3x3 Grammar Matrices & Guides — Product Specification

> Moshi-serialized `ConjugationMatrixData` stored in `GrammarGuideEntity.matrixJson` and rendered as an interactive 3x3 grid in `GrammarMatrixScreen`.

Last updated: 2026-11-13

## Overview

Implements `Docs/prd/grammar-matrices.md` by storing a 9-cell `ConjugationMatrixData` payload inside `GrammarGuideEntity.matrixJson` and deserializing it on demand in `GrammarMatrixScreen`.

## Architecture

- **Model:** `ConjugationMatrixData` and `ConjugationCell` (`@JsonClass(generateAdapter = true)` in `GrammarMatrix.kt`).
- **Persistence:** `GrammarDao` queries `grammar_guides` by `lessonId`.
- **Presentation:** `GrammarMatrixScreen` renders the explanation card, the 3x3 grid, and the selected cell's detail card.

## Data flow

1. When a lesson is selected, `LanguageLearnerViewModel` queries `repository.getGrammarGuideForLesson(lesson.id)` and updates `activeGrammar`.
2. `GrammarMatrixScreen` collects `activeGrammar` and `isLatinScript` and parses `guide.matrixJson` via Moshi's generated `ConjugationMatrixData` adapter.
3. The 3x3 grid iterates over `tenses = listOf("Past", "Present", "Future")` and `forms = listOf("Affirmative", "Negative", "Interrogative")`, matching each cell by `formType` and `tense`.
4. Tapping a cell updates `selectedCell` and triggers `viewModel.playAudio(cell.exampleSerbian, isTargetLanguage = true)`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `GrammarMatrixScreen` | `app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` | Renders grammar explanation, 3x3 conjugation grid, and selected cell detail card. |
| `ConjugationMatrixData` / `ConjugationCell` | `app/src/main/java/com/example/data/model/GrammarMatrix.kt` | Data classes for verb metadata and the 9 tense/form cells. |
| `GrammarDao` | `app/src/main/java/com/example/data/local/GrammarDao.kt` | Queries and inserts `GrammarGuideEntity` rows per lesson. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| `matrixJson` is empty (`"{}"`) or malformed | Moshi parse exception is caught and `matrixData` evaluates to `null`; the screen still renders the text explanation without crashing. |
| `exampleSerbianLatin` is blank when Latin script is enabled | Falls back to transliterating or displaying `exampleSerbian`. |

## Dependencies

- Moshi Kotlin codegen (`ksp(libs.moshi.kotlin.codegen)`) for `ConjugationMatrixData` and `ConjugationCell`.

## Cross-references

- `Docs/prd/grammar-matrices.md` — the requirements this specification implements.
- `Docs/tests/grammar-matrices.md` — the test cases that verify this design.
- [[data-layer]] — data layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/GrammarMatrixScreen.kt` — 3x3 matrix composable.
- `app/src/main/java/com/example/data/model/GrammarMatrix.kt` — matrix data models.
- `app/src/main/java/com/example/data/local/GrammarDao.kt` — Room DAO for grammar guides.
- `Docs/prd/grammar-matrices.md` — requirements this specification implements.
- `Docs/tests/grammar-matrices.md` — test cases.
