# Test Model — Spaced Repetition (SM-2)

**Run:** `2026-11-15-spaced-repetition`
**Scope:** Single functional area — SM-2 scheduling (`SpacedRepetitionAlgorithm`), Room persistence (`SrsDao`, `CurriculumRepository`), and flashcard review UI (`SpacedRepetitionScreen`, `CurriculumScreen` SRS badge/launcher).

---

## 1. Input Parameters (`P<n>`) — EP + BVA

| ID | Parameter | Type & Domain | Valid Classes | Invalid / Clamped Classes | Boundaries (3-value BVA) | Techniques |
|---|---|---|---|---|---|---|
| `P1` | `quality` (`calculateNextReview`) | `Int`, clamped `0..5`, pass threshold `>= 3` | `P1-VC1`: `0..2` (fail); `P1-VC2`: `3..5` (pass) | `P1-IC1`: `< 0` (clamped to `0`); `P1-IC2`: `> 5` (clamped to `5`) | At `0`: `-1, 0, 1`; At `3`: `2, 3, 4`; At `5`: `4, 5, 6` (8 distinct points) | EP, BVA |
| `P2` | `easeFactor` (`calculateNextReview`) | `Float`, floor `1.3f` (`MIN_EASE_FACTOR`), default `2.5f` | `P2-VC1`: `> 1.3f`; `P2-VC2`: `== 1.3f` | `P2-IC1`: `< 1.3f` (floored to `1.3f`) | At `1.3f`: `1.2f, 1.3f, 1.4f` (3 points) | EP, BVA |
| `P3` | `repetition` (`calculateNextReview`) | `Int`, `>= 0`, branches `0`, `1`, `>= 2` | `P3-VC1`: `0`; `P3-VC2`: `1`; `P3-VC3`: `>= 2` | `P3-IC1`: `< 0` (falls into `else` branch) | At `0` & `2`: `-1, 0, 1, 2, 3` (5 points) | EP, BVA |
| `P4` | `nextReviewTimestamp` vs `currentTimestamp` (`SrsDao`) | `Long` (epoch ms), due when `nextReviewTimestamp <= currentTimestamp` | `P4-VC1`: `<= currentTimestamp` (due); `P4-VC2`: `> currentTimestamp` (not due) | — | At `B = currentTimestamp`: `B+1` (not due), `B` (due), `B-1` (overdue) (3 points) | EP, BVA |
| `P5` | `dueCount` (`CurriculumScreen` badge & launcher) | `Int`, `>= 0`, active when `> 0` | `P5-VC1`: `0` (hidden/disabled); `P5-VC2`: `> 0` (badge shown, button enabled) | `P5-IC1`: `< 0` (defensive disabled) | At `0`: `-1, 0, 1` (3 points) | EP, BVA |

---

## 2. Business Rules (`R<n>`) — Decision Tables

### `R1`: SM-2 Interval & Repetition Progression (`dt_columns_full=8`, `dt_columns_reduced=4`)

Conditions:
- `C1`: `clampedQuality >= 3`
- `C2`: `repetition == 0`
- `C3`: `repetition == 1`

| Column | `C1` (`>=3`) | `C2` (`rep==0`) | `C3` (`rep==1`) | Action / Status |
|---|---|---|---|---|
| `1` | `F` | `T` | `T` | Impossible (`C2 && C3`) — collapsed into `R1.1` |
| `2` | `F` | `T` | `F` | Collapsed into `R1.1` |
| `3` | `F` | `F` | `T` | Collapsed into `R1.1` |
| `4` | `F` | `F` | `F` | **`R1.1`** (`F, -, -`): `repetition = 0`, `intervalDays = 1`, `newEaseFactor = max(1.3f, ef_delta)` |
| `5` | `T` | `T` | `T` | Impossible (`repetition` cannot be both `0` and `1`) |
| `6` | `T` | `T` | `F` | **`R1.2`**: `intervalDays = 1`, `repetition = 1` |
| `7` | `T` | `F` | `T` | **`R1.3`**: `intervalDays = 6`, `repetition = 2` |
| `8` | `T` | `F` | `F` | **`R1.4`**: `intervalDays = (intervalDays * newEaseFactor).roundToInt().coerceAtLeast(1)`, `repetition += 1` |

### `R2`: Prompt & Secondary Text Display (`dt_columns_full=4`, `dt_columns_reduced=4`)

Conditions:
- `C1`: `isLatin == true`
- `C2`: `secondaryText.isNotBlank()`

| Reduced Rule | `C1` (`isLatin`) | `C2` (`secondaryText.isNotBlank()`) | `srs_prompt_text` | Secondary Line Below Prompt |
|---|---|---|---|---|
| `R2.1` | `T` | `T` | `currentItem.secondaryText` | Hidden |
| `R2.2` | `T` | `F` | `currentItem.prompt` | Hidden |
| `R2.3` | `F` | `T` | `currentItem.prompt` | `currentItem.secondaryText` shown |
| `R2.4` | `F` | `F` | `currentItem.prompt` | Hidden |

---

## 3. States & Transitions (`S<n>`, `T<n>`) — State Transition

