# Test Model — `vocab-drills`

**Scope:** `VocabDrillScreen` (`app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt`) and `CurriculumRepository.recordAnswer` (`app/src/main/java/com/example/data/repository/CurriculumRepository.kt`)  
**Source:** Pasted PRD (`Docs/prd/vocab-drills.md`) + Resolved Gaps summary  
**Gate Status:** `approved`

> [!NOTE]
> Per the Resolved Gaps context, `VocabDrillScreen.kt` differs from the PRD in two areas:
> 1. **FR-2:** Builds `(listOf(sourceTranslation) + distractors.take(5)).shuffled()` — up to **6 choices** (`1 + 5`), with **no** sibling-vocab/default fallback and **no** `.distinct()`.
> 2. **FR-3:** Does **not** play `sourceTranslation` audio on correct answer and does **not** call `CurriculumRepository.enrollInSrs` during the drill.
> All elements below model the resolved behavior directly.

---

## 1. Input Parameters (`P<n>`) — EP + BVA

| ID | Parameter | Type & Domain | Valid Equivalence Classes | Invalid / Edge Equivalence Classes | Boundaries | Techniques |
|---|---|---|---|---|---|---|
| `P1` | `rawVocabList.size` | `Int`, `>= 0` (Room `Flow<List<VocabItemEntity>>`) | `size == 1` (single item), `size >= 2` (multi-item) | `size == 0` (empty lesson → immediate completion view, `initialTotal = 0`) | `0`, `1`, `2` | EP, BVA |
| `P2` | `distractorsJson` | `String` (JSON array of strings) | Valid `"[]"` (`0` distractors → 1 option); valid `1..5` distractors (`2..6` options); valid `> 5` distractors (truncated via `.take(5)` to 6 options) | Malformed JSON (`"{bad"`, `"null"`, non-array) → caught to `emptyList()`; duplicate distractor matching `sourceTranslation` (no `.distinct()`) | `0`, `1`, `4`, `5`, `6` | EP, BVA, Error Guessing |
| `P3` | `partOfSpeech` & `exampleSentence` | `String` (optional metadata) | Both non-blank (`partOfSpeech` shown; `exampleSentence` shown as `"„…“"`); one blank / one non-blank | Both empty `""` or whitespace-only `"   "` (`isNotBlank() == false` → hidden) | — | EP, Error Guessing |
| `P4` | `correctCount / initialTotal` | `Float` progress `0f..1f` | `initialTotal > 0` with `0 <= correctCount <= initialTotal` | `initialTotal == 0` → `0f` (`"0.0% (0 / 0)"`); `correctCount > initialTotal` → clamped to `1f` (`"100.0%"`) | `0`, `1`, `initialTotal-1`, `initialTotal`, `initialTotal+1` | EP, BVA |
| `P5` | `lesson.score` mastery (`recordAnswer`) | `Int` correct count in latest 100 `LessonAnswerEntity` rows | `0..89` (`score < 4.5f` → not completed); `90..100` (`score >= 4.5f` → completed & `orderIndex + 1` unlocked) | `> 100` total answers (pruned to latest 100 rows) | `89` (`4.45f`), `90` (`4.50f`), `91` (`4.55f`) | EP, BVA |

---

## 2. Business Rules (`R<n>`) — Decision Table

### `R1`: Option Click Evaluation (`VocabDrillScreen.kt`)
- **Conditions (n = 3, full = $2^3 = 8$ columns):**
  - `C1`: `selectedOption == null` (card clickable)
  - `C2`: `optionText == currentItem.sourceTranslation` (correct choice)
  - `C3`: `Build.VERSION.SDK_INT >= Build.VERSION_CODES.O` (haptic API 26+)
- **Collapse:** When `C1 = False`, `C2` and `C3` are don't-care (`—`) because clicks are ignored (4 columns collapse to 1). **Reduced = 5 columns.**

| Rule Column | `C1` (`selectedOption == null`) | `C2` (Correct) | `C3` (`API >= 26`) | Actions |
|---|---|---|---|---|
| `R1-Col1` | `T` | `T` | `T` | Lock (`selectedOption`), `isCorrectState=true`, `correctCount++`, `recordAnswer(true)`, 40ms vibrate, `SuccessEmerald`, 500ms → advance/complete |
| `R1-Col2` | `T` | `T` | `F` | Same as `R1-Col1`, **no** vibrate |
| `R1-Col3` | `T` | `F` | `T` | Lock (`selectedOption`), `isCorrectState=false`, `recordAnswer(false)`, 120ms vibrate, `ErrorCoral` + `SuccessEmerald(0.8f)`, re-append `currentItem`, 800ms → advance |
| `R1-Col4` | `T` | `F` | `F` | Same as `R1-Col3`, **no** vibrate |
| `R1-Col5` | `F` | `—` | `—` | Ignore click; no state, queue, haptic, or repository side effects |

