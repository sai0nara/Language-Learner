# Curriculum Import & Export — Test Cases

> Maps the PRD's acceptance criteria and the PSD's edge cases to concrete verification.

Last updated: 2026-11-13

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| Robolectric unit | `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` | JSON, CSV, and XML parsing, templates, quoted CSV fields, orphan XML tags, malformed inputs, and `readStream`. |
| Robolectric Room | `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` | `CurriculumRepository.importCurriculum` unlocking the first lesson and creating SRS cards. |
| Robolectric Compose UI | `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` | `ImportExportScreen` controls display and back navigation. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | Full JSON curriculum import | Sample JSON template | Call `CurriculumImporter.parseJson` | Returns lessons, vocab, grammar guides, and sentences. |
| T-2 | Standalone JSON vocab import | JSON with top-level `vocab` array | Call `CurriculumImporter.parseJson` | Wraps vocab items in an `"Imported Vocabulary"` lesson. |
| T-3 | Malformed JSON fallback | Invalid JSON string | Call `CurriculumImporter.parseJson` | Returns empty lists and `JSON parse error:` message. |
| T-4 | Quoted & semicolon CSV import | CSV with quoted commas or `;` delimiter | Call `CurriculumImporter.parseCsv` | Preserves commas inside quotes and splits fields accurately. |
| T-5 | XML curriculum & error handling | Sample XML template and malformed XML | Call `CurriculumImporter.parseXml` | Parses valid `<lesson>`, `<vocab>`, `<sentence>`; ignores orphan `<vocab>` and catches malformed XML. |
| T-6 | Repository curriculum persistence | `ImportResult` with 2 locked lessons and 1 vocab | Call `repository.importCurriculum` | First lesson unlocked, second remains locked, vocab and SRS item persisted. |

## Manual checks

- Picking a `.json`, `.csv`, or `.xml` file via the system SAF picker on an Android device.

## Cross-references

- `Docs/prd/curriculum-import.md` — the requirements under test.
- `Docs/psd/curriculum-import.md` — the design the cases verify.
- [[unit-and-ui-tests]] — where the test suite is explained.

## Sources

- `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` — importer unit coverage.
- `app/src/test/java/com/example/data/local/RoomPersistenceAndRepositoryTest.kt` — repository import coverage.
- `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` — UI coverage for `ImportExportScreen`.
- `Docs/prd/curriculum-import.md` — requirements under test.
- `Docs/psd/curriculum-import.md` — design the cases verify.
