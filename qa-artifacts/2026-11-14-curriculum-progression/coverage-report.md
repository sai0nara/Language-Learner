# Coverage Report — Curriculum Progression & Mastery Unlocking

> **Test Model:** `qa-artifacts/2026-11-14-curriculum-progression/test-model.md`
> **Test Suite:** `qa-artifacts/2026-11-14-curriculum-progression/test-suite.md`

---

## 1. Requirements Traceability Matrix (RTM)

| Element ID | Description | Assigned Technique(s) | Test Case ID(s) | Status |
|---|---|---|---|---|
| `P1` | `correctCount` (`0..100`, mastery `90`) | EP, BVA | `TC-003`, `TC-004`, `TC-005`, `TC-006`, `TC-007`, `TC-008`, `TC-009`, `TC-012`, `TC-013` | Covered |
| `P2` | `answerCount` (`0..∞`, prune cap `100`) | EP, BVA | `TC-006`, `TC-007`, `TC-008`, `TC-009`, `TC-010`, `TC-011`, `TC-014`, `TC-020`, `TC-021` | Covered |
| `P3` | `masteryScore` (`0.0f..5.0f`, threshold `4.50f`) | EP, BVA | `TC-003`, `TC-004`, `TC-005`, `TC-012`, `TC-013` | Covered |
| `P4` | `isCorrect` (`true` / `false`) | EP | `TC-003`, `TC-004`, `TC-005`, `TC-007`, `TC-010`, `TC-011` | Covered |
| `P5` | `orderIndex` (sequential lesson order) | EP, BVA | — | **Gap-blocked (`G2`)** |
| `R1` | Initial data seeding (`ensureInitialDataLoaded`) | Decision Table | `TC-001`, `TC-002` | Covered |
| `R2` | Answer recording, pruning & sequential unlocking | Decision Table | `TC-003`, `TC-004`, `TC-005`, `TC-008`, `TC-010`, `TC-011`, `TC-020` | Covered (final-lesson edge `G3` declared) |
| `R3` | Locked lesson card tap guard | Decision Table | `TC-003`, `TC-015` | Covered (exact toast copy `G6` declared) |
| `S1` | State: `Locked` | State Transition | `TC-015`, `TC-016`, `TC-019` | Covered |
| `S2` | State: `Unlocked` | State Transition | `TC-003`, `TC-004`, `TC-019` | Covered |
| `S3` | State: `Mastered` | State Transition | `TC-003`, `TC-009`, `TC-019` | Covered |
| `T1` | Transition: `S1 -> S2` (prev lesson `>= 4.5f`) | State Transition | `TC-003`, `TC-011` | Covered |
| `T2` | Transition: `S2 -> S3` (lesson `>= 4.5f`) | State Transition | `TC-003`, `TC-005`, `TC-011` | Covered |
| `T3` | Transition: `S2 -> S2` (answer `< 4.5f`) | State Transition | `TC-004` | Covered |
| `T4` | Transition: `S3 -> S3` (answer `>= 4.5f`) | State Transition | `TC-009` | Covered |
| `T5` | Illegal Transition: `S1` tap -> no navigation | State Transition | `TC-015` | Covered |
| `T6` | Illegal Transition: `S1 -> S3` direct bypass | State Transition | `TC-016` | Covered |
| `T7` | Transition: `S3` when `masteryScore < 4.5f` | State Transition | — | **Gap-blocked (`G4`)** |
| `C1` | UI Config (`isLatinScript` × `isDarkTheme` × `lessonState`) | Pairwise | `TC-019` | Covered (16/16 pairs; defaults/persistence `G5` declared) |
| `U1` | First-launch init & display (`U1.B`, `U1.A1`) | Use Case | `TC-001`, `TC-002` | Covered |
| `U2` | Practice, mastery & unlock (`U2.B`, `U2.A1`, `U2.A2`, `U2.E1`) | Use Case | `TC-003`, `TC-010`, `TC-011`, `TC-015`, `TC-017` | Covered |
| `U3` | Script & theme toggling (`U3.B`) | Use Case | `TC-018` | Covered |

---

## 2. Coverage Summary

- **Testable Model Elements (`elements_total`):** `20` (`P1`–`P4`, `R1`–`R3`, `S1`–`S3`, `T1`–`T6`, `C1`, `U1`–`U3`)
- **Covered Elements (`elements_covered`):** `20`
- **Gap-Blocked Elements Excluded from Denominator (`elements_gap`):** `2` (`P5` blocked by `G2`; `T7` blocked by `G4`)
- **Model Coverage (`coverage_pct`):** `100.0%` (`20 / 20` testable elements covered; `2` gap-blocked elements excluded)

---

## 3. Reduction Ledger

| Stage | Count | Breakdown / Notes |
|---|---|---|
| **Exhaustive-Equivalent** | `59` | EP+BVA (`20`) + DT (`8`) + States/Transitions (`9`) + Configs (`12`) + Use Cases (`7`) + Error Guessing (`3`) |
| **Post-Reduction (Within-Technique)** | `50` | Pairwise `C1` reduced `12 -> 6` (`50.0%` reduction); State/Transition consolidated to `6` transitions covering `3` states |
| **Post-Dedup (Across-Technique)** | `21` | `TC-001`..`TC-021` (merged overlapping EP/BVA, DT, State Transition, and Use Case assertions with both element IDs preserved and zero weakened assertions) |

---

## 4. Declared Gaps (`G1`–`G7`)

| Gap ID | Unresolved Question | Impact / What It Blocks |
|---|---|---|
| `G1` | Timestamp tie-breaking in `pruneOldAnswers(lessonId)` when >100 rows share identical timestamps | Exact tie-break assertion in `TC-021` (`P2`) |
| `G2` | `orderIndex` base (`0`-based vs `1`-based) and total seeded lesson count | **Blocks `P5`** (`0` test cases generated) |
| `G3` | Behavior when final lesson reaches `masteryScore >= 4.5f` (`orderIndex + 1` missing) | Final-lesson boundary path in `R2` and `U2` |
| `G4` | Whether `Mastered` (`S3`) reverts to `Unlocked` (`S2`) or `orderIndex + 1` re-locks if `masteryScore` drops `< 4.5f` | **Blocks `T7`** (`0` test cases generated) |
| `G5` | Default values and cold-start persistence of `isLatinScript` and `isDarkTheme` | Persistence assertion in `TC-018` (`U3`, `C1`) |
| `G6` | Exact toast string and script/localization for locked-lesson tap | Exact toast copy assertion in `TC-015` (`R3`, `T5`, `U2.E1`) |
| `G7` | Imported decks progression rules (`orderIndex`, initial `isUnlocked`, `lesson_answers` reset/merge) | Imported-deck progression scenarios (out of seed-only scope) |

---

```qa-summary
schema_version=1
gate_status=approved
elements_total=20
elements_covered=20
elements_gap=2
coverage_pct=100.0
cases_total=21
cases_p0=7
cases_p1=9
cases_p2=5
exhaustive_equivalent=59
post_reduction=50
post_dedup=21
```
