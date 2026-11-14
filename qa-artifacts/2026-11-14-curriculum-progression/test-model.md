# Test Model — Curriculum Progression & Mastery Unlocking

> **Scope:** `Docs/prd/curriculum-progression.md` & `Docs/psd/curriculum-progression.md` — initial idempotent 6-lesson seeding (`orderIndex` `1..6`), rolling 100-answer mastery calculation, automatic sequential lesson unlocking (including final-lesson no-op and regression below `4.5f`), locked-lesson toast guard, and in-memory script/theme toggles.
> **Gate Status:** `awaiting_approval` (All `G1`–`G7` gaps resolved in PRD/PSD; ready for human review before Stage 3 test suite generation).

---

## 1. Input-Shape to Technique Assignment

| Model Element(s) | Input Shape | Assigned Technique(s) |
|---|---|---|
| `P1`..`P5` | Bounded numeric / boolean parameters | Equivalence Partitioning + Boundary Value Analysis |
| `R1`..`R3` | Compound boolean business logic | Decision Table |
| `S1`..`S3`, `T1`..`T7` | Entity (`LessonEntity`) with lifecycle / status states | State Transition |
| `C1` | Multi-valued UI & state configuration | Pairwise |
| `U1`..`U3` | End-to-end user workflows | Use Case |
| All elements (Stage 3) | Always-on negative overlay | Error Guessing |

---

## 2. Parameters (`P<n>`) — EP & BVA

| ID | Parameter | Type & Stated Range | Valid Equivalence Classes | Invalid Equivalence Classes | Boundaries (3-value: B-1, B, B+1) | Status / Notes |
|---|---|---|---|---|---|---|
| `P1` | `correctCount` (correct answers in rolling window) | `int`, `0..100` inclusive | `VC1`: `0..89` (below mastery, `0.0f..4.45f`)<br>`VC2`: `90..100` (mastered, `4.5f..5.0f`) | `IC1`: `< 0`<br>`IC2`: `> 100` | Lower: `-1, 0, 1`<br>Mastery: `89, 90, 91`<br>Upper: `99, 100, 101` | Ready |
| `P2` | `answerCount` (total `lesson_answers` rows for lesson) | `int`, `0..∞` (rolling cap `100`) | `VC1`: `0` (empty)<br>`VC2`: `1..100` (within cap, no rows deleted by `pruneOldAnswers`)<br>`VC3`: `> 100` (`pruneOldAnswers` retains 100 rows via `ORDER BY timestamp DESC LIMIT 100`; equal timestamps follow SQLite internal rowid/id scan order) | `IC1`: `< 0` | Lower: `-1, 0, 1`<br>Prune cap: `99, 100, 101` | Ready |
| `P3` | `masteryScore` (`(correctCount / 100f) * 5.0f`) | `float`, `0.0f..5.0f` inclusive | `VC1`: `[0.0f, 4.5f)` (not mastered, `isCompleted = false`)<br>`VC2`: `[4.5f, 5.0f]` (mastered, `isCompleted = true`) | `IC1`: `< 0.0f`<br>`IC2`: `> 5.0f` | Mastery threshold: `4.45f, 4.50f, 4.55f` (discrete `0.05f` steps) | Ready |
| `P4` | `isCorrect` (drill/practice answer outcome) | `boolean` | `VC1`: `true`<br>`VC2`: `false` | — (type-enforced) | — | Ready |
| `P5` | `orderIndex` (1-based sequential lesson order) | `int`, `1..6` in default seed curriculum (`99` / `100` for standalone imported JSON/CSV decks) | `VC1`: `1` (Lesson 1, seeded `isUnlocked = true`)<br>`VC2`: `2..5` (seeded `isUnlocked = false`; mastering `orderIndex` unlocks `orderIndex + 1`)<br>`VC3`: `6` (final seeded lesson; mastering marks `isCompleted = true` and `unlockLessonByOrder(7)` is a 0-row no-op) | `IC1`: `<= 0` (no seeded lesson)<br>`IC2`: `> 6` when querying `unlockLessonByOrder(orderIndex + 1)` on final lesson (0 rows updated, no error) | Lower: `0, 1, 2`<br>Upper (seed): `5, 6, 7` | Ready |

