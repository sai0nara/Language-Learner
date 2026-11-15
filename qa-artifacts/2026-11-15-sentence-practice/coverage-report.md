# Coverage Report — `sentence-practice`

Run directory: `qa-artifacts/2026-11-15-sentence-practice/`
Scope: `SentencePracticeScreen.kt` (token normalization, JSON parsing, answer validation, mastery/progress state, help dialog, and session lifecycle)

## 1. Requirements Traceability Matrix (RTM)

| Element ID | Description | Technique | Test Case IDs | Status |
|---|---|---|---|---|
| `P1` | `cleanToken(raw)` string normalization (`trim`, `lowercase`, `[\p{P}\p{S}]` strip, blank filter) | EP, Error Guessing | `TC-001`, `TC-002` | Covered |
| `P2` | `LessonEntity.masteryScore` (`0.0f..5.0f`, completion/unlock at `>= 4.5f`) | EP, BVA | `TC-003`, `TC-004`, `TC-005`, `TC-006`, `TC-007`, `TC-008` | Covered |
| `P3` | `rawSentences` list size (`0` empty vs `>= 1` non-empty) | EP, BVA | `TC-009`, `TC-010` | Covered |
| `R1` | Answer validation & side effects (`placedTokens.isNotEmpty()`, `isLatin`, `userTokens == expectedTokens`) | Decision Table | `TC-011`, `TC-012`, `TC-013`, `TC-014`, `TC-015` | Covered |
| `S1` | State: Empty / Finished Summary | State Transition | `TC-009`, `TC-010`, `TC-019` | Covered |
| `S2` | State: Drilling — Empty Assembly Bay | State Transition | `TC-011`, `TC-016`, `TC-017`, `TC-018` | Covered |
| `S3` | State: Drilling — Tokens Placed (Unchecked) | State Transition | `TC-012`, `TC-013`, `TC-016`, `TC-017`, `TC-018` | Covered |
| `S4` | State: Validated — Correct | State Transition | `TC-010`, `TC-012`, `TC-014` | Covered |
| `S5` | State: Validated — Incorrect | State Transition | `TC-013`, `TC-015`, `TC-017` | Covered |
| `S6` | State: Help Dialog Open | State Transition | `TC-018` | Covered |
| `T1` | Legal: `S2 -> S3` (tap `pool_chip_$token`) | State Transition | `TC-016` | Covered |
| `T2` | Legal: `S3 -> S3` (tap `pool_chip_$token` or non-last `placed_chip_$token`) | State Transition | `TC-016` | Covered |
| `T3` | Legal: `S3 -> S2` (tap last `placed_chip_$token`) | State Transition | `TC-016` | Covered |
| `T4` | Legal: `S3 -> S4` (tap `check_answer_button` when `userTokens == expectedTokens`) | State Transition | `TC-012`, `TC-014` | Covered |
| `T5` | Legal: `S4 -> S2` (`delay(1200)` auto-advance when `currentIndex + 1 < size`) | State Transition | `TC-012` | Covered |
| `T6` | Legal: `S4 -> S1` (`delay(1200)` auto-advance when `currentIndex + 1 == size`) | State Transition | `TC-010` | Covered |
| `T7` | Legal: `S3 -> S5` (tap `check_answer_button` when `userTokens != expectedTokens`) | State Transition | `TC-013`, `TC-015` | Covered |
| `T8` | Legal: `S5 -> S3` (tap `pool_chip_$token` or non-last `placed_chip_$token` after wrong check) | State Transition | `TC-017` | Covered |
| `T9` | Legal: `S5 -> S2` (tap last `placed_chip_$token` after wrong check) | State Transition | `TC-017` | Covered |
| `T10` | Legal: `S2/S3 -> S6` (tap `sentence_hint_button`) | State Transition | `TC-018` | Covered |
| `T11` | Legal: `S6 -> S2/S3` (dismiss dialog / tap `"Понятно"`) | State Transition | `TC-018` | Covered |
| `T12` | Illegal: `S2 -> S4/S5` (tap disabled `check_answer_button` when `placedTokens.isEmpty()`) | State Transition | `TC-011` | Covered |
| `T13` | Illegal: `S1 -> S2/S3` (attempt drilling action in summary/empty state) | State Transition | `TC-019` | Covered |
| `C1` | Session config (`scriptMode × tokensJsonState × distractorJsonState × grammarHintState`) | Pairwise | `TC-012`, `TC-020`, `TC-021`, `TC-022`, `TC-023`, `TC-024` | Covered |
| `U1` | End-to-end sentence construction drill workflow (basic + 4 alt + 2 exception paths) | Use Case | `TC-009`, `TC-012`, `TC-013`, `TC-016`, `TC-018`, `TC-025`, `TC-026` | Covered |
| `G1` | Double-tap / rapid re-submission on `check_answer_button` during `delay(1200)` window | — | — | GAP |
| `G2` | `isLatin == true` with malformed `tokensJson` falling back to Cyrillic `targetSentence` | — | — | GAP |

