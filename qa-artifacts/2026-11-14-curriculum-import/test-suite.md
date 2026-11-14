# Test Suite — Curriculum Import & Export

**Scope:** `Curriculum Import & Export` (`ImportExportScreen`, `CurriculumImporter`, `CurriculumRepository.importCurriculum`, `CurriculumRepository.resetToDefaultCurriculum`).
**Note on Gaps:** Where the pasted specification leaves exact field names, message formats, or DB merge/unlock semantics unstated, expected results are marked `[UNSTATED / G<n>]` without inventing rules.

## Test Cases

| Test Case ID | Scenario Title | Applied Design Technique | Preconditions | Input Data | Expected Result | Priority | Covers |
|---|---|---|---|---|---|---|---|
| `TC-001` | Import valid JSON with `lessons` array (`vocab`, `grammar`, `sentences`) via pasted text | EP, Decision Table, State Transition, Pairwise, Use Case | App in `S1` (`ExistingCurriculum`); `ImportExportScreen` open; format = `JSON` | Pasted valid JSON payload with `lessons` array containing nested `vocab`, `grammar`, and `sentences` (`[UNSTATED / G2]`) | **UI:** Descriptive import success message displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumImporter.parseJson` returns non-empty `ImportResult`; `CurriculumRepository.importCurriculum` persists lessons, vocab, grammar guides, and sentences to Room (`[UNSTATED / G1]`); first imported lesson forced to `isUnlocked = true` (subsequent lessons `[UNSTATED / G3]`); `SrsReviewEntity` rows seeded for imported vocab (`[UNSTATED / G4]`). State transitions `S1 -> S2`. | `P1` | `P1, P2, R1, S1, S2, T1, C1, U1` |
| `TC-002` | Import valid JSON with standalone `vocab` array via SAF file picker | EP, Decision Table, Pairwise | App in `S1`; `ImportExportScreen` open; format = `JSON`; valid `.json` file on device storage | SAF file containing valid JSON with standalone `vocab` array (`lessons` absent/empty) (`[UNSTATED / G2]`) | **UI:** Descriptive import success message displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumImporter.parseJson` returns `ImportResult` with `vocabItems` non-empty and `lessons` empty; `CurriculumRepository.importCurriculum` persists vocab to Room and seeds `SrsReviewEntity` rows (`[UNSTATED / G1, G4]`). | `P0` | `P1, P2, R1, C1` |
| `TC-003` | Import valid JSON with `lessons` array and empty `vocab` | Decision Table | App in `S1`; format = `JSON` | Valid JSON with `lessons` array (`>= 1` lesson) and no `vocab` items (`[UNSTATED / G2]`) | **UI:** Descriptive import success message displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumImporter.parseJson` returns `lessons` non-empty, `vocabItems` empty; Room persists lesson(s) with first lesson `isUnlocked = true`; zero `SrsReviewEntity` rows seeded. | `P1` | `P2, R1` |
| `TC-004` | Import valid comma-delimited CSV with header row and quoted embedded commas via SAF file picker | EP, Decision Table, Pairwise | App in `S1`; format = `CSV`; valid `.csv` file on device storage | SAF `.csv` file with header row and data row containing `"field, with comma",term2` (`[UNSTATED / G2, G6]`) | **UI:** Descriptive import success message displayed (`[UNSTATED / G9]`).<br>**Backend:** `parseCsvLine` toggles `inQuotes` on `"`, preserves embedded comma inside quotes, splits on unquoted `,`; `ImportResult` contains parsed vocab; Room persists vocab and seeds `SrsReviewEntity` rows (`[UNSTATED / G1, G4]`). | `P0` | `P1, P3, R1, R2, C1` |
| `TC-005` | Import valid semicolon-delimited CSV without header row via pasted text | EP, Decision Table, Pairwise | App in `S1`; format = `CSV` | Pasted semicolon-delimited CSV without header row (`field1;field2;...`) (`[UNSTATED / G2, G6]`) | **UI:** Descriptive import success message displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumImporter.parseCsv` splits on `;`, parses all rows as vocab items, persists to Room, and seeds `SrsReviewEntity` rows. | `P1` | `P1, P3, R2, C1` |
| `TC-006` | Import valid XML `<curriculum>` with `<lesson>`, `<vocab>`, and `<sentence>` via SAF file picker | EP, Pairwise | App in `S1`; format = `XML`; valid `.xml` file on device storage | SAF `.xml` file with `<curriculum><lesson>...<vocab>...</vocab><sentence>...</sentence></lesson></curriculum>` (`[UNSTATED / G2]`) | **UI:** Descriptive import success message displayed (`[UNSTATED / G9]`).<br>**Backend:** `XmlPullParser` parses lesson, vocab, and sentence into `ImportResult`; Room persists entities, forces first lesson `isUnlocked = true`, seeds `SrsReviewEntity` rows. | `P1` | `P1, P4, C1` |
| `TC-007` | Import XML with `<vocab>` and `<sentence>` outside `<lesson>` via pasted text | EP, Pairwise | App in `S1`; format = `XML` | Pasted XML `<curriculum>` containing `<vocab>` and `<sentence>` outside any `<lesson>` tag (`currentLessonId` empty) | **UI:** No crash; status message displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumImporter.parseXml` safely ignores `<vocab>` and `<sentence>` when `currentLessonId` is empty; no orphan entities persisted to Room. | `P1` | `P1, P4, C1` |
| `TC-008` | Reject malformed JSON syntax without corrupting existing DB | EP, Decision Table, State Transition, Use Case | App in `S1` with known existing curriculum in Room; format = `JSON` | Pasted malformed JSON string (e.g., `{ "lessons": [`) | **UI:** Error message prefixed with `JSON parse error:` displayed; no uncaught exception.<br>**Backend:** `CurriculumImporter.parseJson` catches exception and returns empty `ImportResult`; existing Room DB data remains completely intact (`S1 -> S3`). | `P0` | `P1, P2, R1, S1, S3, T2, U1` |
| `TC-009` | Reject malformed XML syntax after prior successful import | EP, State Transition, Use Case | App in `S2` (`ImportedCurriculum`) with previously imported data in Room; format = `XML` | Pasted malformed XML string (e.g., `<curriculum><lesson></curriculum>`) | **UI:** Error message prefixed with `XML parse error:` displayed; no uncaught exception.<br>**Backend:** `CurriculumImporter.parseXml` catches exception and returns empty `ImportResult`; previously imported Room DB data remains intact (`S2 -> S3`). | `P0` | `P1, P4, S2, S3, T4, U1` |
| `TC-010` | Reject blank or whitespace-only CSV | EP, Decision Table, State Transition, Use Case, Error Guessing | App in `S1` with known existing curriculum in Room; format = `CSV` | Whitespace-only input `"   \n\t  "` | **UI:** Status message `CSV file is empty` displayed.<br>**Backend:** `CurriculumImporter.parseCsv` returns empty `ImportResult`; existing Room DB data remains intact (`S1 -> S3`). | `P0` | `P1, P3, R1, S1, S3, T2, U1` |
| `TC-011` | Recover from failed import (`S3`) by submitting valid JSON (`S3 -> S2`) | State Transition | App in `S3` (`ImportFailed`) after a prior parse error; format = `JSON` | Pasted valid JSON curriculum payload (`[UNSTATED / G2]`) | **UI:** Descriptive success message replaces previous error message (`[UNSTATED / G9]`).<br>**Backend:** Room persists imported entities, forces first lesson `isUnlocked = true`, seeds `SrsReviewEntity` rows (`S3 -> S2`). | `P1` | `S3, S2, T6` |
| `TC-012` | Reset to default curriculum from existing state (`S1 -> S4`) | State Transition, Use Case | App in `S1` with modified/custom curriculum in Room; `ImportExportScreen` open | Tap reset-to-defaults button (`[UNSTATED / G9]`) | **UI:** Reset feedback displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumRepository.resetToDefaultCurriculum` restores Room DB to built-in default offline seed curriculum (`S1 -> S4`). | `P0` | `S1, S4, T3, U2` |
| `TC-013` | Reset to default curriculum after successful import (`S2 -> S4`) | State Transition | App in `S2` (`ImportedCurriculum`) | Tap reset-to-defaults button (`[UNSTATED / G9]`) | **UI:** Reset feedback displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumRepository.resetToDefaultCurriculum` replaces/restores Room DB to built-in default seed curriculum (`S2 -> S4`). | `P0` | `S2, S4, T5` |
| `TC-014` | Reset to default curriculum after failed import (`S3 -> S4`) | State Transition | App in `S3` (`ImportFailed`) | Tap reset-to-defaults button (`[UNSTATED / G9]`) | **UI:** Reset feedback displayed (`[UNSTATED / G9]`).<br>**Backend:** `CurriculumRepository.resetToDefaultCurriculum` restores Room DB to built-in default seed curriculum (`S3 -> S4`). | `P0` | `S3, S4, T7` |
| `TC-015` | Load and copy JSON sample template, then submit | Pairwise, Use Case | `ImportExportScreen` open; format = `JSON` | Tap Load JSON template, tap Copy JSON template, paste/submit (`[UNSTATED / G2]`) | **UI:** Raw text input populated with valid JSON template on Load; clipboard contains JSON template on Copy (`[UNSTATED / G9]`); submit succeeds.<br>**Backend:** Template parses without error and persists to Room. | `P2` | `C2, U1` |
| `TC-016` | Load and copy CSV sample template | Pairwise | `ImportExportScreen` open; format = `CSV` | Tap Load CSV template, then tap Copy CSV template (`[UNSTATED / G2]`) | **UI:** Raw text input populated with valid CSV template on Load; clipboard contains CSV template on Copy (`[UNSTATED / G9]`). | `P2` | `C2` |
| `TC-017` | Load and copy XML sample template | Pairwise | `ImportExportScreen` open; format = `XML` | Tap Load XML template, then tap Copy XML template (`[UNSTATED / G2]`) | **UI:** Raw text input populated with valid XML template on Load; clipboard contains XML template on Copy (`[UNSTATED / G9]`). | `P2` | `C2` |
| `TC-018` | Import valid-but-empty JSON (`{}`) or XML (`<curriculum/>`) | Decision Table, Error Guessing | App in `S1`; format = `JSON` (or `XML`) | Pasted `{}` (or `<curriculum/>`) | **UI & Backend:** `[UNSTATED / G5]` — Spec does not state whether valid-empty JSON/XML returns an empty-curriculum error or succeeds with 0 persisted items; must not crash. | `P1` | `P1, P2, P4, R1` |
| `TC-019` | Import multi-byte Unicode (non-ASCII / emoji / diacritics) vocabulary | Error Guessing | App in `S1`; format = `CSV` (or `JSON`/`XML`) | Pasted CSV with multi-byte Unicode characters (e.g., `"café, ☕","日本語"`) (`[UNSTATED / G2]`) | **UI:** Import succeeds without encoding corruption or crash (`[UNSTATED / G9]`).<br>**Backend:** Room persists exact Unicode strings for vocab/lessons without truncation or mojibake. | `P1` | `P1, P3` |
| `TC-020` | Import SQL injection string in vocab fields & double-submit import | Error Guessing | App in `S1`; format = `JSON` | Pasted JSON containing `"term": "'; DROP TABLE lessons; --"`, submitted twice rapidly (`[UNSTATED / G2, G10]`) | **UI:** No crash; status message displayed (`[UNSTATED / G10]`).<br>**Backend:** Room parameterized queries treat SQLi string as literal text without altering schema; concurrent/double-submit does not corrupt Room DB (`[UNSTATED / G10]`). | `P0` | `P1` |

