# 3x3 Grammar Matrices & Guides — Test Cases

> Verifies `GrammarGuideEntity` JSON import, Room persistence, and cascade deletion.

Last updated: 2026-11-13

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| Robolectric unit | `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` | Parsing `grammar` and `matrix` JSON into `GrammarGuideEntity`. |
| Robolectric Room | `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` | `GrammarDao` insert, lookup by `lessonId`, and cascade delete when parent lesson is deleted. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | Import grammar guide with 3x3 matrix | Sample JSON template with `grammar.matrix` | Call `CurriculumImporter.parseJson` | Produces `GrammarGuideEntity` with serialized `matrixJson` and matching `lessonId`. |
| T-2 | GrammarDao persistence & cascade delete | Lesson `l1` and `GrammarGuideEntity` `g1` | Insert `g1`, query `getGrammarGuideForLessonSync("l1")`, then delete `l1` | Returns `g1` before delete; returns `null` after `l1` is deleted. |

## Manual checks

- Tapping each of the 9 cells in `GrammarMatrixScreen` and verifying the detail card and audio playback.

## Cross-references

- `Docs/prd/grammar-matrices.md` — the requirements under test.
- `Docs/psd/grammar-matrices.md` — the design the cases verify.
- [[unit-and-ui-tests]] — test suite overview.

## Sources

- `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` — JSON grammar matrix import test.
- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — `GrammarDao` persistence test.
- `Docs/prd/grammar-matrices.md` — requirements under test.
- `Docs/psd/grammar-matrices.md` — design the cases verify.
