# Curriculum Import & Export — Product Specification

> Stateless Moshi/CSV/XmlPullParser parsing in `CurriculumImporter` paired with Room persistence in `CurriculumRepository` and a Compose UI in `ImportExportScreen`.

Last updated: 2026-11-13

## Overview

Implements the requirements in `Docs/prd/curriculum-import.md` by separating format parsing (`CurriculumImporter`) from database persistence (`CurriculumRepository.importCurriculum`) and user interaction (`ImportExportScreen`).

## Architecture

- **Presentation:** `ImportExportScreen` (`Composable`) connected to `LanguageLearnerViewModel`.
- **Domain / Parser:** `CurriculumImporter` singleton returning an immutable `ImportResult`.
- **Data:** `CurriculumRepository` writing entities to `AppDatabase` DAOs on `Dispatchers.IO`.

## Data flow

1. User selects a format (`JSON`, `CSV`, or `XML`) in `ImportExportScreen` and either picks a file via Android's Storage Access Framework (`ActivityResultContracts.GetContent`) or pastes raw text and taps submit.
2. `LanguageLearnerViewModel` delegates the raw string to `CurriculumImporter.parseJson`, `parseCsv`, or `parseXml`.
3. `CurriculumImporter` parses the payload into an `ImportResult` containing `lessons`, `vocabItems`, `grammarGuides`, `sentences`, and a human-readable `message`.
4. `CurriculumRepository.importCurriculum` inserts the parsed entities into Room, forces the first imported lesson to `isUnlocked = true`, and seeds `SrsReviewEntity` rows for imported vocabulary.

## Components

| Component | File | Responsibility |
|---|---|---|
| `ImportExportScreen` | `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` | Format selector, SAF file picker, template loader/copier, raw text input, and reset-to-defaults button. |
| `CurriculumImporter` | `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` | Parses JSON (Moshi), CSV (custom quote-aware tokenizer), and XML (`XmlPullParser`) into `ImportResult`. |
| `CurriculumRepository` | `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` | Persists `ImportResult` into Room and seeds SRS items; handles `resetToDefaultCurriculum`. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| Malformed JSON or XML syntax | Parser catches exception and returns empty `ImportResult` with `JSON parse error:` or `XML parse error:` message. |
| Blank or whitespace-only CSV | Returns empty `ImportResult` with `CSV file is empty` message. |
| CSV fields containing embedded commas inside quotes | `parseCsvLine` toggles `inQuotes` state so commas inside `"..."` are preserved. |
| XML `<vocab>` or `<sentence>` outside a `<lesson>` tag | Ignored safely when `currentLessonId` is empty. |

## Dependencies

- Moshi (`com.squareup.moshi`) for JSON parsing and token/distractor list serialization.
- Android `XmlPullParser` (`android.util.Xml`) for XML parsing.
- Room (`androidx.room`) for persisting imported entities.

## Cross-references

- `Docs/prd/curriculum-import.md` — the requirements this specification implements.
- `Docs/tests/curriculum-import.md` — the test cases that verify this design.
- [[data-layer]] — overview of the data and importer layer.

## Sources

- `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` — parser implementation and sample templates.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `importCurriculum` and `resetToDefaultCurriculum`.
- `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` — Compose UI for import/export.
- `Docs/prd/curriculum-import.md` — requirements this specification implements.
- `Docs/tests/curriculum-import.md` — test cases.