---

## 3. Business Rules (`R<n>`) — Decision Tables

### `R1`: Initial Data Seeding (`ensureInitialDataLoaded`)

| Condition / Action | Col 1 | Col 2 |
|---|---|---|
| **C1:** `lessons` table is empty (`existing.isEmpty()`) | `T` | `F` |
| **A1:** Insert `InitialCurriculumData` (6 lessons `orderIndex` `1..6` with Lesson 1 unlocked and Lessons 2–6 locked, plus vocab, grammar, sentences, SRS) | `X` | — |
| **A2:** No-op (preserve existing rows, prevent duplicates) | — | `X` |

- Full columns: `2` | Reduced columns: `2`

### `R2`: Answer Recording, Rolling Pruning & Sequential Unlocking (`recordAnswer`)

| Condition / Action | Col 1 | Col 2 | Col 3 | Col 4 |
|---|---|---|---|---|
| **C1:** `answerCount > 100` after insert | `F` | `F` | `T` | `T` |
| **C2:** Recalculated `masteryScore >= 4.5f` | `F` | `T` | `F` | `T` |
| **A1:** Insert `LessonAnswerEntity` | `X` | `X` | `X` | `X` |
| **A2:** `pruneOldAnswers(lessonId)` deletes rows outside `ORDER BY timestamp DESC LIMIT 100` | — | — | `X` | `X` |
| **A3:** Recalculate `masteryScore` & `updateGradeStats(id, completed = C2, score, correct, total)` | `X` | `X` | `X` | `X` |
| **A4:** `unlockLessonByOrder(current.orderIndex + 1)` (unlocks next lesson, or 0-row no-op on final lesson `orderIndex = 6`) | — | `X` | — | `X` |

- Full columns: `4` | Reduced columns: `4`

### `R3`: Locked Lesson Card Tap (`CurriculumScreen`)

| Condition / Action | Col 1 | Col 2 |
|---|---|---|
| **C1:** `lesson.isUnlocked == true` | `T` | `F` |
| **A1:** Call `viewModel.selectLesson(lesson)` and navigate to `LessonDetailScreen` | `X` | — |
| **A2:** Show `Toast.LENGTH_SHORT` `"Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}"`; stay on `CurriculumScreen` | — | `X` |

- Full columns: `2` | Reduced columns: `2`

---

## 4. States (`S<n>`) & Transitions (`T<n>`) — State Transition

### States (`LessonEntity` progression lifecycle)

| ID | State | Entry / Definition |
|---|---|---|
| `S1` | `Locked` | `isUnlocked = false, isCompleted = false`. Initial state on seed for Lessons 2–6 (`orderIndex` `2..6`); tapping card shows toast and blocks navigation. |
| `S2` | `Unlocked` | `isUnlocked = true, isCompleted = false` (`masteryScore < 4.5f`). Initial state on seed for Lesson 1 (`orderIndex = 1`), entered when `orderIndex - 1` reaches `masteryScore >= 4.5f`, or re-entered when a `Mastered` lesson drops below `4.5f`. |
| `S3` | `Mastered` | `isUnlocked = true, isCompleted = true` (`masteryScore >= 4.5f`, `>= 90/100` correct). |

### Transitions

