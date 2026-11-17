# Vocabulary Matching Drills — Product Specification

> Stateful Compose drill queue in `VocabDrillScreen` backed by `VocabDao`, Moshi distractor lists, `AudioController`, and `CurriculumRepository`.

Last updated: 2026-11-17

## Overview

Implements `Docs/prd/vocab-drills.md` by maintaining a mutable `drillQueue` in `VocabDrillScreen` that grows when a learner misses an item, ensuring 100% completion within a session.

## Architecture

- **Presentation:** `VocabDrillScreen` observes `viewModel.activeVocab` and renders Latin target words (`targetWordLatin.ifBlank { targetWord }`).
- **Data:** `VocabDao` provides per-lesson `VocabItemEntity` lists; `CurriculumRepository` handles `recordAnswer` and `enrollInSrs`.

## Data flow

1. `LaunchedEffect(rawVocabList)` populates `drillQueue` with `rawVocabList.shuffled()`.
2. `LaunchedEffect(currentItem)` calls `viewModel.playAudio` to speak the target word in the active script (`targetWord` or `targetWordLatin`).
3. Candidate options are constructed by combining `currentItem.sourceTranslation` with up to 3 distractors parsed from `currentItem.distractorsJson` (or sampled from sibling vocabulary translations) and shuffling.
4. When the user taps an option, `VocabDrillScreen` sets `selectedOption` and `isCorrectState`, calls `viewModel.recordLessonAnswer(correct)`, and either enrolls the item in SRS (on correct) or appends `currentItem` to `drillQueue` (on wrong) before advancing after a brief delay.

## Components

| Component | File | Responsibility |
|---|---|---|
| `VocabDrillScreen` | `app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt` | Renders prompt card, option buttons, progress bar, completion screen, and re-queue logic. |
| `VocabDao` | `app/src/main/java/com/example/data/local/VocabDao.kt` | Queries and inserts `VocabItemEntity` records. |
| `CurriculumRepository` | `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` | Records lesson answers and enrolls mastered vocabulary in `srs_reviews`. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| `distractorsJson` is empty or invalid JSON | Falls back to sampling translations from other items in `rawVocabList` or default Russian verb distractors. |
| Lesson has fewer than 4 vocabulary items | `distinct()` option list renders the available unique choices without duplicating options. |

## Dependencies

- Moshi for parsing `distractorsJson`.
- Android `Vibrator` service for error haptics.

## Cross-references

- `Docs/prd/vocab-drills.md` — the requirements this specification implements.
- `Docs/tests/vocab-drills.md` — the test cases that verify this design.
- [[ui-layer]] — UI layer overview.

## Sources

- `app/src/main/java/com/example/ui/screens/VocabDrillScreen.kt` — drill screen implementation.
- `app/src/main/java/com/example/data/local/VocabDao.kt` — vocabulary DAO queries.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `enrollInSrs` and `recordAnswer`.
- `Docs/prd/vocab-drills.md` — requirements this specification implements.
- `Docs/tests/vocab-drills.md` — test cases.
