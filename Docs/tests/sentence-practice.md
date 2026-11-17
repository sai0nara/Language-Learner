# Sentence Construction Practice — Test Cases

> Verifies inner-case token cleaning, sentence DAO persistence, and mastery score updates from sentence practice answers.

Last updated: 2026-11-17

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| JVM unit | `app/src/test/java/com/example/ExampleUnitTest.kt` | Unicode punctuation stripping and lowercase normalization (`testInnerCaseTokenCleaning`). |
| Robolectric Room | `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` | `SentenceDao` CRUD, cascade deletion, and `CurriculumRepository.recordAnswer` mastery progression. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | Inner-case token normalization | Tokens with Cyrillic uppercase and punctuation (`"Сутра,"`, `"„Књигу“!"`) | Apply `cleanToken` | Outputs `"сутра"` and `"књигу"` with all punctuation removed. |
| T-2 | Sentence persistence & cascade delete | Lesson `l1` with sentence `s1` in `SentenceDao` | Query `getSentencesForLessonSync("l1")`, then delete `l1` | Returns `s1` before delete; cascades deletion when `l1` is removed. |
| T-3 | Mastery score update on sentence check | Active lesson `l1` | Record correct/incorrect answers via `recordAnswer` | Updates `correctAnswersInLast100` and `masteryScore` in `LessonEntity`. |

## Manual checks

- Haptic vibration on incorrect sentence check and help dialog display on device.

## Cross-references

- `Docs/prd/sentence-practice.md` — the requirements under test.
- `Docs/psd/sentence-practice.md` — the design the cases verify.
- [[unit-and-ui-tests]] — test suite overview.

## Sources

- `app/src/test/java/com/example/ExampleUnitTest.kt` — token normalization unit test.
- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — `SentenceDao` and `recordAnswer` tests.
- `Docs/prd/sentence-practice.md` — requirements under test.
- `Docs/psd/sentence-practice.md` — design the cases verify.
