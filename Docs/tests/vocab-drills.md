# Vocabulary Matching Drills — Test Cases

> Verifies `VocabDao` persistence, cascade deletion, SRS enrollment, and mastery recording for vocabulary drills.

Last updated: 2026-11-17

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| Robolectric Room | `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` | `VocabDao` CRUD, lesson cascade delete, `CurriculumRepository.enrollInSrs`, and `recordAnswer`. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | Vocab persistence & cascade delete | Lesson `l1` with `VocabItemEntity` `v1` | Query `getVocabForLessonSync("l1")`, then delete `l1` | Returns `v1` before delete; empty list after `l1` is deleted. |
| T-2 | Idempotent SRS enrollment from drill | `VocabItemEntity` `v1` | Call `repository.enrollInSrs(v1)`, record an SRS review, then call `enrollInSrs(v1)` again | Creates `SrsReviewEntity` on first call; second call preserves existing review progress without overwriting. |

## Manual checks

- Automatic TTS pronunciation on card display and missed-card re-appearance at the end of a drill session.

## Cross-references

- `Docs/prd/vocab-drills.md` — the requirements under test.
- `Docs/psd/vocab-drills.md` — the design the cases verify.
- [[unit-and-ui-tests]] — test suite overview.

## Sources

- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — `VocabDao` and `enrollInSrs` tests.
- `Docs/prd/vocab-drills.md` — requirements under test.
- `Docs/psd/vocab-drills.md` — design the cases verify.
