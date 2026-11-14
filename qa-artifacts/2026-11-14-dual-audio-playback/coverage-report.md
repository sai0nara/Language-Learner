# Coverage Report — Dual-Audio & TTS Pronunciation Playback

- **Run Date:** 2026-11-14
- **Scoped Area:** Dual-Audio & TTS Pronunciation Playback (`AudioController` + `LanguageLearnerViewModel.playAudio` / `onCleared()`)
- **Gate Status:** `approved`

---

## 1. Requirements Traceability Matrix (RTM)

| Element ID | Description | Technique | Test Case IDs | Status |
|---|---|---|---|---|
| `P1` | `text` (spoken utterance for TTS fallback) | EP, Error Guessing | `TC-001, TC-003, TC-004, TC-005, TC-020, TC-021` | Covered (partial gap `G1`) |
| `P2` | `audioUri` (custom audio file path or URI) | EP, Error Guessing | `TC-001, TC-002, TC-003, TC-004, TC-005, TC-006, TC-007, TC-008` | Covered (partial gap `G2`) |
| `P3` | `isTargetLanguage` (`true` → `sr_RS`, `false` → `ru_RU`) | EP | `TC-001, TC-002, TC-003, TC-004, TC-005, TC-006` | Covered |
| `R1` | Audio Playback Path & TTS Fallback Rule (5 reduced columns) | Decision Table | `TC-001, TC-002, TC-003, TC-004, TC-005, TC-006, TC-007, TC-008, TC-020, TC-021` | Covered (async note `G6`) |
| `R2` | TTS Locale Selection & Fallback Rule (4 stated columns) | Decision Table | `TC-003, TC-004, TC-005, TC-006, TC-020` | Covered (partial gap `G3`) |
| `S1` | State: `Uninitialized` | State Transition | `TC-002, TC-003, TC-007, TC-008` | Covered |
| `S2` | State: `Ready` | State Transition | `TC-001, TC-003, TC-004, TC-005, TC-006, TC-009` | Covered |
| `S3` | State: `PlayingCustomAudio` | State Transition | `TC-001, TC-002, TC-009` | Covered |
| `S4` | State: `PlayingTTS` | State Transition | `TC-003, TC-004, TC-005, TC-006, TC-009, TC-020` | Covered |
| `S5` | State: `Released` | State Transition | `TC-009` | Covered |
| `T1` | Legal: `S1` → `S2` on `onInit(SUCCESS)` | State Transition | `TC-003` | Covered |
| `T2` | Legal: `S1` → `S3` on `speak` (valid `audioUri`) | State Transition | `TC-002` | Covered |
| `T3` | Legal: `S1` → `S1` on `speak` (blank/invalid `audioUri`) | State Transition | `TC-007, TC-008` | Covered |
| `T4` | Legal: `S2` → `S3` on `speak` (valid `audioUri`) | State Transition | `TC-001` | Covered |
| `T5` | Legal: `S2` → `S4` on `speak` (blank/invalid `audioUri`) | State Transition | `TC-003, TC-004, TC-005, TC-006, TC-020` | Covered |
| `T6` | Legal: `S3` → `S2` on `MediaPlayer` completion | State Transition | `TC-001` | Covered |
| `T7` | Legal: `S4` → `S2` on TTS completion | State Transition | `TC-003` | Covered |
| `T8` | Legal: `S1/S2/S3/S4` → `S5` on `onCleared()` / `release()` | State Transition | `TC-009` | Covered |
| `T9` | Illegal: `S5` → `S3/S4` on `speak(...)` after `release()` | State Transition | — | **GAP** (`G4`) |
| `T10` | Illegal: `S3` → `S3/S4` on `speak(...)` while `MediaPlayer` playing | State Transition | — | **GAP** (`G5`) |
| `C1` | Playback Configuration Matrix (62 pairs across 16 configs) | Pairwise | `TC-001, TC-002, TC-004, TC-005, TC-006, TC-007, TC-010, TC-011, TC-012, TC-013, TC-014, TC-015, TC-016, TC-017, TC-018, TC-019` | Covered |
| `U1` | Pronunciation Playback & Lifecycle (6 paths) | Use Case | `TC-001, TC-003, TC-004, TC-005, TC-006, TC-007, TC-008, TC-009` | Covered (UI note `G6`) |

