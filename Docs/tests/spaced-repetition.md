# Spaced Repetition (SM-2) — Test Cases

> Verifies SM-2 interval progression, ease factor floor, lapse resets, quality clamping, and `SrsDao` due-item persistence.

Last updated: 2026-11-17

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| JVM unit | `app/src/test/java/com/example/srs/SpacedRepetitionAlgorithmTest.kt` | `gradeToQuality`, 1d/6d/ease-multiplied intervals, lapse resets, `1.3f` ease floor, `0..5` clamping, and future timestamp scheduling. |
| Robolectric Room | `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` | `SrsDao` due-item filtering, due counts, and `CurriculumRepository.recordSrsResult`. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | SM-2 interval progression | New `SrsReviewEntity` (`repetition = 0`, `easeFactor = 2.5f`) | Review with `quality = 4`, then `4`, then `5` | Intervals progress `1` -> `6` -> `16` days; `repetition` increments `1` -> `2` -> `3`. |
| T-2 | Lapse reset on quality < 3 | Established card (`repetition = 4`, `intervalDays = 30`) | Review with `quality` in `0, 1, 2` | `repetition` resets to `0` and `intervalDays` resets to `1`. |
| T-3 | Ease factor minimum bound | Card with `easeFactor = 1.5f` | Apply 10 consecutive `quality = 0` reviews | `easeFactor` never drops below `1.3f`. |
| T-4 | Due item filtering in `SrsDao` | 1 past-due card and 1 future card in `srs_reviews` | Query `getDueItems(now)` and `getDueCount(now)` | Returns only the past-due card and count `1`. |

## Manual checks

- Flashcard reveal animation and grading button flow in `SpacedRepetitionScreen`.

## Cross-references

- `Docs/prd/spaced-repetition.md` — the requirements under test.
- `Docs/psd/spaced-repetition.md` — the design the cases verify.
- [[unit-and-ui-tests]] — test suite overview.

## Sources

- `app/src/test/java/com/example/srs/SpacedRepetitionAlgorithmTest.kt` — SM-2 unit test suite.
- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — `SrsDao` and `recordSrsResult` integration tests.
- `Docs/prd/spaced-repetition.md` — requirements under test.
- `Docs/psd/spaced-repetition.md` — design the cases verify.