| ID | From State | Event / Trigger | To State | Type | Notes |
|---|---|---|---|---|---|
| `T1` | `S1` (`Locked`) | Previous lesson (`orderIndex - 1`) reaches `masteryScore >= 4.5f` | `S2` (`Unlocked`) | Legal | Triggered via `unlockLessonByOrder(current.orderIndex + 1)` |
| `T2` | `S2` (`Unlocked`) | Answer recorded -> `masteryScore >= 4.5f` | `S3` (`Mastered`) | Legal | Sets `isCompleted = true` + triggers `T1` on `orderIndex + 1` (or 0-row no-op if `orderIndex = 6`) |
| `T3` | `S2` (`Unlocked`) | Answer recorded -> `masteryScore < 4.5f` | `S2` (`Unlocked`) | Legal (self-loop) | Updates `LessonEntity` stats (`isCompleted = false`) |
| `T4` | `S3` (`Mastered`) | Answer recorded -> `masteryScore >= 4.5f` | `S3` (`Mastered`) | Legal (self-loop) | Remains `isCompleted = true` |
| `T5` | `S1` (`Locked`) | User taps locked lesson card on `CurriculumScreen` | `S1` (`Locked`) | **Illegal** | Shows `"Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}"`; navigation blocked |
| `T6` | `S1` (`Locked`) | Attempt to open drills/practice or jump directly to `Mastered` from UI without `Unlocked` | `S1` (`Locked`) | **Illegal** | Blocked by locked-card guard in `CurriculumScreen` |
| `T7` | `S3` (`Mastered`) | Subsequent wrong answers in rolling 100 window drop `masteryScore < 4.5f` | `S2` (`Unlocked`) | Legal | `updateGradeStats` sets `isCompleted = false`; previously unlocked `orderIndex + 1` remains `isUnlocked = true` |

---

## 5. Configuration Dimensions (`C<n>`) — Pairwise

| ID | Config Name | Dimensions & Discrete Values | Exhaustive Combinations | Pairwise Target | Notes |
|---|---|---|---|---|---|
| `C1` | UI Display & Lesson State Matrix | `isLatinScript`: `{false (Cyrillic default: "Српски језик (B1 • Ћирилица)"), true (Latin: "Srpski jezik (B1 • Latinica)")}`<br>`isDarkTheme`: `{true (Dark default), false (Light)}`<br>`lessonState`: `{Locked, Unlocked, Mastered}` | `2 × 2 × 3 = 12` | `6` configurations (all pairs covered) | In-memory `MutableStateFlow`s; reset to `false` / `true` on cold process restart |

---

## 6. Use Cases (`U<n>`)

| ID | Use Case | Paths |
|---|---|---|
| `U1` | First-Launch Curriculum Initialization & Display | **Basic (`U1.B`):** Empty DB on `ViewModel` init -> `ensureInitialDataLoaded()` seeds 6 lessons (`orderIndex` `1..6`) -> `CurriculumScreen` shows Lesson 1 `Unlocked` (`0.0` badge), Lessons 2–6 `Locked` (lock icon), and floating `"Продолжить: ${active.title}"` button targeting Lesson 1.<br>**Alternative (`U1.A1`):** Non-empty DB on subsequent launch -> `ensureInitialDataLoaded()` skips insert -> existing lessons & scores preserved without duplicates. |
| `U2` | Lesson Practice, Rolling Mastery Update & Sequential Unlocking | **Basic (`U2.B`):** Tap `Unlocked` lesson card or `"Продолжить: ..."` button -> `LessonDetailScreen` (`"Lesson ${lesson.orderIndex}"`) -> `SentencePracticeScreen` or `VocabDrillScreen` -> `recordAnswer(lessonId, isCorrect)` -> `masteryScore >= 4.5f` -> lesson marked `isCompleted = true` & `orderIndex + 1` unlocked (or safe 0-row no-op on Lesson 6).<br>**Alternative 1 (`U2.A1`):** Answer recorded with `answerCount > 100` -> `pruneOldAnswers` deletes rows outside `ORDER BY timestamp DESC LIMIT 100` before recalculating `masteryScore` (if wrong answers drop a `Mastered` lesson below `4.5f`, `isCompleted` reverts to `false` while `orderIndex + 1` stays unlocked).<br>**Alternative 2 (`U2.A2`):** Open `LessonDetailScreen` and launch 3x3 Grammar Matrix (`action_grammar_matrix`).<br>**Exception (`U2.E1`):** Tap `Locked` lesson card on `CurriculumScreen` -> `Toast.LENGTH_SHORT` `"Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}"`; no navigation. |
| `U3` | Script & Theme Toggling | **Basic (`U3.B`):** User toggles `toggle_script_button` (`isLatinScript`: `false` ↔ `true`) or `toggle_theme_button` (`isDarkTheme`: `true` ↔ `false`) in `CurriculumScreen` top bar -> in-memory `StateFlow` updates -> UI re-renders in selected script/theme for the session and resets on cold process restart. |