---

## Gherkin Scenarios

```gherkin
Feature: Curriculum Import & Export

  @TC-001
  Scenario: Import valid JSON with lessons array via pasted text
    Given the app is in state S1 with existing curriculum data
    And the user is on ImportExportScreen with format "JSON" selected
    When the user pastes a valid JSON payload containing a "lessons" array with nested "vocab", "grammar", and "sentences"
    And taps submit
    Then the UI displays a descriptive import success message "[UNSTATED / G9]"
    And Room persists the imported lessons, vocab, grammar guides, and sentences "[UNSTATED / G1]"
    And the first imported lesson has isUnlocked = true
    And SrsReviewEntity rows are seeded for all imported vocabulary "[UNSTATED / G4]"

  @TC-002
  Scenario: Import valid JSON with standalone vocab array via SAF file picker
    Given the app is in state S1
    And the user is on ImportExportScreen with format "JSON" selected
    When the user selects a valid ".json" file containing a standalone "vocab" array via SAF
    Then the UI displays a descriptive import success message "[UNSTATED / G9]"
    And Room persists the imported vocab and seeds SrsReviewEntity rows "[UNSTATED / G1, G4]"

  @TC-003
  Scenario: Import valid JSON with lessons array and empty vocab
    Given the app is in state S1
    When the user submits a valid JSON payload with at least one lesson and zero vocab items
    Then Room persists the lesson with the first lesson isUnlocked = true and seeds zero SrsReviewEntity rows

  @TC-004
  Scenario: Import valid comma-delimited CSV with header and quoted embedded commas via SAF
    Given the app is in state S1
    And the user selects format "CSV"
    When the user selects a ".csv" file via SAF with a header row and quoted fields containing embedded commas
    Then parseCsvLine preserves commas inside quotes and splits on unquoted commas
    And Room persists the parsed vocab and seeds SrsReviewEntity rows

  @TC-005
  Scenario: Import valid semicolon-delimited CSV without header via pasted text
    Given the app is in state S1
    And the user selects format "CSV"
    When the user pastes semicolon-delimited CSV rows without a header row and submits
    Then Room persists all parsed vocab rows and seeds SrsReviewEntity rows

  @TC-006
  Scenario: Import valid XML curriculum via SAF file picker
    Given the app is in state S1
    And the user selects format "XML"
    When the user selects a valid ".xml" file containing "<curriculum>", "<lesson>", "<vocab>", and "<sentence>" via SAF
    Then XmlPullParser parses the entities and Room persists them with the first lesson unlocked and SRS seeded

  @TC-007
  Scenario: Import XML with vocab and sentence outside lesson tag
    Given the app is in state S1
    And the user selects format "XML"
    When the user pastes XML containing "<vocab>" and "<sentence>" outside any "<lesson>" tag
    Then the importer safely ignores the orphan elements when currentLessonId is empty without crashing

  @TC-008
  Scenario: Reject malformed JSON syntax without corrupting existing DB
    Given the app is in state S1 with known existing curriculum data in Room
    And the user selects format "JSON"
    When the user pastes malformed JSON and taps submit
    Then the UI displays an error message prefixed with "JSON parse error:"
    And no uncaught exception is thrown
    And the existing Room database remains unchanged

  @TC-009
  Scenario: Reject malformed XML syntax after prior successful import
    Given the app is in state S2 with previously imported curriculum data in Room
    And the user selects format "XML"
    When the user pastes malformed XML and taps submit
    Then the UI displays an error message prefixed with "XML parse error:"
    And the previously imported Room database remains unchanged

  @TC-010
  Scenario: Reject blank or whitespace-only CSV
    Given the app is in state S1 with known existing curriculum data in Room
    And the user selects format "CSV"
    When the user submits whitespace-only CSV text
    Then the UI displays "CSV file is empty"
    And the existing Room database remains unchanged

  @TC-011
  Scenario: Recover from failed import by submitting valid JSON
    Given the app is in state S3 after a failed import
    When the user selects format "JSON" and submits a valid JSON curriculum payload
    Then Room persists the imported curriculum and transitions to state S2

  @TC-012
  Scenario: Reset to default curriculum from existing state
    Given the app is in state S1
    When the user taps the reset-to-defaults button
    Then CurriculumRepository.resetToDefaultCurriculum restores the built-in default offline seed curriculum

  @TC-013
  Scenario: Reset to default curriculum after successful import
    Given the app is in state S2
    When the user taps the reset-to-defaults button
    Then CurriculumRepository.resetToDefaultCurriculum restores the built-in default offline seed curriculum

  @TC-014
  Scenario: Reset to default curriculum after failed import
    Given the app is in state S3
    When the user taps the reset-to-defaults button
    Then CurriculumRepository.resetToDefaultCurriculum restores the built-in default offline seed curriculum

  @TC-015
  Scenario: Load and copy JSON sample template then submit
    Given the user is on ImportExportScreen with format "JSON" selected
    When the user taps load template and copy template, then submits the loaded template
    Then the template populates the input and clipboard "[UNSTATED / G9]" and imports successfully

  @TC-016
  Scenario: Load and copy CSV sample template
    Given the user is on ImportExportScreen with format "CSV" selected
    When the user taps load template and copy template
    Then the CSV template populates the input and clipboard "[UNSTATED / G9]"

  @TC-017
  Scenario: Load and copy XML sample template
    Given the user is on ImportExportScreen with format "XML" selected
    When the user taps load template and copy template
    Then the XML template populates the input and clipboard "[UNSTATED / G9]"

  @TC-018
  Scenario: Import valid-but-empty JSON or XML payload
    Given the app is in state S1
    When the user submits "{}" as JSON or "<curriculum/>" as XML
    Then the app handles the empty structure without crashing "[UNSTATED / G5]"

  @TC-019
  Scenario: Import multi-byte Unicode vocabulary
    Given the app is in state S1
    When the user submits a valid CSV payload containing multi-byte Unicode and emoji characters
    Then Room persists the exact Unicode strings without encoding corruption

  @TC-020
  Scenario: Import SQL injection string and double-submit import
    Given the app is in state S1
    When the user submits a valid JSON payload containing "'; DROP TABLE lessons; --" in a field and taps submit twice rapidly
    Then Room stores the string literally without SQL injection and does not corrupt the database "[UNSTATED / G10]"
```