---

## 2. Coverage Summary

- **Total Modeled Elements (`elements_total`):** 22 (`P1`–`P3`, `R1`–`R2`, `S1`–`S5`, `T1`–`T10`, `C1`, `U1`)
- **Gap-Blocked Elements (`elements_gap`):** 2 (`T9`, `T10` — excluded from denominator; no test cases emitted)
- **Covered Elements (`elements_covered`):** 20 of 20 non-gap elements
- **Coverage (`coverage_pct`):** **100.0%** (`elements_covered=20`, `elements_total=22`, `elements_gap=2`; partial gaps `G1`–`G3` and `G6` remain declared below)
- **Pairwise Coverage (`C1`):** 62 / 62 pairs covered across 16 configurations (0 missing)
- **Use Case Coverage (`U1`):** 6 / 6 paths covered (1 basic + 2 alternative + 3 exception)
- **State Transition Coverage:** 5 / 5 states, 8 / 8 legal transitions, 0 / 2 illegal transitions (2 gap-blocked)

---

## 3. Reduction Ledger

| Stage | Count | Notes |
|---|---|---|
| Exhaustive-equivalent | 132 | `P1..P3` (8) + `R1` full (8) + `R2` stated (4) + `T1..T8` (8) + `C1` exhaustive (96) + `U1` (6) + Error Guessing (2) |
| Post-reduction (within technique) | 49 | `P1..P3` (8) + `R1` reduced (5) + `R2` (4) + `T1..T8` (8) + `C1` pairwise (16, 83.3% reduction) + `U1` (6) + Error Guessing (2) |
| Post-dedup (across techniques) | 21 | `TC-001`..`TC-021`; all `Covers` IDs preserved without weakening any assertion |

---

## 4. Declared Gaps (`G1`–`G6`)

| Gap ID | Unresolved Question / Ambiguity | Blocks / Affects |
|---|---|---|
| `G1` | Expected behavior when `text` is empty (`""`) or whitespace-only during TTS fallback, and any length limits. | Partially blocks `P1` (`TC-021` expectation marked `[UNSTATED / G1]`) |
| `G2` | Supported `audioUri` URI schemes (`file://`, `content://`, `android.resource://`, raw paths, `http(s)://`) and nullability. | Partially blocks `P2` |
| `G3` | Fallback behavior when `ru_RU` (`isTargetLanguage == false`) is missing, and when `Locale.getDefault()` itself is missing/unsupported. | Partially blocks `R2` (`TC-010`, `TC-011`, `TC-017` marked `[UNSTATED / G3]`) |
| `G4` | Behavior on `OnInitListener.onInit` error status (`status != SUCCESS`), `speak()` after `release()`, and `release()` idempotency. | **Blocks `T9`** (0 cases emitted) |
| `G5` | Concurrent/overlapping `speak()` calls while `MediaPlayer` is already playing. | **Blocks `T10`** (0 cases emitted) |
| `G6` | Async `MediaPlayer` errors (`OnErrorListener`) after `prepare`/`start`, and UI playback indicator/feedback expectations. | Affects `R1` and `U1` |

---

```qa-summary
schema_version=1
gate_status=approved
elements_total=22
elements_covered=20
elements_gap=2
coverage_pct=100.0
cases_total=21
cases_p0=0
cases_p1=1
cases_p2=20
techniques=EP,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=8
dt_columns_reduced=5
states_total=5
transitions_legal=8
transitions_illegal=2
transitions_illegal_tested=0
usecase_paths=6
usecase_paths_tested=6
configs=16
pairs_target=62
pairs_covered=62
pairs_missing=0
exhaustive_equivalent=132
post_reduction=49
post_dedup=21
```
