# Test Model — 3x3 Grammar Matrices & Guides (`grammar-matrices`)

- **Run Date:** 2026-11-14
- **Scope:** `grammar-matrices` — `GrammarMatrixScreen` / `ConjugationMatrixData` 3x3 verb conjugation matrix rendering, script display (`isLatinScript` Cyrillic/Latin), cell selection & detail card, and Serbian audio playback delegation (`viewModel.playAudio`).
- **Out of Scope (per spec):** Editable grammar tables in mobile UI; custom JSON import workflow; server-rendered markdown or webview grammar pages.
- **Gate Status:** `approved` (All gaps `G1`–`G7` resolved via `Docs/psd/grammar-matrices.md`, `Docs/tests/grammar-matrices.md`, `Entities.kt`, `GrammarMatrix.kt`, and `GrammarMatrixScreen.kt`).

---

## 1. Element Inventory & Technique Assignment

| ID | Kind | Name / Description | Assigned Technique(s) | Status |
|---|---|---|---|---|
| `P1` | Param | `GrammarGuideEntity` fields (`title`, `summary`, `explanationMarkdown`, `matrixJson`) & `lesson` fallback | EP, Error Guessing | Approved |
| `P2` | Param | `matrixJson` deserialization into `ConjugationMatrixData` & cell lookup | EP, Error Guessing | Approved |
| `P3` | Param | `ConjugationCell` detail fields (`formula`, `exampleSerbian`, `exampleSerbianLatin`, `exampleRussian`, `notes`) | EP, Error Guessing | Approved |
| `R1` | Rule | Script rendering & Latin fallback in `selected_matrix_cell_card` (`isLatinScript` × `exampleSerbianLatin.isBlank()`) | Decision Table | Approved |
| `R2` | Rule | Guide & matrix rendering (`grammarGuide != null` × `matrixValid` × `explanationMarkdown.isBlank()`) | Decision Table | Approved |
| `S1` | State | `NoCellSelected` (`selectedCell == null`, `selected_matrix_cell_card` hidden) | State Transition | Approved |
| `S2` | State | `CellSelected` (`selectedCell != null`, cell highlighted, detail card & audio button visible) | State Transition | Approved |
| `S3` | State | `AudioTriggered` (`viewModel.playAudio(...)` invoked; non-blocking UI remains interactive) | State Transition | Approved |
| `T1` | Transition (Legal) | `S1` --[tap non-null cell (`form`, `tense`)]--> `S2` | State Transition | Approved |
| `T2` | Transition (Legal) | `S2` --[tap different non-null cell (`form`, `tense`)]--> `S2` | State Transition | Approved |
| `T3` | Transition (Legal) | `S2` --[tap audio `IconButton` `"Озвучить пример"`]--> `S3` | State Transition | Approved |
| `T4` | Transition (Legal) | `S3` --[tap another cell or re-tap audio button]--> `S2` / `S3` (non-blocking) | State Transition | Approved |
| `T5` | Transition (Illegal) | `S1` --[tap missing `"—"` cell]--> `S1` (no-op; detail card stays hidden) | State Transition | Approved |
| `T6` | Transition (Illegal) | `S1` --[access `selected_matrix_cell_card` / audio button]--> absent while `selectedCell == null` | State Transition | Approved |
| `C1` | Config | Display matrix (`formType` × `tense` × `isLatinScript`) | Pairwise | Approved |
| `U1` | Use Case | Inspect grammar guide, select 3x3 cell, view detail card, play Serbian audio (basic + 2 alt + 3 exc) | Use Case | Approved |

---

## 2. Parameter Partitions (`P1`–`P3`)

### `P1`: `GrammarGuideEntity` & Lesson Fallback
- `P1-V1`: `grammarGuide != null` with non-blank `title`, `summary`, `explanationMarkdown`, and valid `matrixJson`.
- `P1-V2`: `grammarGuide != null` with `explanationMarkdown.isBlank()` → explanation card omitted.
- `P1-V3`: Multi-byte Unicode text (Serbian Cyrillic/Latin diacritics, Russian Cyrillic).
- `P1-I1`: `grammarGuide == null` → falls back to `lesson.title` and `lesson.subtitle`, `matrixData = null`, renders `FallbackMatrixTable`.

### `P2`: `matrixJson` (`ConjugationMatrixData`) & Cell Lookup
- `P2-V1`: Valid JSON with `cells` covering all 9 `(formType, tense)` combinations.
- `P2-I1`: Malformed JSON → Moshi parse exception caught → `matrixData = null` → `FallbackMatrixTable` renders without crash.
- `P2-I2`: Empty JSON (`"{}"`) or `matrixData.cells.isEmpty()` → `FallbackMatrixTable` renders.
- `P2-I3`: Missing cell for `(form, tense)` after `formType.startsWith(form.take(5)) && tense == tense` and `tense == tense` fallback → displays `"—"` and tap is a no-op.

