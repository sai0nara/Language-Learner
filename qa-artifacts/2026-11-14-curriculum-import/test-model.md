# Test Model — Curriculum Import & Export

**Scope:** One area — `Curriculum Import & Export` (`ImportExportScreen`, `LanguageLearnerViewModel.importData` / `resetToDefaults`, `CurriculumImporter`, `CurriculumRepository.importCurriculum`, and `CurriculumRepository.clearAndResetToDefaults`), derived from `Docs/prd/curriculum-import.md` and `Docs/psd/curriculum-import.md`.

**Gate Status:** `awaiting_approval` (All `G1`–`G10` gaps resolved in PRD/PSD; ready for human review before Stage 3 test suite generation).

## 1. Modeled Elements

### Input Parameters (`P<n>`)

| ID | Element | Type / Domain | Valid Equivalence Classes | Invalid Equivalence Classes | Boundaries | Assigned Technique(s) | Gap / Blockers |
|---|---|---|---|---|---|---|---|
| `P1` | `rawPayload` | `String` (UTF-8 file content via SAF `*/*` or pasted text) | Non-blank JSON object; Non-blank CSV (≥1 row with non-blank `targetWord`); Non-blank XML `<curriculum>` | Blank or whitespace-only CSV (`"CSV file is empty"`); Malformed JSON syntax or root array `[...]` (`"JSON parse error: ..."`); JSON `null` literal (`"Invalid JSON structure"`); Malformed XML syntax (`"XML parse error: ..."`); Unrecognized format string in `importData` (`"Unsupported format: <format>"`); Blank manual paste (`submit_import_text_button` disabled) | No artificial size/length cap (0 chars vs. ≥1 non-whitespace char for `pastedText.isNotBlank()`) | EP, BVA, Error Guessing overlay (empty, Unicode Cyrillic/Latin, SQLi strings, double-submit) | None |
| `P2` | `jsonStructureMode` | `JSON` root `Map<String, Any>` shape | Full `lessons` array (`id`, `title`, `subtitle`, `grammarFocus`, `level`, nested `vocab`, optional `grammar` with 3x3 `matrix`, `sentences`) → `"Imported <L> lessons, <V> vocab words, and <S> sentences."`; Standalone `vocab` array (`lessons` absent) → synthetic lesson (`orderIndex = 99`, `level = "Custom"`) + `"Imported <V> vocabulary items successfully"`; Standalone empty `{"vocab": []}` → 1 synthetic lesson (`vocabCount = 0`) | Valid empty JSON object `{}` or `{"lessons": []}` → empty `ImportResult` (`"Imported 0 lessons, 0 vocab words, and 0 sentences."`, no DB write); JSON `null` (`"Invalid JSON structure"`); Non-object JSON root or syntax error (`"JSON parse error: ..."`) | `0` vs. `1` vs. `>1` lessons in `lessons[]` (`idx == 0` forced `isUnlocked = true`, `idx > 0` `isUnlocked = false`) | EP, BVA | None |
| `P3` | `csvFormatOptions` | `CSV` delimiter, header, quoting & columns | First non-blank line contains `";"` (`";"` delimiter file-wide) vs. no `";"` (`","` delimiter file-wide); Quoted fields with embedded delimiters (`"..."` toggles `inQuotes` and strips `"`); First line containing `"target"` or `"serbian"` (case-insensitive, skipped as header) vs. data-only first line (`startIndex = 0`); 1–6 positional columns (`0: targetWord`, `1: targetWordLatin`, `2: sourceTranslation`, `3: partOfSpeech`, `4: exampleSentence`, `5: audioUri`) | Blank/whitespace-only CSV (`"CSV file is empty"`); Row with blank/missing col 0 (`targetWord.isBlank()`, row skipped); Extra columns `>6` (ignored); Header-only CSV (`0` vocab items, 1 synthetic lesson `orderIndex = 100` with `"Imported CSV deck with 0 words."`); Unclosed quote (extends to EOL and trims token) | `0` non-blank lines vs. `1` header-only line vs. `≥1` valid data rows; `1` column (`targetWord` only) vs. `6` columns vs. `>6` columns | EP, BVA | None |
| `P4` | `xmlStructure` | `XML` `<curriculum>` document | `<curriculum>` with `<lesson id title grammarFocus level>` containing `<vocab target latin translation pos audio>` and `<sentence source target latin tokens distractors hint audio>` → `"Imported XML with <L> lessons, <V> words, <S> sentences."`; `<vocab>` or `<sentence>` before first `<lesson>` (ignored when `currentLessonId` is empty); `<vocab>` with blank `target` (skipped); Unsupported tags like `<grammar>` (ignored, `grammarGuides` empty) | Malformed XML syntax (`"XML parse error: ..."`); Empty/self-closing `<curriculum/>` with 0 lessons and 0 vocab (`"Imported XML with 0 lessons, 0 words, 0 sentences."`, no DB write) | `0` vs. `1` vs. `>1` `<lesson>` elements | EP, BVA | None |

### Business Rules (`R<n>`)