## 2. Coverage Summary

- **Total modeled elements (`elements_total`):** `27`
- **Gap-blocked / unresolved elements (`elements_gap`):** `2` (`G1`, `G2`, excluded from denominator)
- **Testable denominator (`elements_total - elements_gap`):** `25`
- **Elements covered (`elements_covered`):** `25`
- **Coverage percentage (`coverage_pct`):** `100.0%` (`25 / 25`, with `elements_gap = 2`)
- **Pairwise coverage:** `30 / 30` pairs (`100.0%`, `0` missing) across `6` configurations
- **Decision Table coverage (`R1`):** `5 / 5` reduced columns (`100.0%`)
- **State Transition coverage:** `6 / 6` states, `11 / 11` legal transitions, `2 / 2` illegal transitions (`100.0%`)
- **Use Case coverage (`U1`):** `7 / 7` paths (`100.0%`)

## 3. Gap List

| Gap ID | Unresolved Question | Impact / What It Blocks |
|---|---|---|
| `G1` | Spec does not state whether `check_answer_button` is disabled or debounced while `validationResult == true` during the `delay(1200)` auto-advance coroutine window. | Blocks deterministic concurrency / double-submit test case on `S4`. |
| `G2` | When `isLatin == true` and `tokensJson` is malformed, `setupCurrentSentence` falls back to `targetSentence.split(" ")` (Cyrillic) while validation checks `targetSentenceLatin`. Expected UX/error handling for this mixed-script fallback is unspecified. | Blocks expected-pass validation assertion for `Latin + Malformed tokensJson` (`TC-022` verifies fallback does not crash; pass/fail semantics on mixed script remain unspecified). |

## 4. Reduction Ledger

| Technique | Exhaustive-Equivalent | Post-Reduction | Post-Dedup | De-duplication Notes |
|---|---|---|---|---|
| EP + BVA (`P1, P2, P3`) | 16 | 10 | 10 | `TC-001..TC-010` (`TC-001..002` also carry Error Guessing; `TC-009..010` also cover `S1`, `T6`, `U1` exc. 2) |
| Decision Table (`R1`) | 8 | 5 | 5 | `TC-011..TC-015` (also cover `T4, T5, T7, T12, S3, S4, S5`, `C1` Config 1, `U1` basic & exc. 1) |
| State Transition (`S1..S6`, `T1..T13`) | 13 | 9 | 4 | 5 transitions/states merged into `P3` and `R1` without weakening assertions; `TC-016..TC-019` cover remaining |
| Pairwise (`C1`) | 24 | 6 | 5 | Config 1 merged into `TC-012`; `TC-020..TC-024` cover Configs 2–6 |
| Use Case (`U1`) | 7 | 7 | 2 | 5 paths merged into `P3`, `R1`, `S1..S6`; `TC-025..TC-026` cover speaker audio and early back-navigation |
| Error Guessing | 4 | 4 | 2 | Empty payload & Unicode merged into `TC-001..TC-002`; `TC-027..TC-028` cover SQL/special chars & duplicate tokens |
| **Total** | **72** | **41** | **28** | **61.1% total reduction (`72 -> 28`) with zero assertion weakening** |

```qa-summary
schema_version=1
gate_status=approved
elements_total=27
elements_covered=25
elements_gap=2
coverage_pct=100.0
cases_total=28
cases_p0=6
cases_p1=7
cases_p2=15
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=8
dt_columns_reduced=5
states_total=6
transitions_legal=11
transitions_illegal=2
transitions_illegal_tested=2
usecase_paths=7
usecase_paths_tested=7
configs=6
pairs_target=30
pairs_covered=30
pairs_missing=0
exhaustive_equivalent=72
post_reduction=41
post_dedup=28
```