### States (`states_total=3`)
- **`S1` (`Due_Unrevealed`)**: `dueItems.isNotEmpty()`, `currentItem != null`, `isRevealed == false`. Shows `srs_prompt_text`, speaker `IconButton`, `show_answer_button` (`"Показать перевод"`).
- **`S2` (`Due_Revealed`)**: `dueItems.isNotEmpty()`, `currentItem != null`, `isRevealed == true`. Shows `srs_answer_text`, interval/ease summary, and 4 `GradeButton`s (`grade_again`, `grade_hard`, `grade_good`, `grade_easy`).
- **`S3` (`Queue_Empty`)**: `dueItems.isEmpty() || currentItem == null`. Shows CheckCircle icon, `"Очередь чиста!"`, completion message, `srs_done_back_button` (`"Вернуться к урокам"`).

### Transitions (`transitions_legal=6`, `transitions_illegal=2`)
- **`T1` (legal)**: `S1` --[tap `show_answer_button`]--> `S2` (`isRevealed = true`, plays target-language audio)
- **`T2` (legal)**: `S1` --[tap speaker `IconButton`]--> `S1` (plays `promptText` audio, `isRevealed` remains `false`)
- **`T3` (legal)**: `S2` --[tap `GradeButton` with remaining due items]--> `S1` (`recordReview`, `isRevealed = false`, advance/wrap `currentIndex`)
- **`T4` (legal)**: `S2` --[tap `GradeButton` on last due item]--> `S3` (`recordReview`, `dueItems` empty)
- **`T5` (legal)**: `S3` --[tap `srs_done_back_button`]--> Exit SRS screen
- **`T6` (legal)**: `S1` or `S2` --[tap `back_from_srs`]--> Exit SRS screen without grading
- **`T7` (illegal)**: `S1` --[attempt to submit grade while unrevealed]--> Rejected (`GradeButton`s not rendered)
- **`T8` (illegal)**: `S3` --[attempt to reveal or grade on empty queue]--> Rejected (`show_answer_button` / `GradeButton`s not rendered; `start_srs_review_button` disabled)

---

## 4. Configuration Dimensions (`C<n>`) — Pairwise

- **`C1`**: `ReviewGrade` (`AGAIN`, `HARD`, `GOOD`, `EASY`) × `ScriptMode` (`Latin`, `TargetScript`) × `ItemType` (`VOCAB`, `SENTENCE`)
  - Exhaustive: `4 × 2 × 2 = 16`
  - Pairwise (`configs=8`, 50.0% reduction): `pairs_target=20`, `pairs_covered=20`, `pairs_missing=0`:
    1. `(AGAIN, Latin, VOCAB)`
    2. `(AGAIN, TargetScript, SENTENCE)`
    3. `(HARD, Latin, SENTENCE)`
    4. `(HARD, TargetScript, VOCAB)`
    5. `(GOOD, Latin, VOCAB)`
    6. `(GOOD, TargetScript, SENTENCE)`
    7. `(EASY, Latin, SENTENCE)`
    8. `(EASY, TargetScript, VOCAB)`

---

## 5. Use Cases (`U<n>`) — Use Case

- **`U1`** (`usecase_paths=5`): End-to-End SRS Review & Enrollment
  - `U1-BP`: Basic path — badge visible -> launch -> reveal + audio -> grade `GOOD` -> Room update -> `"Очередь чиста!"` -> return.
  - `U1-AP1`: Idempotent enrollment (`CurriculumRepository.enrollInSrs(vocab)` preserves existing SRS record).
  - `U1-AP2`: Speaker `IconButton` audio replay before reveal.
  - `U1-EP1`: Empty queue on launch (`dueCount == 0`, button disabled / `"Очередь чиста!"`).
  - `U1-EP2`: Failing recall (`AGAIN` / `HARD`) resets `repetition = 0`, `intervalDays = 1`, reduces `easeFactor` (`>= 1.3f`).

---

## 6. Error Guessing Overlay (Always-On)

- Empty/blank strings (`prompt`, `answer`, `secondaryText`)
- Multi-byte Unicode (Cyrillic, CJK, emoji, RTL marks)
- SQL injection string (`' OR '1'='1; DROP TABLE srs_reviews;--`)
- Double-submit / rapid double-tap on `GradeButton`
- DST / clock boundary (`23:59:59` <-> `00:00:00`, spring-forward `02:30` epoch ms)

---

## 7. Gaps (`G<n>`)

- **`G1` (GAP — blocks automatic sentence enrollment):** PRD mentions vocabulary and sentence flashcards, and `SrsReviewEntity.itemType` supports `"SENTENCE"`, but `CurriculumRepository` does not implement `"SENTENCE"` auto-enrollment (only `vocabItems`).
- **`G2` (GAP — non-blocking discrepancy):** `ReviewGrade.HARD` displays static `"2d"` (`intervalDescription`), whereas `gradeToQuality(HARD) = 2 < 3` resets `intervalDays = 1` in `calculateNextReview`.
- **`G3` (GAP — non-blocking):** `LanguageLearnerViewModel.dueSrsItems` and `dueSrsCount` evaluate `System.currentTimeMillis()` at flow creation with no stated refresh trigger across midnight/day boundaries.

```qa-summary
schema_version=1
gate_status=approved
elements_total=20
elements_gap=3
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=12
dt_columns_reduced=8
states_total=3
transitions_legal=6
transitions_illegal=2
usecase_paths=5
configs=8
pairs_target=20
pairs_covered=20
pairs_missing=0
```
