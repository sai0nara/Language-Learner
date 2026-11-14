# Curriculum Import & Export — Product Specification

> Stateless Moshi/CSV/XmlPullParser parsing in `CurriculumImporter` paired with Room persistence in `CurriculumRepository` and a Compose UI in `ImportExportScreen`.

Last updated: 2026-11-14

## Overview

Implements the requirements in `Docs/prd/curriculum-import.md` by separating format parsing (`CurriculumImporter`) from database persistence (`CurriculumRepository.importCurriculum` and `CurriculumRepository.clearAndResetToDefaults`) and user interaction (`ImportExportScreen` + `LanguageLearnerViewModel`).

## Architecture

- **Presentation:** `ImportExportScreen` (`Composable`) connected to `LanguageLearnerViewModel`.
- **Domain / Parser:** `CurriculumImporter` singleton returning an immutable `ImportResult`.
- **Data:** `CurriculumRepository` writing entities to `AppDatabase` DAOs on `Dispatchers.IO` using `OnConflictStrategy.REPLACE`.

## Data flow

1. **Input selection:**
   - **SAF File Picker (`select_file_button`):** Launches `ActivityResultContracts.GetContent` with `"*/*"`. When a non-null `Uri` is returned, `ImportExportScreen` sets `isImporting = true`, reads UTF-8 text via `CurriculumImporter.readStream`, auto-detects the format from trimmed content (`"<"` → `"XML"`, `"{"` or `"["` → `"JSON"`, else `"CSV"`), calls `viewModel.importData(content, format)`, updates `statusMessage = result.message`, displays `Toast.makeText(context, result.message, Toast.LENGTH_LONG)`, and resets `isImporting = false`.
   - **Manual Paste (`import_text_input` + `submit_import_text_button`):** User selects a format chip (`JSON`, `CSV`, `XML`), enters non-blank text, and taps submit (enabled when `pastedText.isNotBlank() && !isImporting`). `ImportExportScreen` sets `isImporting = true` (showing a `CircularProgressIndicator`), calls `viewModel.importData(pastedText, selectedFormat)`, updates `statusMessage = result.message`, and resets `isImporting = false`.
   - **Sample Templates (`JSON`, `CSV`, `XML` buttons):** Copies `getSampleJsonTemplate()`, `getSampleCsvTemplate()`, or `getSampleXmlTemplate()` to `LocalClipboardManager`, populates `pastedText`, sets `selectedFormat`, and shows `Toast.LENGTH_SHORT` (`"Шаблон <FORMAT> скопирован"`).
2. **Format routing (`LanguageLearnerViewModel.importData`):** Dispatches by `format.uppercase()` to `CurriculumImporter.parseJson`, `parseCsv`, or `parseXml` (returning `ImportResult(message = "Unsupported format: $format")` for any other format string).
3. **Persistence (`CurriculumRepository.importCurriculum`):** Invoked iff `result.lessons.isNotEmpty() || result.vocabItems.isNotEmpty()`. Upserts entities via Room `@Insert(onConflict = OnConflictStrategy.REPLACE)`:
   - Forces the first imported lesson (`idx == 0`) to `isUnlocked = true`, leaves subsequent lessons in the batch (`idx > 0`) at `isUnlocked = false`, and sets `masteryScore = 0f` for all imported lessons.
   - Inserts `vocabItems`, `grammarGuides` (JSON only), and `sentences`, and seeds an initial `SrsReviewEntity` (`id = v.id`, `repetition = 0`, `intervalDays = 1`, `easeFactor = 2.5f`, `nextReviewTimestamp = System.currentTimeMillis()`, `lastQuality = 0`) for every imported vocabulary item via `SrsDao.insertAll` (`OnConflictStrategy.REPLACE`).
4. **Reset to defaults (`reset_defaults_button`):** Invokes `viewModel.resetToDefaults()` → `CurriculumRepository.clearAndResetToDefaults()` (`db.lessonDao().clearAll()`, cascading deletes to child tables, followed by `ensureInitialDataLoaded()`) and immediately sets `statusMessage = "База сброшена до стандартной программы B1 Сербского языка."`.

## Components

| Component | File | Responsibility |
|---|---|---|
| `ImportExportScreen` | `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` | Format selector, SAF file picker, template loader/copier, raw text input with `isImporting` guard, status card, and reset-to-defaults button. |
| `LanguageLearnerViewModel` | `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` | Routes `importData(content, format)` to `CurriculumImporter` and `CurriculumRepository.importCurriculum`; triggers `resetToDefaults()`. |
| `CurriculumImporter` | `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` | Parses JSON (Moshi), CSV (custom quote-aware tokenizer), and XML (`XmlPullParser`) into `ImportResult`; provides sample templates. |
| `CurriculumRepository` | `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` | Upserts `ImportResult` into Room (`OnConflictStrategy.REPLACE`), unlocks first imported lesson, seeds SRS items, and executes `clearAndResetToDefaults`. |

## Edge cases & failure handling

| Failure mode | Expected behaviour |
|---|---|
| Malformed JSON, CSV, or XML syntax / exception | Parser catches `Exception` and returns empty `ImportResult` with `"JSON parse error: <localizedMessage>"`, `"CSV parse error: <localizedMessage>"`, or `"XML parse error: <localizedMessage>"`; Room DB is untouched. |
| JSON `null` literal or root without `lessons`/`vocab` (`{}`) | `null` returns `"Invalid JSON structure"`; `{}` or `{"lessons":[]}` returns empty `ImportResult` with `"Imported 0 lessons, 0 vocab words, and 0 sentences."` and skips DB persistence. |
| JSON standalone empty `{"vocab":[]}` | Creates 1 synthetic `LessonEntity` (`vocabCount = 0`) and returns `"Imported 0 vocabulary items successfully"`. |
| Blank or whitespace-only CSV | Returns empty `ImportResult` with `"CSV file is empty"` message; Room DB is untouched. |
| CSV delimiter, header, quotes, and column count variations | First non-blank line containing `";"` selects `";"` delimiter for all lines (else `","`); first line containing `"target"` or `"serbian"` (case-insensitive) is skipped as header; `parseCsvLine` toggles `inQuotes` on `"` (preserving embedded delimiters and stripping `"`); rows with blank `targetWord` (col 0) are skipped; missing cols `1..5` use defaults and extra cols `>5` are ignored. |
| XML `<vocab>` or `<sentence>` outside `<lesson>`, or `<grammar>` tag | Elements before the first `<lesson>` are ignored safely (`currentLessonId.isEmpty()`); `<vocab>` with blank `target` is skipped; `<grammar>` and unknown tags are ignored (`grammarGuides` is empty). Empty/self-closing `<curriculum/>` returns 0 counts and skips DB write. |
| Re-importing existing lesson or vocabulary IDs | `OnConflictStrategy.REPLACE` upserts lessons (cascading delete/re-insert of child rows for that `lessonId`) and resets matching `SrsReviewEntity` primary keys to initial SM-2 state without duplicating rows. |
| SAF `openInputStream(uri)` returns `null` or throws `Exception` | Sets `statusMessage = "Не удалось открыть выбранный файл"` (null stream) or `"Ошибка чтения файла: <localizedMessage>"` (exception) and resets `isImporting = false`. |

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
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — `importCurriculum` and `clearAndResetToDefaults`.
- `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` — Compose UI for import/export.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `importData` and `resetToDefaults`.
- `Docs/prd/curriculum-import.md` — requirements this specification implements.
- `Docs/tests/curriculum-import.md` — test cases.
