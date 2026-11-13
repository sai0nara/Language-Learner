# Curriculum Progression & Mastery Unlocking — Test Cases

> Maps the curriculum progression and mastery unlocking requirements to automated unit, Room, and Compose UI tests.

Last updated: 2026-11-13

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| JVM unit | `app/src/test/java/com/example/ExampleUnitTest.kt` | Mastery grade formula `(correctCount / 100f) * 5.0f` and 4.5 unlock threshold. |
| Robolectric Room | `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` | Idempotent initial curriculum seeding, 100-answer pruning, and next-lesson unlocking at score `>= 4.5`. |
| Robolectric Compose UI | `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` | `CurriculumScreen` header, tabs, and navigation callbacks. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | Idempotent initial data seeding | Empty in-memory `AppDatabase` | Call `ensureInitialDataLoaded()` twice | Seeds lessons, vocab, grammar, sentences, and SRS items on first call; second call does not duplicate rows. |
| T-2 | Rolling 100-answer pruning | Lesson `l1` exists | Insert 105 answers (first 5 wrong, next 100 right) and call `pruneOldAnswers` | Exactly 100 most recent answers remain, all correct. |
| T-3 | Unlocking next lesson at 4.5 grade | `l1` unlocked, `l2` locked | Record 89 correct answers, verify `l2` locked, then record 90th correct answer | `l1` score reaches `4.5`, `l1.isCompleted` is true, and `l2.isUnlocked` becomes true. |
| T-4 | Curriculum tabs navigation | `CurriculumScreen` composed | Click `tab_srs` -> `start_srs_review_button` and `tab_import` -> `open_importer_button` | Invokes `onOpenSrsReview` and `onOpenImportExport` callbacks. |

## Manual checks

- Visual verification of dark/light theme switching and toast message on tapping a locked lesson card.

## Cross-references

- `Docs/prd/curriculum-progression.md` — the requirements under test.
- `Docs/psd/curriculum-progression.md` — the design the cases verify.
- [[unit-and-ui-tests]] — where the test suite is explained.

## Sources

- `app/src/test/java/com/example/ExampleUnitTest.kt` — grade formula unit test.
- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — Room DAO and repository mastery tests.
- `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` — Compose UI test for `CurriculumScreen`.
- `Docs/prd/curriculum-progression.md` — requirements under test.
- `Docs/psd/curriculum-progression.md` — design the cases verify.
