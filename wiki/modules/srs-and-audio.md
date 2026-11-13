# SRS and Audio

> SM-2 spaced repetition scheduling and dual-audio pronunciation playback.

Last updated: 2026-11-13

## Spaced Repetition (`srs/`)

`SpacedRepetitionAlgorithm` computes next review intervals, repetition counts, and clamped ease factors from `ReviewGrade` ratings (`AGAIN`, `HARD`, `GOOD`, `EASY`). Failed recalls reset repetition to zero and schedule a 1-day interval.

## Dual Audio (`audio/`)

`AudioController` manages Text-to-Speech and audio playback for target-language words and sentences during drills and reviews.

## Cross-references

- [[data-layer]] — persists `SrsReviewEntity` records via `SrsDao`.
- [[ui-layer]] — `SpacedRepetitionScreen` and drill screens invoke grading and audio playback.
- [[unit-and-ui-tests]] — `SpacedRepetitionAlgorithmTest` verifies SM-2 intervals and bounds.

## Sources

- `app/src/main/java/com/example/srs/SpacedRepetitionAlgorithm.kt` — SM-2 scheduling and `ReviewGrade` mapping.
- `app/src/main/java/com/example/audio/AudioController.kt` — TTS and audio playback controller.

Back to [[overview]]