---

## 7. Gap List (`G<n>`) — Resolved in PRD & PSD

All 7 previously identified gaps (`G1`–`G7`) have been resolved in `Docs/prd/curriculum-progression.md` and `Docs/psd/curriculum-progression.md`:

| ID | Resolution Summary | Status |
|---|---|---|
| `G1` | **Timestamp tie-breaking in `pruneOldAnswers`:** `LessonAnswerDao.pruneOldAnswers` and `getRecentAnswers` use `ORDER BY timestamp DESC LIMIT 100`; equal-timestamp rows follow SQLite's internal rowid/id scan order. | Resolved (FR-2) |
| `G2` | **`orderIndex` base & lesson count:** `orderIndex` is 1-based; `InitialCurriculumData` seeds 6 lessons (`lesson_1`..`lesson_6` with `orderIndex` `1..6`, Lesson 1 unlocked, Lessons 2–6 locked). | Resolved (FR-1, `P5`) |
| `G3` | **Final-lesson mastery behavior:** Mastering the final lesson (`orderIndex = 6`) marks it `isCompleted = true` and executes `unlockLessonByOrder(7)` as a safe 0-row SQL `UPDATE` no-op. | Resolved (FR-3, `P5`, `R2`) |
| `G4` | **Rolling mastery regression below `4.5f`:** `updateGradeStats` sets `isCompleted = score >= 4.5f`, so dropping below `4.5f` transitions the lesson from `S3` (`Mastered`) back to `S2` (`Unlocked`, `T7`), while `orderIndex + 1` remains `isUnlocked = true`. | Resolved (FR-3, `T7`) |
| `G5` | **Script & theme preference persistence/defaults:** `LanguageLearnerViewModel` initializes `isLatinScript = false` (Cyrillic) and `isDarkTheme = true` (Dark) as in-memory `MutableStateFlow`s that reset on cold process restart. | Resolved (FR-5, `C1`, `U3`) |
| `G6` | **Exact locked-lesson toast text & script:** Tapping a locked card shows `Toast.LENGTH_SHORT` `"Урок заблокирован. Наберите оценку 4.5 (90/100) в Уроке ${lesson.orderIndex - 1}"` and card subtitle `"Заблокировано (требуется оценка 4.5 в предыдущем уроке)"`. | Resolved (FR-4, `R3`, `T5`, `U2.E1`) |
| `G7` | **Imported decks progression rules:** Imported lessons use 1-based `orderIndex` (`1..L`, `99`, or `100`), force `idx == 0` in the imported batch to `isUnlocked = true` (`idx > 0` `isUnlocked = false`, `masteryScore = 0f`), and upsert via `OnConflictStrategy.REPLACE` (cascading delete on `lesson_answers` only when an existing `lessonId` is replaced). | Resolved (FR-3) |

---

```qa-summary
schema_version=1
gate_status=approved
elements_total=22
elements_gap=0
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
boundaries_tested=0,1,6,90,100
dt_columns_full=8
dt_columns_reduced=8
states_total=3
transitions_legal=5
transitions_illegal=2
usecase_paths=7
configs=1
```
