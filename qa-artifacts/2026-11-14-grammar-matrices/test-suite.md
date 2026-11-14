# Test Suite — 3x3 Grammar Matrices & Guides (`grammar-matrices`)

- **Run Date:** 2026-11-14
- **Test Model:** `qa-artifacts/2026-11-14-grammar-matrices/test-model.md` (`gate_status=approved`)
- **Total Cases:** 18 (`P0`: 0, `P1`: 4, `P2`: 14)

---

## 1. Test Cases

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | Load grammar guide, select Affirmative/Past cell in Cyrillic, and trigger audio playback | Use Case, EP, Decision Table, State Transition, Pairwise, Error Guessing | Offline Room `grammar_guides` has valid `GrammarGuideEntity` with non-blank `explanationMarkdown` and 9-cell `matrixJson`; `isLatinScript = false`; `selectedCell = null` | `formType = "Affirmative"`, `tense = "Past"`, `isLatinScript = false`, Unicode Cyrillic/Russian strings | Guide `title`, `summary`, explanation card, and `Matrix3x3Table` render; tapping `(Affirmative, Past)` highlights cell and shows `selected_matrix_cell_card` with `formula`, `exampleSerbian`, `exampleRussian`, `notes`; tapping `"Озвучить пример"` calls `viewModel.playAudio(cell.exampleSerbian, isTargetLanguage = true)` | `P1` | `P1, P2, P3, R1, R2, S1, S2, S3, T1, T3, C1, U1` |
| `TC-002` | Switch from selected cell to Negative/Present cell and re-trigger audio without UI lock | Use Case, State Transition, Pairwise | `GrammarMatrixScreen` open with `(Affirmative, Past)` selected (`S2`) and audio triggered (`S3`) | Tap `(Negative, Present)` cell (`isLatinScript = false`), then tap `"Озвучить пример"` | Highlight moves to `(Negative, Present)`; `selected_matrix_cell_card` updates to new `formula`, examples, and `notes`; `viewModel.playAudio` is invoked with new `exampleSerbian` without blocking UI | `P2` | `S2, S3, T2, T4, C1, U1` |
| `TC-003` | Render and play Latin example when `isLatinScript = true` and `exampleSerbianLatin` is populated | Use Case, Decision Table, Pairwise | Valid `GrammarGuideEntity` with 9-cell `matrixJson`; `isLatinScript = true` | `formType = "Affirmative"`, `tense = "Present"`, `isLatinScript = true`, non-blank `exampleSerbianLatin` | `Matrix3x3Table` displays `cell.formula`; `selected_matrix_cell_card` displays `exampleSerbianLatin`; tapping `"Озвучить пример"` calls `viewModel.playAudio(cell.exampleSerbianLatin, isTargetLanguage = true)` | `P2` | `R1, C1, U1` |
| `TC-004` | Fallback to Cyrillic `exampleSerbian` when `isLatinScript = true` and `exampleSerbianLatin` is blank | Decision Table, EP, Error Guessing | Valid `GrammarGuideEntity`; `isLatinScript = true` | `ConjugationCell` with `exampleSerbian = "Ја радим"`, `exampleSerbianLatin = ""` | Per `Docs/psd/grammar-matrices.md`, detail card and `playAudio` fall back to transliterated or `exampleSerbian` text *(Note: verify against `GrammarMatrixScreen.kt` direct-pass discrepancy)* | `P2` | `P3, R1` |
| `TC-005` | Fallback to lesson title/subtitle and `FallbackMatrixTable` when `grammarGuide == null` | EP, Decision Table, Use Case | Lesson exists in DB with `lesson.title` and `lesson.subtitle`; `grammar_guides` has no row for `lessonId` (`grammarGuide = null`) | `lessonId = 99`, `grammarGuide = null` | Screen renders without crash; header shows `lesson.title` and `lesson.subtitle`; explanation card is omitted; `FallbackMatrixTable` renders | `P1` | `P1, R2, U1` |
| `TC-006` | Catch Moshi parse exception on malformed `matrixJson` and render `FallbackMatrixTable` | EP, Decision Table, Use Case, Error Guessing | `GrammarGuideEntity` exists with non-blank `explanationMarkdown` | `matrixJson = "{malformed_json"` | Parse exception is caught (`matrixData = null`); guide `title`, `summary`, and explanation card render; `FallbackMatrixTable` renders without crash | `P1` | `P2, R2, U1` |
| `TC-007` | Render `FallbackMatrixTable` when `matrixJson` is `"{}"` or has empty `cells` list | EP, Decision Table | `GrammarGuideEntity` exists with non-blank `explanationMarkdown` | Case A: `matrixJson = "{}"`; Case B: valid JSON with `cells = []` | `FallbackMatrixTable` renders in both cases; no 3x3 interactive cell grid or `selected_matrix_cell_card` is shown | `P2` | `P2, R2` |
| `TC-008` | Omit explanation card when `explanationMarkdown` is blank and `matrixJson` is valid | Decision Table, EP | `GrammarGuideEntity` exists with valid 9-cell `matrixJson` | `explanationMarkdown = "   "` (blank) | Guide `title` and `summary` render; explanation card is omitted from UI tree; `Matrix3x3Table` renders normally | `P2` | `P1, R2` |
| `TC-009` | Omit explanation card and render `FallbackMatrixTable` when `explanationMarkdown` is blank and `matrixJson` is invalid | Decision Table | `GrammarGuideEntity` exists | `explanationMarkdown = ""`, `matrixJson = "{}"` | Guide `title` and `summary` render; explanation card is omitted; `FallbackMatrixTable` renders | `P2` | `R2` |
| `TC-010` | Missing `(form, tense)` cell displays `"—"` and tapping it is a no-op | EP, State Transition, Use Case, Error Guessing | `GrammarGuideEntity` with `matrixJson` missing `(Interrogative, Future)` and no fallback `Future` cell; `selectedCell = null` | Tap `(Interrogative, Future)` cell | Cell displays `"—"`; tapping it leaves `selectedCell == null` (`S1`); `selected_matrix_cell_card` remains hidden | `P1` | `P2, S1, T5, U1` |
| `TC-011` | Initial state `S1 (NoCellSelected)` hides `selected_matrix_cell_card` and audio button | State Transition | `GrammarMatrixScreen` freshly opened with valid `GrammarGuideEntity` and 9-cell `matrixJson` | None (initial render) | `selectedCell == null`; `selected_matrix_cell_card` and `"Озвучить пример"` button are not present in the UI | `P2` | `S1, T6` |
| `TC-012` | Omit `"Примечание: ..."` line in detail card when `cell.notes` is blank | EP, State Transition | Valid 9-cell `matrixJson`; cell has `notes = ""` | Tap cell with `notes = ""` | `selected_matrix_cell_card` renders `formula`, Serbian example, `exampleRussian`, and audio button; `"Примечание:"` line is omitted | `P2` | `P3, S2` |
| `TC-013` | Pairwise config #3: `(Affirmative, Future, Cyrillic)` | Pairwise | Valid 9-cell `matrixJson`; `isLatinScript = false` | Tap `(Affirmative, Future)` | Cell displays `formula` and highlights; detail card shows Cyrillic `exampleSerbian` and `exampleRussian` | `P2` | `C1` |
| `TC-014` | Pairwise config #4: `(Negative, Past, Latin)` | Pairwise | Valid 9-cell `matrixJson`; `isLatinScript = true` | Tap `(Negative, Past)` | Cell displays `formula` and highlights; detail card shows `exampleSerbianLatin` and `exampleRussian` | `P2` | `C1` |
| `TC-015` | Pairwise config #6: `(Negative, Future, Latin)` | Pairwise | Valid 9-cell `matrixJson`; `isLatinScript = true` | Tap `(Negative, Future)` | Cell displays `formula` and highlights; detail card shows `exampleSerbianLatin` and `exampleRussian` | `P2` | `C1` |
| `TC-016` | Pairwise config #7: `(Interrogative, Past, Cyrillic)` | Pairwise | Valid 9-cell `matrixJson`; `isLatinScript = false` | Tap `(Interrogative, Past)` | Cell displays `formula` and highlights; detail card shows Cyrillic `exampleSerbian` and `exampleRussian` | `P2` | `C1` |
| `TC-017` | Pairwise config #8: `(Interrogative, Present, Latin)` | Pairwise | Valid 9-cell `matrixJson`; `isLatinScript = true` | Tap `(Interrogative, Present)` | Cell displays `formula` and highlights; detail card shows `exampleSerbianLatin` and `exampleRussian` | `P2` | `C1` |
| `TC-018` | Pairwise config #9: `(Interrogative, Future, Cyrillic)` | Pairwise | Valid 9-cell `matrixJson`; `isLatinScript = false` | Tap `(Interrogative, Future)` | Cell displays `formula` and highlights; detail card shows Cyrillic `exampleSerbian` and `exampleRussian` | `P2` | `C1` |