### `R2`: Option Grid Row Chunking (`options.chunked(2)`)
- **Conditions (n = 1, full = 2, reduced = 2):**
  - `R2-Col1`: `options.size % 2 == 0` (2, 4, 6) → full 2-column rows, no trailing `Spacer`
  - `R2-Col2`: `options.size % 2 == 1` (1, 3, 5) → final row has 1 card + `Spacer(Modifier.weight(1f))`

---

## 3. States & Transitions (`S<n>`, `T<n>`) — State Transition

### States (`S1..S5`)
- `S1`: `Empty` — `rawVocabList.isEmpty()`, `currentItem == null`
- `S2`: `AwaitingAnswer` — `!isCompleted`, `selectedOption == null`, `currentItem != null`
- `S3`: `CorrectFeedback` — `selectedOption != null`, `isCorrectState == true` (500ms delay active)
- `S4`: `IncorrectFeedback` — `selectedOption != null`, `isCorrectState == false` (800ms delay active)
- `S5`: `Completed` — `isCompleted == true` (completion view + `restart_drill_button`)

### Transitions (`T1..T11`)
| ID | From | Event | To | Legality |
|---|---|---|---|---|
| `T1` | `S1 (Empty)` | Initial render with empty `rawVocabList` | `S5 (Completed)` | Legal |
| `T2` | `S2 (AwaitingAnswer)` | Tap `vocab_pronounce_button` | `S2 (AwaitingAnswer)` | Legal |
| `T3` | `S2 (AwaitingAnswer)` | Tap correct option card | `S3 (CorrectFeedback)` | Legal |
| `T4` | `S2 (AwaitingAnswer)` | Tap incorrect option card | `S4 (IncorrectFeedback)` | Legal |
| `T5` | `S3 (CorrectFeedback)` | 500ms elapsed (`currentIndex + 1 < drillQueue.size`) | `S2 (AwaitingAnswer)` | Legal |
| `T6` | `S3 (CorrectFeedback)` | 500ms elapsed (`currentIndex + 1 == drillQueue.size`) | `S5 (Completed)` | Legal |
| `T7` | `S4 (IncorrectFeedback)` | 800ms elapsed (`currentItem` re-appended) | `S2 (AwaitingAnswer)` | Legal |
| `T8` | `S5 (Completed)` | Tap `restart_drill_button` (`rawVocabList.isNotEmpty()`) | `S2 (AwaitingAnswer)` | Legal |
| `T9` | `S3 (CorrectFeedback)` | Tap any option card while `selectedOption != null` | `S3` (ignored) | **Illegal** |
| `T10` | `S4 (IncorrectFeedback)` | Tap any option card while `selectedOption != null` | `S4` (ignored) | **Illegal** |
| `T11` | `S5 (Completed)` | Tap `restart_drill_button` when `rawVocabList.isEmpty()` | `S5` (remains empty) | **Illegal** |

---

## 4. Configuration Matrix (`C1`) — Pairwise

- **Dimensions (descending value-count):**
  1. `distractorCount`: `0`, `3` (`1..5`), `6` (`>5`)
  2. `isLatinScript`: `true`, `false`
  3. `buildSdk`: `API>=26`, `API<26`
  4. `optionalMetadata`: `both_present`, `both_blank`
- **Exhaustive:** $3 \times 2 \times 2 \times 2 = 24$. **Pairwise (6 configs, 30/30 pairs covered, 0 missing, 75.0% reduction):**
  1. `(0, true, API>=26, both_present)`
  2. `(0, false, API<26, both_blank)`
  3. `(3, true, API<26, both_blank)`
  4. `(3, false, API>=26, both_present)`
  5. `(6, true, API>=26, both_blank)`
  6. `(6, false, API<26, both_present)`

---

## 5. Use Cases (`U1`) — Use Case

- `U1-Basic`: Load non-empty lesson → auto-pronounce target word → answer all items correctly → reach `100.0% (N / N)` completion view.
- `U1-Alt1`: Replay target word audio via `vocab_pronounce_button` with `isLatinScript = true`.
- `U1-Alt2`: Answer an item incorrectly → re-queued to end of `drillQueue` → later answered correctly → drill completes.
- `U1-Exc1`: Empty lesson (`rawVocabList.isEmpty()`) → immediate completion view (`0.0% (0 / 0)`).
- `U1-Exc2`: Malformed `distractorsJson` → caught exception → single `sourceTranslation` option rendered.

---

## 6. Gaps (`G<n>`)

None. All ambiguities from `Docs/prd/vocab-drills.md` are resolved in the `RESOLVED GAPS` input (`elements_gap=0`).

```qa-summary
schema_version=1
gate_status=approved
elements_total=25
elements_gap=0
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=10
dt_columns_reduced=7
states_total=5
transitions_legal=8
transitions_illegal=3
usecase_paths=5
configs=6
pairs_target=30
pairs_covered=30
pairs_missing=0
```