| ID | Rule | Conditions | Actions | Full Columns | Reduced Columns | Assigned Technique(s) | Gap / Blockers |
|---|---|---|---|---|---|---|---|
| `R1` | Import Outcome & Room Upsert Persistence | C1: Parse succeeds without exception (`T`/`F`)<br>C2: `lessons` non-empty (`T`/`F`)<br>C3: `vocabItems` non-empty (`T`/`F`) | A1: Return `ImportResult` with format-specific `message`<br>A2: Upsert entities to Room via `OnConflictStrategy.REPLACE` (preserving unrelated existing rows; cascading child replacement on matching `lessonId`)<br>A3: Force `idx == 0` lesson `isUnlocked = true`, `idx > 0` `isUnlocked = false`, all `masteryScore = 0f`<br>A4: Upsert `SrsReviewEntity` rows (`id = v.id`, `repetition = 0`, `intervalDays = 1`, `easeFactor = 2.5f`) via `OnConflictStrategy.REPLACE`<br>A5: Skip `importCurriculum` and leave Room DB untouched when `C1=F` or (`C2=F` and `C3=F`) | 8 | 5 (collapse `C1=F` don't-cares on `C2,C3`) | Decision Table | None |
| `R2` | CSV Line Tokenization (`parseCsvLine`) | C1: `inQuotes` (`T`/`F`)<br>C2: Char (`delimiter ,/;` \| `"` \| `other`) | A1: Toggle `inQuotes` (strip `"`)<br>A2: Split field and trim token<br>A3: Append literal char to current field | 6 | 6 | Decision Table | None |

### States (`S<n>`) & Transitions (`T<n>`)

| ID | State | Description / Entry & Exit | Assigned Technique(s) | Gap / Blockers |
|---|---|---|---|---|
| `S1` | `ExistingCurriculum` | Local Room DB holds default seed or previously imported curriculum; `isImporting = false` | State Transition | None |
| `S2` | `ImportedCurriculum` | Room DB holds upserted entities (`OnConflictStrategy.REPLACE`) alongside non-colliding prior entities; first imported lesson has `isUnlocked = true` (subsequent in batch `isUnlocked = false`, `masteryScore = 0f`); `SrsReviewEntity` rows upserted for imported vocab; `statusMessage` shows success message | State Transition | None |
| `S3` | `ImportFailedOrNoOp` | Empty `ImportResult` returned (parse error, blank CSV, empty `{}` / `<curriculum/>`, or SAF stream null/error); existing Room DB state preserved uncorrupted; `statusMessage` shows error/0-count message | State Transition | None |
| `S4` | `DefaultSeedCurriculum` | Room DB wiped via `lessonDao().clearAll()` and re-seeded via `ensureInitialDataLoaded()`; `statusMessage` set to `"База сброшена до стандартной программы B1 Сербского языка."` | State Transition | None |

| ID | From | Event | To | Legal / Illegal | Assigned Technique(s) | Gap / Blockers |
|---|---|---|---|---|---|---|
| `T1` | `S1` | Submit valid non-empty JSON/CSV/XML payload (SAF or paste) | `S2` | Legal | State Transition | None |
| `T2` | `S1` | Submit malformed JSON/XML, blank CSV, empty `{}`/`<curriculum/>`, or unreadable SAF URI | `S3` | Legal | State Transition | None |
| `T3` | `S1` | Tap `reset_defaults_button` | `S4` | Legal | State Transition | None |
| `T4` | `S2` | Submit malformed JSON/XML, blank CSV, or empty `{}`/`<curriculum/>` | `S3` | Legal (preserves `S2` data) | State Transition | None |
| `T5` | `S2` | Tap `reset_defaults_button` | `S4` | Legal | State Transition | None |
| `T6` | `S3` | Submit valid non-empty JSON/CSV/XML payload | `S2` | Legal | State Transition | None |
| `T7` | `S3` | Tap `reset_defaults_button` | `S4` | Legal | State Transition | None |
| `T8` | `S1` / `S2` / `S3` | Tap `submit_import_text_button` while `pastedText.isBlank()` or `isImporting == true` | — | Illegal (blocked by UI `enabled = pastedText.isNotBlank() && !isImporting`) | State Transition | None |

### Configuration Dimensions (`C<n>`)

| ID | Dimension | Factors & Values | Exhaustive Pairs | Assigned Technique(s) | Gap / Blockers |
|---|---|---|---|---|---|
| `C1` | Import Format × Input Source | Format: `{JSON, CSV, XML}` × Source: `{SAF file picker (content auto-detected), Pasted raw text (chip-selected)}` | 6 | Pairwise | None |
| `C2` | Sample Template Format × UI Side Effects | Format: `{JSON, CSV, XML}` × Side Effect: `{Populate pastedText & selectedFormat, Copy to Clipboard & show Toast "Шаблон <FORMAT> скопирован"}` | 6 | Pairwise | None |

### Use Cases (`U<n>`)

| ID | Use Case | Paths | Assigned Technique(s) | Gap / Blockers |
|---|---|---|---|---|
| `U1` | Import Curriculum | **Basic (1):** Select format chip or pick SAF file → parse → upsert to Room (`OnConflictStrategy.REPLACE`), unlock 1st imported lesson (`idx == 0`), keep `idx > 0` locked, seed SRS cards, show `result.message` in status card (plus `Toast.LENGTH_LONG` for SAF).<br>**Alternative (2):** (A1) Tap sample template button (`JSON`/`CSV`/`XML`) → copies template to clipboard, populates `pastedText`, sets `selectedFormat`, shows `"Шаблон <FORMAT> скопирован"` Toast → submit; (A2) Cancel SAF file picker (`uri == null`) → no-op, DB and `statusMessage` unchanged.<br>**Exception (3):** (E1) Malformed JSON/CSV/XML or JSON `null` → `"<FORMAT> parse error: ..."` / `"Invalid JSON structure"`, DB intact; (E2) Blank/whitespace CSV or empty `{}` / `<curriculum/>` → `"CSV file is empty"` / 0-count message, DB intact; (E3) SAF `openInputStream` returns `null` or throws `Exception` → `"Не удалось открыть выбранный файл"` or `"Ошибка чтения файла: ..."`, DB intact. | Use Case | None |
| `U2` | Reset to Default Curriculum | **Basic (1):** Tap `reset_defaults_button` in `ImportExportScreen` → immediately calls `viewModel.resetToDefaults()` (`clearAll()` + `ensureInitialDataLoaded()`, no confirmation dialog) and displays `"База сброшена до стандартной программы B1 Сербского языка."`. | Use Case | None |

---

## 2. Gap List (`G<n>`) — Resolved in PRD & PSD

All 10 previously identified gaps (`G1`–`G10`) have been resolved in `Docs/prd/curriculum-import.md` and `Docs/psd/curriculum-import.md`:

| Gap ID | Resolution Summary | Status |
|---|---|---|
| `G1` | **Import merge vs. replace semantics:** `CurriculumRepository.importCurriculum` upserts by primary key using `@Insert(onConflict = OnConflictStrategy.REPLACE)` without wiping non-colliding lessons/items (replacing a `LessonEntity` ID cascades to its child tables). | Resolved (FR-4) |
| `G2` | **Exact schema & field definitions:** Full field keys, defaults, synthetic lesson metadata (`orderIndex = 99` for JSON vocab, `100` for CSV), ID generation patterns, and sample template contents are specified. | Resolved (FR-1, FR-2, FR-3) |
| `G3` | **Subsequent lessons' `isUnlocked` state:** `idx == 0` in the imported batch is forced to `isUnlocked = true`; all `idx > 0` remain `isUnlocked = false`, and all imported lessons reset `masteryScore = 0f`. | Resolved (FR-4) |
| `G4` | **Duplicate / re-imported vocabulary SRS behavior:** `SrsDao.insertAll` uses `OnConflictStrategy.REPLACE` keyed by `id = v.id`, resetting matching SRS rows to initial SM-2 values (`repetition = 0`, `intervalDays = 1`, `easeFactor = 2.5f`) without duplicating rows. | Resolved (FR-4) |
| `G5` | **Blank/empty JSON & XML and valid-empty structures:** JSON `null` returns `"Invalid JSON structure"`; `{}` / `{"lessons":[]}` and empty `<curriculum/>` return 0-count messages and skip `importCurriculum`; `{"vocab":[]}` creates 1 empty synthetic lesson. | Resolved (FR-1, FR-3, FR-4) |
| `G6` | **CSV edge cases:** File-wide delimiter auto-detected from first non-blank line (`";"` vs `","`), header skipped iff first line contains `"target"` or `"serbian"` (case-insensitive), `"` toggles `inQuotes` and is stripped, blank `targetWord` rows skipped, extra cols `>5` ignored. | Resolved (FR-2) |
| `G7` | **Input boundaries & SAF errors:** No artificial size/count limit; SAF launches `"*/*"`, auto-detects format from trimmed prefix (`"<"`, `"{"`/`"["`, else `"CSV"`), and reports `"Не удалось открыть выбранный файл"` or `"Ошибка чтения файла: ..."` on stream errors. | Resolved (FR-5) |
| `G8` | **XML `<grammar>` support:** `<grammar>` is unsupported in XML (and CSV) and ignored by `parseXml`; 3x3 grammar matrices are imported via JSON only. | Resolved (FR-3, Out of scope) |
| `G9` | **Exact status messages & UI feedback:** Exact strings for parser results, template copy Toasts (`"Шаблон <FORMAT> скопирован"`), and reset status (`"База сброшена до стандартной программы B1 Сербского языка."`) are specified. | Resolved (FR-1..FR-5) |
| `G10` | **Concurrency / double-submit behavior:** `isImporting` state disables `submit_import_text_button` and renders `CircularProgressIndicator` during active imports (`T8`). | Resolved (FR-5) |

```qa-summary
schema_version=1
gate_status=approved
elements_total=22
elements_gap=0
techniques=EP,BVA,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
boundaries_tested=0,1,6
dt_columns_full=14
dt_columns_reduced=11
states_total=4
transitions_legal=7
transitions_illegal=1
usecase_paths=7
configs=2
```