---

## 2. Gherkin Scenarios (`P0` / `P1` Cases)

```gherkin
Feature: 3x3 Grammar Matrices & Guides

  @TC-001 @P1
  Scenario: Load grammar guide, select Affirmative/Past cell in Cyrillic, and trigger audio playback
    Given the local "grammar_guides" table contains a valid GrammarGuideEntity with non-blank explanationMarkdown and a 9-cell matrixJson
    And isLatinScript is false
    When the learner opens GrammarMatrixScreen for the lesson
    And the learner taps the "Affirmative" / "Past" cell in the 3x3 matrix
    And the learner taps the "Озвучить пример" audio button
    Then the guide title, summary, explanation card, and 3x3 Matrix3x3Table are displayed
    And the "Affirmative" / "Past" cell is highlighted and selected_matrix_cell_card displays formula, exampleSerbian, exampleRussian, and notes
    And viewModel.playAudio is invoked with cell.exampleSerbian and isTargetLanguage = true

  @TC-005 @P1
  Scenario: Fallback to lesson title/subtitle and FallbackMatrixTable when grammarGuide is null
    Given the lesson exists with lesson.title and lesson.subtitle
    And no GrammarGuideEntity exists in "grammar_guides" for the lessonId
    When the learner opens GrammarMatrixScreen for the lesson
    Then the screen renders without crashing
    And the header displays lesson.title and lesson.subtitle
    And the explanation card is omitted and FallbackMatrixTable is displayed

  @TC-006 @P1
  Scenario: Catch Moshi parse exception on malformed matrixJson and render FallbackMatrixTable
    Given a GrammarGuideEntity exists with non-blank explanationMarkdown and malformed matrixJson "{malformed_json"
    When the learner opens GrammarMatrixScreen for the lesson
    Then the Moshi parse exception is caught without crashing
    And the guide title, summary, and explanation card are displayed
    And FallbackMatrixTable is displayed

  @TC-010 @P1
  Scenario: Missing (form, tense) cell displays "—" and tapping it is a no-op
    Given a GrammarGuideEntity exists whose matrixJson omits the "Interrogative" / "Future" cell with no tense fallback
    And selectedCell is initially null
    When the learner opens GrammarMatrixScreen and taps the "Interrogative" / "Future" cell displaying "—"
    Then selectedCell remains null
    And selected_matrix_cell_card remains hidden
```

---

## 3. Reduction Ledger

| Stage | Count | Notes |
|---|---|---|
| Exhaustive-equivalent | `52` | `P1`–`P3` (10) + `R1`/`R2` full DT (12) + `S`/`T` (6) + `C1` full 3×3×2 (18) + `U1` (6) |
| Post-reduction (within-technique) | `36` | EP/EG (7) + reduced DT `R1`(3)+`R2`(5) (8) + `S`/`T` (6) + Pairwise `C1` (9) + `U1` (6) |
| Post-dedup (across-technique) | `18` | Merged overlapping `U1`, `R1`/`R2`, `S`/`T`, and `C1` #1/#2/#5 rows with all element IDs preserved and zero weakened assertions |

```qa-summary
schema_version=1
gate_status=approved
cases_total=18
cases_p0=0
cases_p1=4
cases_p2=14
techniques=EP,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=12
dt_columns_reduced=8
states_total=3
transitions_legal=4
transitions_illegal=2
transitions_illegal_tested=2
usecase_paths=6
usecase_paths_tested=6
configs=9
pairs_target=21
pairs_covered=21
pairs_missing=0
exhaustive_equivalent=52
post_reduction=36
post_dedup=18
```
