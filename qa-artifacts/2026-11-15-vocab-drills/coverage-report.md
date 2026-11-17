# Coverage Report — `vocab-drills`

**Run:** `2026-11-15-vocab-drills`  
**Scope:** `VocabDrillScreen` (`app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt`) & `CurriculumRepository.recordAnswer`  
**Gate Status:** `approved` (Stage 1–3 complete using resolved gap context)
**Status Note (2026-11-17):** `isLatinScript` script-toggle dimensions and Cyrillic-only cases are **Obsolete** following track `latin_only_remove_script_switch_20261117` (UI renders Latin target words with primary fallback).

---

## 1. Requirements Traceability Matrix (RTM)

| Element ID | Description | Applied Technique(s) | Test Case ID(s) | Status |
|---|---|---|---|---|
| `P1` | `rawVocabList.size` (`0`, `1`, `>= 2`) | EP, BVA | `TC-001`, `TC-002`, `TC-003` | Covered |
| `P2` | `distractorsJson` (`"[]"`, `1..5`, `> 5` via `.take(5)`, malformed JSON, duplicate) | EP, BVA, Error Guessing | `TC-009`, `TC-010`, `TC-011`, `TC-012`, `TC-013` | Covered |
| `P3` | `partOfSpeech` & `exampleSentence` (blank vs non-blank) | EP, Error Guessing | `TC-014` | Covered |
| `P4` | `correctCount / initialTotal` progress calculation & `coerceIn(0f, 1f)` | EP, BVA | `TC-001`, `TC-002`, `TC-003`, `TC-015` | Covered |
| `P5` | `lesson.score` mastery threshold (`89` / `90` / `91` in 100-answer window) | EP, BVA | `TC-016` | Covered |
| `R1` | Option click evaluation (`selectedOption == null`, `isCorrect`, `API >= 26`) | Decision Table | `TC-002`, `TC-004`, `TC-005`, `TC-006` | Covered |
| `R2` | Option grid row chunking (`options.chunked(2)` even vs odd) | Decision Table | `TC-009`, `TC-010` | Covered |
| `S1` | State: `Empty` (`rawVocabList.isEmpty()`) | State Transition | `TC-001` | Covered |
| `S2` | State: `AwaitingAnswer` (`!isCompleted && selectedOption == null`) | State Transition | `TC-002`, `TC-003`, `TC-004`, `TC-007` | Covered |
| `S3` | State: `CorrectFeedback` (`selectedOption != null && isCorrectState == true`) | State Transition | `TC-002`, `TC-003`, `TC-006` | Covered |
| `S4` | State: `IncorrectFeedback` (`selectedOption != null && isCorrectState == false`) | State Transition | `TC-004`, `TC-006` | Covered |
| `S5` | State: `Completed` (`isCompleted == true`) | State Transition | `TC-001`, `TC-002`, `TC-003`, `TC-008` | Covered |
| `T1` | Legal: `S1 → S5` (empty list renders completion view) | State Transition | `TC-001` | Covered |
| `T2` | Legal: `S2 → S2` (tap `vocab_pronounce_button` replays target audio) | State Transition | `TC-007` | Covered |
| `T3` | Legal: `S2 → S3` (tap correct option) | State Transition | `TC-002` | Covered |
| `T4` | Legal: `S2 → S4` (tap incorrect option) | State Transition | `TC-004` | Covered |
| `T5` | Legal: `S3 → S2` (500ms delay, more items remain) | State Transition | `TC-003` | Covered |
| `T6` | Legal: `S3 → S5` (500ms delay, last item completed) | State Transition | `TC-002` | Covered |
| `T7` | Legal: `S4 → S2` (800ms delay, re-appended item keeps queue active) | State Transition | `TC-004` | Covered |
| `T8` | Legal: `S5 → S2` (tap `restart_drill_button` on non-empty lesson) | State Transition | `TC-008` | Covered |
| `T9` | Illegal: `S3 → S3/S4` (tap option while `selectedOption != null` in CorrectFeedback) | State Transition | `TC-006` | Covered |
| `T10` | Illegal: `S4 → S3/S4` (tap option while `selectedOption != null` in IncorrectFeedback) | State Transition | `TC-006` | Covered |
| `T11` | Illegal: `S5 → S2` (tap `restart_drill_button` when `rawVocabList.isEmpty()`) | State Transition | `TC-008` | Covered |
| `C1` | Config matrix (`distractorCount × isLatinScript × buildSdk × optionalMetadata`) | Pairwise | `TC-017`, `TC-018`, `TC-019` | Covered |
| `U1` | End-to-end Vocabulary Drill Session (1 basic + 2 alt + 2 exception paths) | Use Case | `TC-001`, `TC-002`, `TC-003`, `TC-004`, `TC-007`, `TC-012` | Covered |

---

## 2. Coverage Summary

- **Total modeled elements:** `25` (`5` Parameters, `2` Rules, `5` States, `11` Transitions, `1` Config Matrix, `1` Use Case)
- **Gap-blocked elements:** `0`
- **Covered elements:** `25` / `25`
- **Model coverage:** `100.0%` (`elements_gap=0`)
- **Decision table coverage:** `7` / `7` reduced columns (`10` full → `7` reduced; `30.0%` collapse)
- **State transition coverage:** `5` / `5` states, `8` / `8` legal transitions, `3` / `3` illegal transitions (`100.0%`)
- **Pairwise coverage:** `30` / `30` pairs (`0` missing; `24` exhaustive → `6` pairwise configs, `75.0%` reduction)
- **Use case coverage:** `5` / `5` paths (`1` basic + `2` alternative + `2` exception)

---

## 3. Gap List

No unresolved `G<n>` gaps remain; all ambiguities from `Docs/prd/vocab-drills.md` were resolved in the provided `RESOLVED GAPS` context (notably FR-2's `1 + 5` option cap without fallback or `.distinct()`, and FR-3's omission of source-translation audio and mid-drill `enrollInSrs`).

---

## 4. Reduction Ledger

| Stage | Count | Breakdown |
|---|---|---|
| Exhaustive-equivalent | `69` | `14` (EP+BVA) + `10` (DT full: `8+2`) + `11` (ST transitions) + `24` (Pairwise full) + `5` (Use Case paths) + `5` (Error Guessing) |
| Post-reduction | `45` | `14` (EP+BVA) + `7` (DT reduced: `5+2`) + `11` (ST transitions) + `3` (Pairwise `6` configs in `3` paired cases) + `5` (Use Case paths) + `5` (Error Guessing) |
| Post-dedup | `19` | `TC-001` through `TC-019` (`2` P0, `4` P1, `13` P2) — zero weakened assertions |

```qa-summary
schema_version=1
gate_status=approved
elements_total=25
elements_covered=25
elements_gap=0
coverage_pct=100.0
cases_total=19
cases_p0=2
cases_p1=4
cases_p2=13
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
boundaries_tested=0,1,2,4,5,6,89,90,91
dt_columns_full=10
dt_columns_reduced=7
states_total=5
transitions_legal=8
transitions_illegal=3
transitions_illegal_tested=3
usecase_paths=5
usecase_paths_tested=5
configs=6
pairs_target=30
pairs_covered=30
pairs_missing=0
exhaustive_equivalent=69
post_reduction=45
post_dedup=19
```
