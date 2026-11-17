# Curriculum Import & Export — Product Requirements

> Enables self-directed learners to import custom XML, CSV, and JSON curricula offline and export sample templates.

Last updated: 2026-11-17

## Overview

Self-directed learners need full control over their study materials without relying on cloud servers. The import/export feature in `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt`, `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt`, `app/src/main/java/com/example/data/importer/CurriculumImporter.kt`, and `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` lets users load custom lessons, vocabulary decks, 3x3 grammar matrices, and sentence drills from files or clipboard text, or reset to the built-in seed curriculum.

## Goals / Non-goals

**Goals**

- Parse custom curricula in JSON, CSV, and XML formats completely offline.
- Provide one-tap sample templates for JSON, CSV, and XML so users can author compatible decks.
- Upsert imported lessons, vocabulary, grammar guides, and sentences into Room and automatically enroll imported vocabulary into SRS.
- Allow resetting the local database back to the default offline curriculum.

**Non-goals**

- Cloud-hosted deck marketplaces or mandatory account synchronization.
- Artificial file-size, character-length, or item-count limits beyond platform memory constraints.

## User stories

- As a self-directed learner, I want to import a JSON, CSV, or XML file from device storage or pasted text so that I can study my own textbook or tutor materials offline.
- As a curriculum author, I want to load and copy a sample JSON/CSV/XML template in the app so that I know the exact schema expected by the importer.
- As a learner, I want to reset the curriculum to the default seed data if I want a fresh start.

## Functional requirements

- **FR-1 (JSON import modes & schema):** Support importing JSON root objects (`Map<String, Any>`) in two modes:
  1. **Full `lessons` array mode:** When root key `lessons` is present, parse each lesson object (`id` defaulting to `"lesson_<timestamp>_<index>"`, `title` defaulting to `"Custom Lesson <index+1>"`, `subtitle` defaulting to `""`, `grammarFocus` defaulting to `"General"`, `level` defaulting to `"B1"`, sequential `orderIndex = index + 1`) along with nested `vocab` (`targetWord`, `targetWordLatin`, `sourceTranslation`, `partOfSpeech` defaulting to `"Word"`, `distractors` string list, `audioUri`; `id = "vocab_<lessonId>_<vIdx>"`), optional nested `grammar` (`title` defaulting to lesson `title`, `explanation`, and `matrix` object serialized as `matrixJson` or `"{}"` if absent; `id = "grammar_<lessonId>"`, `summary = subtitle`), and nested `sentences` (`sourceSentence`, `targetSentence`, `targetSentenceLatin`, `tokens` defaulting to space-split `targetSentence`, `distractors`, `hint`, `audioUri`; `id = "sent_<lessonId>_<sIdx>"`). Return message `"Imported <L> lessons, <V> vocab words, and <S> sentences."`.
  2. **Standalone `vocab` array mode:** When `lessons` is absent and root key `vocab` is present, parse each vocabulary object (`targetWord`, `targetWordLatin`, `sourceTranslation`, `partOfSpeech` defaulting to `"Word"`, `audioUri`; `id = "vocab_<defaultLessonId>_<idx>"`) into a single synthetic `LessonEntity` (`id = "imported_vocab_<timestamp>"`, `orderIndex = 99`, `title = "Imported Vocabulary"`, `subtitle = "Imported via JSON"`, `grammarFocus = "Vocabulary"`, `level = "Custom"`, `vocabCount = <V>`, `sentenceCount = 0`). Return message `"Imported <V> vocabulary items successfully"`.
  3. **Empty / missing keys:** A valid JSON object lacking both `lessons` and `vocab` (e.g., `{}`) or having `"lessons": []` returns an empty `ImportResult` with `"Imported 0 lessons, 0 vocab words, and 0 sentences."` and performs no database write. A JSON `null` literal returns `"Invalid JSON structure"`.
- **FR-2 (CSV import & tokenization):** Support importing comma- and semicolon-delimited CSV vocabulary lists:
  - Filter out blank/whitespace-only lines; if none remain, return an empty `ImportResult` with message `"CSV file is empty"` and do not modify Room.
  - Detect the file-wide delimiter from the first non-blank line (`";"` if that line contains `";"`, else `","`).
  - Skip the first non-blank line as a header row iff its lowercased text contains `"target"` or `"serbian"`.
  - Tokenize each data line with quote-toggling on `"` characters (preserving delimiters inside quotes and stripping `"` characters; unclosed quotes extend to end-of-line) and trim each token.
  - Map up to 6 positional columns per row (`0: targetWord`, `1: targetWordLatin = ""`, `2: sourceTranslation = ""`, `3: partOfSpeech = "Word"`, `4: exampleSentence = ""`, `5: audioUri = ""`; extra columns ignored). Rows with missing or blank `targetWord` are skipped.
  - Wrap parsed items (`id = "csv_vocab_<idx>"`) in a single synthetic `LessonEntity` (`id = "csv_lesson_<timestamp>"`, `orderIndex = 100`, `title = "Imported CSV Deck"`, `subtitle = "Imported <V> words"`, `grammarFocus = "Lexical Study"`, `level = "Custom"`, `vocabCount = <V>`, `sentenceCount = 0`) and return `"Imported CSV deck with <V> words."`.