---

## Risk Register

| Test Case ID | Impact Score & Criterion | Probability Signals Counted | Unassessable Signals | Probability Level | Resulting Priority |
|---|---|---|---|---|---|
| `TC-001` | `4` (data mutation: Room entities + SRS) | `1`: boolean-logic complexity (`R1` has 3 conditions) | boundary density (`G7`), novelty (not marked) | Low (1) | `P1` |
| `TC-002` | `4` (data mutation: Room entities + SRS) | `2`: boolean-logic complexity (`R1`), known-fragile pattern (SAF file upload) | boundary density (`G7`), novelty | Medium (2) | `P0` |
| `TC-003` | `4` (data mutation: Room lessons) | `1`: boolean-logic complexity (`R1`) | boundary density (`G7`), novelty | Low (1) | `P1` |
| `TC-004` | `4` (data mutation: Room vocab + SRS) | `2`: boolean-logic complexity (`R1`), known-fragile pattern (SAF file upload) | boundary density (`G7`), novelty | Medium (2) | `P0` |
| `TC-005` | `4` (data mutation: Room vocab + SRS) | `0` (`R2` has 2 conditions < 3) | boundary density (`G7`), novelty | Low (0) | `P1` |
| `TC-006` | `4` (data mutation: Room entities + SRS) | `1`: known-fragile pattern (SAF file upload) | boundary density (`G7`), novelty | Low (1) | `P1` |
| `TC-007` | `4` (data mutation / selective ignore) | `0` | boundary density (`G7`), novelty | Low (0) | `P1` |
| `TC-008` | `5` (data loss/corruption prevention) | `1`: boolean-logic complexity (`R1`) | boundary density (`G7`), novelty | Low (1) | `P0` |
| `TC-009` | `5` (data loss/corruption prevention) | `0` | boundary density (`G7`), novelty | Low (0) | `P0` |
| `TC-010` | `5` (data loss/corruption prevention) | `1`: boolean-logic complexity (`R1`) | boundary density (`G7`), novelty | Low (1) | `P0` |
| `TC-011` | `4` (data mutation: recovery import) | `0` | boundary density (`G7`), novelty | Low (0) | `P1` |
| `TC-012` | `5` (irreversible action: resets DB to default) | `0` | novelty | Low (0) | `P0` |
| `TC-013` | `5` (irreversible action: wipes imported DB) | `0` | novelty | Low (0) | `P0` |
| `TC-014` | `5` (irreversible action: resets DB to default) | `0` | novelty | Low (0) | `P0` |
| `TC-015` | `3` (core workflow: template -> import) | `0` | novelty | Low (0) | `P2` |
| `TC-016` | `2` (secondary convenience flow: template) | `0` | novelty | Low (0) | `P2` |
| `TC-017` | `2` (secondary convenience flow: template) | `0` | novelty | Low (0) | `P2` |
| `TC-018` | `4` (data mutation / empty state handling) | `1`: boolean-logic complexity (`R1`) | boundary density (`G7`), novelty | Low (1) | `P1` |
| `TC-019` | `4` (data mutation: encoding integrity) | `1`: known-fragile pattern (character encoding) | boundary density (`G7`), novelty | Low (1) | `P1` |
| `TC-020` | `5` (data loss or corruption: SQLi + concurrency) | `1`: known-fragile pattern (concurrency / double-submit) | novelty | Low (1) | `P0` |

```qa-summary
schema_version=1
gate_status=approved
elements_total=21
elements_covered=21
elements_gap=10
coverage_pct=100.0
cases_total=20
cases_p0=9
cases_p1=8
cases_p2=3
techniques=EP,Decision Table,State Transition,Pairwise,Use Case,Error Guessing
dt_columns_full=14
dt_columns_reduced=11
states_total=4
transitions_legal=7
transitions_illegal=0
usecase_paths=5
configs=2
pairs_target=12
pairs_covered=12
pairs_missing=0
exhaustive_equivalent=61
post_reduction=58
post_dedup=20
```
