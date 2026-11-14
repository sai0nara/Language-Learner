# Coverage Report — Curriculum Import & Export

**Scope:** `Curriculum Import & Export` (`ImportExportScreen`, `CurriculumImporter`, `CurriculumRepository.importCurriculum`, `CurriculumRepository.resetToDefaultCurriculum`).

## 1. Requirements Traceability Matrix (RTM)

| Element ID | Description | Technique(s) | Test Case ID(s) | Status |
|---|---|---|---|---|
| `P1` | `rawPayload` (`String` via SAF or pasted text) | EP, Error Guessing | `TC-001`, `TC-002`, `TC-004`, `TC-005`, `TC-006`, `TC-007`, `TC-008`, `TC-009`, `TC-010`, `TC-018`, `TC-019`, `TC-020` | Covered (partial assertion gaps `G5`, `G7`) |
| `P2` | `jsonStructureMode` (`lessons` array vs standalone `vocab` array) | EP | `TC-001`, `TC-002`, `TC-003`, `TC-008`, `TC-018` | Covered (schema gap `G2`, empty-object gap `G5`) |
| `P3` | `csvFormatOptions` (`,`, `;`, quoted embedded commas, optional header) | EP | `TC-004`, `TC-005`, `TC-010`, `TC-019` | Covered (edge-case gaps `G2`, `G6`) |
| `P4` | `xmlStructure` (`<curriculum>`, `<lesson>`, `<vocab>`, `<sentence>`, orphan tags) | EP | `TC-006`, `TC-007`, `TC-009`, `TC-018` | Covered (schema/grammar gaps `G2`, `G5`, `G8`) |
| `R1` | Import Outcome & Persistence (5 reduced columns) | Decision Table | `TC-001` (Col2), `TC-002` (Col4), `TC-003` (Col3), `TC-004` (Col4), `TC-008` (Col1), `TC-010` (Col1), `TC-018` (Col5) | Covered (DB/SRS/UI gaps `G1`, `G3`, `G4`, `G5`, `G9`) |
| `R2` | CSV Line Tokenization `parseCsvLine` (6 reduced columns) | Decision Table | `TC-004` (Col1–Col6), `TC-005` (Col1, Col3) | Covered (edge-case gap `G6`) |
| `S1` | `ExistingCurriculum` state | State Transition | `TC-001`, `TC-008`, `TC-010`, `TC-012` | Covered (`G1`) |
| `S2` | `ImportedCurriculum` state | State Transition | `TC-001`, `TC-009`, `TC-011`, `TC-013` | Covered (`G1`, `G3`, `G4`) |
| `S3` | `ImportFailed` state | State Transition | `TC-008`, `TC-009`, `TC-010`, `TC-011`, `TC-014` | Covered (`G5`, `G9`) |
| `S4` | `DefaultSeedCurriculum` state | State Transition | `TC-012`, `TC-013`, `TC-014` | Covered (`G9`) |
| `T1` | `S1` -> `S2` (valid payload) | State Transition | `TC-001` | Covered |
| `T2` | `S1` -> `S3` (malformed/blank payload) | State Transition | `TC-008`, `TC-010` | Covered |
| `T3` | `S1` -> `S4` (reset to default) | State Transition | `TC-012` | Covered |
| `T4` | `S2` -> `S3` (malformed/blank payload) | State Transition | `TC-009` | Covered |
| `T5` | `S2` -> `S4` (reset to default) | State Transition | `TC-013` | Covered |
| `T6` | `S3` -> `S2` (valid payload) | State Transition | `TC-011` | Covered |
| `T7` | `S3` -> `S4` (reset to default) | State Transition | `TC-014` | Covered |
| `C1` | Import Format (`JSON`/`CSV`/`XML`) × Input Source (`SAF`/`Pasted`) (6 pairs) | Pairwise | `TC-001`, `TC-002`, `TC-004`, `TC-005`, `TC-006`, `TC-007` | Covered (6/6 pairs) |
| `C2` | Sample Template Format (`JSON`/`CSV`/`XML`) × Action (`Load`/`Copy`) (6 pairs) | Pairwise | `TC-015`, `TC-016`, `TC-017` | Covered (6/6 pairs) |
| `U1` | Import Curriculum (Basic + 1 Alt + 2 Exception paths) | Use Case | `TC-001` (Basic), `TC-015` (Alt), `TC-008` (E1), `TC-009` (E1), `TC-010` (E2) | Covered (4/4 paths) |
| `U2` | Reset to Default Curriculum (Basic path) | Use Case | `TC-012` | Covered (1/1 path) |