### `P3`: `ConjugationCell` Detail Fields
- `P3-V1`: All fields populated (`formula`, `exampleSerbian`, `exampleSerbianLatin`, `exampleRussian`, `notes`).
- `P3-V2`: `notes.isBlank()` → `"Примечание: ..."` line omitted.
- `P3-I1`: `exampleSerbianLatin.isBlank()` when `isLatinScript == true` → PSD specifies fallback to transliterating or displaying `exampleSerbian` (note: current `GrammarMatrixScreen.kt` passes `cell.exampleSerbianLatin` directly).

---

## 3. Business Rules & Decision Tables (`R1`–`R2`)

### `R1`: Script Rendering & Latin Fallback
| Rule Column | `R1-C1` | `R1-C2` | `R1-C3` |
|---|---|---|---|
| `isLatinScript` | `False` | `True` | `True` |
| `exampleSerbianLatin.isBlank()` | `-` | `False` | `True` |
| **Action:** Render & pass `exampleSerbian` | `X` | — | `X` (fallback per PSD) |
| **Action:** Render & pass `exampleSerbianLatin` | — | `X` | — |

- Full columns: `4` | Reduced columns: `3`.

### `R2`: Guide & Matrix Availability
| Rule Column | `R2-C1` | `R2-C2` | `R2-C3` | `R2-C4` | `R2-C5` |
|---|---|---|---|---|---|
| `grammarGuide != null` | `False` | `True` | `True` | `True` | `True` |
| `matrixJson` valid & `cells.isNotEmpty()` | `-` | `False` | `False` | `True` | `True` |
| `explanationMarkdown.isBlank()` | `-` | `False` | `True` | `False` | `True` |
| **Action:** Header (`lesson.title` + `lesson.subtitle`) | `X` | — | — | — | — |
| **Action:** Header (`guide.title` + `guide.summary`) | — | `X` | `X` | `X` | `X` |
| **Action:** Render explanation card | — | `X` | — | `X` | — |
| **Action:** Render `Matrix3x3Table` | — | — | — | `X` | `X` |
| **Action:** Render `FallbackMatrixTable` | `X` | `X` | `X` | — | — |

- Full columns: `8` | Reduced columns: `5`.

---

## 4. State Transition Model (`S1`–`S3`, `T1`–`T6`)

- `S1` (`NoCellSelected`): `selectedCell == null`, `selected_matrix_cell_card` hidden.
- `S2` (`CellSelected`): `selectedCell != null`, cell highlighted, `selected_matrix_cell_card` visible with `formula`, Serbian example, `exampleRussian`, optional `notes`, and `"Озвучить пример"` button.
- `S3` (`AudioTriggered`): `viewModel.playAudio(text, isTargetLanguage = true)` invoked; UI remains non-blocking.
- Legal transitions: `T1` (`S1→S2`), `T2` (`S2→S2`), `T3` (`S2→S3`), `T4` (`S3→S2/S3`).
- Illegal / No-op transitions: `T5` (`S1→S1` tap `"—"`), `T6` (`S1` audio/detail card absent).

---

## 5. Configuration Matrix (`C1`)

- Dimensions: `formType` (`Affirmative`, `Negative`, `Interrogative`) × `tense` (`Past`, `Present`, `Future`) × `isLatinScript` (`Cyrillic/false`, `Latin/true`).
- Exhaustive: `18` | Pairwise: `9` configurations covering all `21` pairs (`0` missing).

---

## 6. Use Case Paths (`U1`)

- `U1-Basic`: Load guide + 3x3 matrix → tap cell → inspect detail card → tap `"Озвучить пример"` → `playAudio` called.
- `U1-Alt1`: Tap second cell → highlight and detail card update.
- `U1-Alt2`: `isLatinScript = true` → detail card & audio use Latin script.
- `U1-Exc1`: `grammarGuide == null` → `lesson.title`/`lesson.subtitle` + `FallbackMatrixTable`.
- `U1-Exc2`: Malformed/empty `matrixJson` → `FallbackMatrixTable` without crash.
- `U1-Exc3`: Missing cell `"—"` tapped → no-op, detail card stays hidden.

```qa-summary
schema_version=1
gate_status=approved
elements_total=16
elements_gap=0
techniques=EP,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=12
dt_columns_reduced=8
states_total=3
transitions_legal=4
transitions_illegal=2
configs=9
pairs_target=21
usecase_paths=6
```
