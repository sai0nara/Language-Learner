# SRS and Audio

> SM-2 spaced repetition scheduling and dual-audio pronunciation playback.

Last updated: 2026-11-16

## Spaced Repetition (`srs/`)

`SpacedRepetitionAlgorithm` implements FSRS v5-inspired scheduling (`NEW`, `LEARNING`, `REVIEW`, `RELEARNING`, `stability`, `difficulty`, retrievability `R = (1 + (19/81) * (t / S))^-0.5`), implicit accuracy/latency/return rating mapping (`deriveVocabRating`, `deriveSentenceRating`), and `buildDailyBlendedQueue` (20-item `70%` overdue / `30%` active-lesson queue capped at `30`/day), while preserving `calculateNextReview` for legacy unit test compatibility.

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