---

## 2. Coverage Summary

- **Modeled non-gap elements:** `21` (`P1`–`P4`, `R1`–`R2`, `S1`–`S4`, `T1`–`T7`, `C1`–`C2`, `U1`–`U2`)
- **Elements covered:** `21 / 21` (**`100.0%`**)
- **Declared gaps (`elements_gap`):** `10` (`G1`–`G10`, excluded from denominator per `test-artifact-schema`; affected assertions in `test-suite.md` are explicitly tagged `[UNSTATED / G<n>]`)
- **Pairwise coverage:** `12 / 12` pairs (`C1`: 6/6, `C2`: 6/6; `pairs_missing = 0`)
- **Decision table reduced column coverage:** `11 / 11` (`R1`: 5/5, `R2`: 6/6)
- **State transition coverage:** `4 / 4` states, `7 / 7` legal transitions (`0` illegal transitions modeled)
- **Use case path coverage:** `5 / 5` paths (`U1`: 4/4, `U2`: 1/1)

---

## 3. Reduction Ledger

| Stage | Count | Notes |
|---|---|---|
| **Exhaustive-equivalent** | `61` | EP (`19`) + `R1` full DT (`8`) + `R2` full DT (`6`) + State Transitions (`7`) + `C1` (`6`) + `C2` (`6`) + Use Cases (`5`) + Error Guessing (`4`) |
| **Post-reduction (within-technique)** | `58` | `R1` don't-care collapse (`8 -> 5`, saving `3` columns when `parse succeeds = false`) |
| **Post-dedup (cross-technique)** | `20` | Merged overlapping EP, DT, State Transition, Pairwise, Use Case, and Error Guessing rows into `TC-001`..`TC-020` while preserving all `Covers` IDs and without weakening any assertion |

---

## 4. Declared Gaps (`G1`–`G10`)

Per Paste-only I/O, no unstated rules, schemas, or boundaries were invented. The following gaps remain open and are marked `[UNSTATED / G<n>]` in `test-suite.md`:

| Gap ID | Unresolved Question | Impact on Suite |
|---|---|---|
| `G1` | Import merge vs. replace semantics (append vs. upsert vs. wipe-and-replace in Room) | Marked `[UNSTATED / G1]` in `TC-001`, `TC-002`, `TC-004` |
| `G2` | Exact JSON keys (including 3x3 grammar matrix), CSV column count/order, XML attributes vs. elements, and sample template contents | Marked `[UNSTATED / G2]` in `TC-001`–`TC-006`, `TC-015`–`TC-017`, `TC-019`, `TC-020` |
| `G3` | Subsequent lessons' `isUnlocked` state (default `false` vs. payload-driven) | Marked `[UNSTATED / G3]` in `TC-001` |
| `G4` | Duplicate / re-imported vocabulary SRS behavior (skip vs. reset vs. duplicate) | Marked `[UNSTATED / G4]` in `TC-001`, `TC-002`, `TC-004` |
| `G5` | Blank/empty JSON & XML and valid-empty structures (`{}`, `<curriculum/>`) behavior/message | Marked `[UNSTATED / G5]` in `TC-018` |
| `G6` | CSV edge cases (header detection rule, `""` escaped quotes, wrong column count, mixed `,`/`;`) | Marked `[UNSTATED / G6]` in `TC-004`, `TC-005` |
| `G7` | Input size/length limits, SAF MIME filters, unreadable URI, and non-UTF-8 encoding | Unassessed in probability `boundary density` across import cases |
| `G8` | XML `<grammar>` tag support (JSON FR-1 mentions `grammar`, XML FR-3 does not) | Excluded from `TC-006` |
| `G9` | Exact success message format, reset confirmation/feedback, and template load/copy UI feedback | Marked `[UNSTATED / G9]` in `TC-001`–`TC-007`, `TC-011`–`TC-017`, `TC-019` |
| `G10` | Concurrency / double-submit button-disable behavior during `Dispatchers.IO` | Marked `[UNSTATED / G10]` in `TC-020` |

```qa-summary
schema_version=1
gate_status=approved
elements_total=21
elements_covered=21
elements_gap=10
coverage_pct=100.0
cases_total=20
cases_p0=9
cases_p1=8
cases_p2=3
techniques=EP,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=14
dt_columns_reduced=11
states_total=4
transitions_legal=7
transitions_illegal=0
usecase_paths=5
configs=2
pairs_target=12
pairs_covered=12
pairs_missing=0
exhaustive_equivalent=61
post_reduction=58
post_dedup=20
```