- **FR-3 (XML import):** Support importing XML documents via `XmlPullParser`:
  - Parse `<lesson>` elements (attributes `id` defaulting to `"xml_l_<timestamp>"`, `title` defaulting to `"Lesson <order>"`, `grammarFocus` defaulting to `"Grammar"`, `level` defaulting to `"B1"`, sequential `orderIndex` starting at `1`, `subtitle = "Imported XML Lesson"`).
  - Parse `<vocab>` elements inside a lesson (attributes `target` [skipped if blank], `latin = ""`, `translation = ""`, `pos = "Word"`, `audio = ""`; `id = "xml_v_<idx>"`).
  - Parse `<sentence>` elements inside a lesson (attributes `source = ""`, `target = ""`, `latin = ""`, `tokens` [comma-separated, defaulting to `target`], `distractors` [comma-separated, blank tokens filtered], `hint = ""`, `audio = ""`; `id = "xml_s_<idx>"`).
  - Ignore `<vocab>` or `<sentence>` elements appearing before any `<lesson>` tag (`currentLessonId` is empty), and ignore unsupported tags such as `<grammar>` (`grammarGuides` is always empty for XML).
  - Return `"Imported XML with <L> lessons, <V> words, <S> sentences."` (if `<L> == 0` and `<V> == 0`, no database write occurs).
- **FR-4 (Room upsert, lesson unlocking, and SRS seeding):** When `ImportResult.lessons` or `ImportResult.vocabItems` is non-empty, `CurriculumRepository.importCurriculum` persists entities on `Dispatchers.IO` using `@Insert(onConflict = OnConflictStrategy.REPLACE)`:
  - Existing curriculum entities with non-colliding IDs remain intact (merge/upsert by primary key; replacing an existing `LessonEntity` ID cascades deletion to its prior child rows via foreign keys).
  - The first lesson in the imported batch (`idx == 0`) is forced to `isUnlocked = true`, while all subsequent lessons in the batch (`idx > 0`) retain `isUnlocked = false`, and all imported lessons have `masteryScore = 0f`.
  - Every imported `VocabItemEntity` seeds an `SrsReviewEntity` (`id = v.id`, `itemType = "VOCAB"`, `repetition = 0`, `intervalDays = 1`, `easeFactor = 2.5f`, `nextReviewTimestamp = now`, `lastQuality = 0`) via `OnConflictStrategy.REPLACE`, resetting any existing SRS card sharing the same primary key without creating duplicate rows.
- **FR-5 (Error handling & UI feedback):**
  - Malformed JSON, CSV, or XML exceptions are caught and return an empty `ImportResult` with `"JSON parse error: <localizedMessage>"`, `"CSV parse error: <localizedMessage>"`, or `"XML parse error: <localizedMessage>"` without modifying Room. An unrecognized format string in `LanguageLearnerViewModel.importData` returns `"Unsupported format: <format>"`.
  - SAF file picker (`ActivityResultContracts.GetContent` with `"*/*"` filter) auto-detects format from trimmed file content (`"<"` → `"XML"`, `"{"` or `"["` → `"JSON"`, else `"CSV"`), displays the result `message` in both the on-screen status card and a `Toast.LENGTH_LONG`, sets `statusMessage = "Не удалось открыть выбранный файл"` if `openInputStream` returns `null`, sets `statusMessage = "Ошибка чтения файла: <localizedMessage>"` on I/O exceptions, and is a no-op when cancelled (`uri == null`).
  - Manual paste submit button (`submit_import_text_button`) is enabled only when `pastedText.isNotBlank() && !isImporting`, showing a `CircularProgressIndicator` while `isImporting` is true to prevent double-submission.
  - Sample template buttons (`JSON`, `CSV`, `XML`) copy the corresponding template from `CurriculumImporter` to the clipboard, populate `pastedText`, switch `selectedFormat`, and show a `Toast.LENGTH_SHORT` (`"Шаблон JSON скопирован"`, `"Шаблон CSV скопирован"`, `"Шаблон XML скопирован"`).
  - Tapping the reset button (`reset_defaults_button`) immediately invokes `viewModel.resetToDefaults()` (`db.lessonDao().clearAll()` followed by `ensureInitialDataLoaded()`, without a confirmation dialog) and sets `statusMessage = "База сброшена до стандартной программы B1 Сербского языка."`.

## Non-functional requirements

- **Offline-first:** All file reading, parsing, and database persistence execute locally without network access.
- **Resilience:** Malformed inputs never throw uncaught exceptions on the UI thread.

## Acceptance criteria

The product specification (`Docs/psd/curriculum-import.md`) defines how these requirements are met; the test cases (`Docs/tests/curriculum-import.md`) verify them.

## Out of scope

- Remote URL fetching or cloud sync of imported decks.
- XML or CSV import of 3x3 grammar guides (supported in JSON only).

## Cross-references

- `Docs/psd/curriculum-import.md` — the specification that implements these requirements.
- `Docs/tests/curriculum-import.md` — the test cases that verify them.
- [[data-layer]] — where the importer and repository persistence are explained.

## Sources

- `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` — JSON, CSV, and XML parsers and sample templates.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — persistence of imported curricula, reset to defaults, and SRS card creation.
- `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` — import/export UI screen, template actions, reset button, and SAF file picker integration.
- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — `importData` format routing and `resetToDefaults` orchestration.
- `Docs/psd/curriculum-import.md` — product specification.
- `Docs/tests/curriculum-import.md` — test cases.
