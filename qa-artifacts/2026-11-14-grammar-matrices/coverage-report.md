# Coverage & Audit Report — 3x3 Grammar Matrices & Guides (`grammar-matrices`)

- **Run Date:** 2026-11-14
- **Audit Verdict:** **PASS** — 16 of 16 modeled elements covered (`100.0%`), `0` gaps remaining, all 21 pairwise parameter pairs covered in 9 configurations, and all footer counts reconcile with tables.
- **Status Note (2026-11-17):** `isLatinScript` script-toggle cases are **Obsolete** following track `latin_only_remove_script_switch_20261117` (UI renders Latin examples with primary fallback).

---

## 1. Risk Register

| Test Case ID | Impact (1–5) | Probability Signals Present | Unassessable Signals | Signal Count → Probability | Priority |
|---|---|---|---|---|---|
| `TC-001` | 3 (core workflow) | `boolean-logic complexity` (`R2` 3 conds), `known-fragile pattern` (Cyrillic/Russian encoding) | None | 2 → Medium | `P1` |
| `TC-002` | 3 (core workflow) | `known-fragile pattern` (Cyrillic/Russian encoding) | None | 1 → Low | `P2` |
| `TC-003` | 3 (core workflow) | `known-fragile pattern` (Latin diacritics/Russian encoding) | None | 1 → Low | `P2` |
| `TC-004` | 2 (degraded/recoverable) | `known-fragile pattern` (Cyrillic/Latin encoding) | None | 1 → Low | `P2` |
| `TC-005` | 3 (core workflow) | `boolean-logic complexity` (`R2` 3 conds), `known-fragile pattern` (encoding) | None | 2 → Medium | `P1` |
| `TC-006` | 3 (core workflow) | `boolean-logic complexity` (`R2` 3 conds), `known-fragile pattern` (encoding) | None | 2 → Medium | `P1` |
| `TC-007` | 3 (core workflow) | `boolean-logic complexity` (`R2` 3 conds) | None | 1 → Low | `P2` |
| `TC-008` | 1 (display-only) | `boolean-logic complexity` (`R2` 3 conds) | None | 1 → Low | `P2` |
| `TC-009` | 2 (degraded/recoverable) | `boolean-logic complexity` (`R2` 3 conds) | None | 1 → Low | `P2` |
| `TC-010` | 3 (core workflow) | `state-machine depth` (illegal/no-op `T5`), `known-fragile pattern` (encoding) | None | 2 → Medium | `P1` |
| `TC-011` | 3 (core workflow) | `state-machine depth` (illegal `T6`) | None | 1 → Low | `P2` |
| `TC-012` | 1 (display-only) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |
| `TC-013` | 2 (secondary config) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |
| `TC-014` | 2 (secondary config) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |
| `TC-015` | 2 (secondary config) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |
| `TC-016` | 2 (secondary config) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |
| `TC-017` | 2 (secondary config) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |
| `TC-018` | 2 (secondary config) | `known-fragile pattern` (encoding) | None | 1 → Low | `P2` |

---

## 2. Requirements Traceability Matrix (RTM)

| Element ID | Description | Technique(s) | Test Case IDs | Status |
|---|---|---|---|---|
| `P1` | `GrammarGuideEntity` fields & `lesson` fallback | EP, Error Guessing | `TC-001`, `TC-005`, `TC-008` | Covered |
| `P2` | `matrixJson` deserialization & cell lookup | EP, Error Guessing | `TC-001`, `TC-006`, `TC-007`, `TC-010` | Covered |
| `P3` | `ConjugationCell` detail fields (`formula`, examples, `notes`) | EP, Error Guessing | `TC-001`, `TC-004`, `TC-012` | Covered |
| `R1` | Script rendering & Latin fallback (`isLatinScript` × `exampleSerbianLatin.isBlank()`) | Decision Table | `TC-001`, `TC-003`, `TC-004` | Covered |
| `R2` | Guide & matrix availability (`grammarGuide` × `matrixValid` × `explanationMarkdown`) | Decision Table | `TC-001`, `TC-005`, `TC-006`, `TC-007`, `TC-008`, `TC-009` | Covered |
| `S1` | `NoCellSelected` (`selectedCell == null`) | State Transition | `TC-001`, `TC-010`, `TC-011` | Covered |
| `S2` | `CellSelected` (`selectedCell != null`) | State Transition | `TC-001`, `TC-002`, `TC-012` | Covered |
| `S3` | `AudioTriggered` (`viewModel.playAudio` invoked) | State Transition | `TC-001`, `TC-002` | Covered |
| `T1` | Legal: `S1` --[tap non-null cell]--> `S2` | State Transition | `TC-001` | Covered |
| `T2` | Legal: `S2` --[tap different non-null cell]--> `S2` | State Transition | `TC-002` | Covered |
| `T3` | Legal: `S2` --[tap audio button]--> `S3` | State Transition | `TC-001` | Covered |
| `T4` | Legal: `S3` --[tap cell / re-tap audio]--> `S2`/`S3` | State Transition | `TC-002` | Covered |
| `T5` | Illegal/No-op: `S1` --[tap `"—"` cell]--> `S1` | State Transition | `TC-010` | Covered |
| `T6` | Illegal: `S1` --[access detail card / audio]--> absent | State Transition | `TC-011` | Covered |
| `C1` | Display matrix (`formType` × `tense` × `isLatinScript`) — 21/21 pairs | Pairwise | `TC-001`, `TC-002`, `TC-003`, `TC-013`, `TC-014`, `TC-015`, `TC-016`, `TC-017`, `TC-018` | Covered |
| `U1` | End-to-end guide, 3x3 matrix, cell detail, audio (basic + 2 alt + 3 exc) | Use Case | `TC-001`, `TC-002`, `TC-003`, `TC-005`, `TC-006`, `TC-010` | Covered |

---

## 3. Coverage Summary & Gap List

- **Modeled Elements:** `16` (`elements_total=16`)
- **Elements Covered:** `16` (`elements_covered=16`)
- **Gap-Blocked Elements:** `0` (`elements_gap=0`)
- **Coverage Percentage:** `100.0%` (`16 / 16`)
- **Pairwise Verification:** `21 / 21` pairs covered across `9` configurations (`pairs_missing=0`).
- **Known Spec-vs-Code Discrepancy (noted for verification in `TC-004`):** `Docs/psd/grammar-matrices.md` requires fallback to transliterating or displaying `exampleSerbian` when `exampleSerbianLatin` is blank in Latin mode, whereas `GrammarMatrixScreen.kt` currently passes `cell.exampleSerbianLatin` directly.

---

## 4. Reduction Ledger

- **Exhaustive-equivalent:** `52`
- **Post-reduction (within-technique):** `36`
- **Post-dedup (across-technique):** `18` (`65.4%` total reduction from exhaustive-equivalent with `0` weakened assertions).

```qa-summary
schema_version=1
gate_status=approved
elements_total=16
elements_covered=16
elements_gap=0
coverage_pct=100.0
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
