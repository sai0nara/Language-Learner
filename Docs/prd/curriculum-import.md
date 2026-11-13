# Curriculum Import & Export — Product Requirements

> Enables self-directed learners to import custom XML, CSV, and JSON curricula offline and export sample templates.

Last updated: 2026-11-13

## Overview

Self-directed learners need full control over their study materials without relying on cloud servers. The import/export feature in `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` and `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` lets users load custom lessons, vocabulary decks, 3x3 grammar matrices, and sentence drills from files or clipboard text, or reset to the built-in seed curriculum.

## Goals / Non-goals

**Goals**

- Parse custom curricula in JSON, CSV, and XML formats completely offline.
- Provide one-tap sample templates for JSON, CSV, and XML so users can author compatible decks.
- Persist imported lessons, vocabulary, grammar guides, and sentences into Room and automatically enroll imported vocabulary into SRS.
- Allow resetting the local database back to the default offline curriculum.

**Non-goals**

- Cloud-hosted deck marketplaces or mandatory account synchronization.

## User stories

- As a self-directed learner, I want to import a JSON, CSV, or XML file from device storage or pasted text so that I can study my own textbook or tutor materials offline.
- As a curriculum author, I want to load or copy a sample JSON/CSV/XML template in the app so that I know the exact schema expected by the importer.
- As a learner, I want to reset the curriculum to the default seed data if I want a fresh start.

## Functional requirements

- FR-1: Support importing JSON payloads containing either a full `lessons` array (with nested `vocab`, `grammar`, and `sentences`) or a standalone `vocab` array.
- FR-2: Support importing comma- and semicolon-delimited CSV vocabulary lists, handling quoted fields and optional header rows.
- FR-3: Support importing XML `<curriculum>` documents containing `<lesson>`, `<vocab>`, and `<sentence>` elements.
- FR-4: Unlock the first imported lesson automatically and create initial SRS review cards for all imported vocabulary items.
- FR-5: Gracefully handle malformed or empty input by returning a descriptive status message without crashing or corrupting existing data.

## Non-functional requirements

- **Offline-first:** All file reading, parsing, and database persistence execute locally without network access.
- **Resilience:** Malformed inputs never throw uncaught exceptions on the UI thread.

## Acceptance criteria

The product specification (`Docs/psd/curriculum-import.md`) defines how these requirements are met; the test cases (`Docs/tests/curriculum-import.md`) verify them.

## Out of scope

- Remote URL fetching or cloud sync of imported decks.

## Cross-references

- `Docs/psd/curriculum-import.md` — the specification that implements these requirements.
- `Docs/tests/curriculum-import.md` — the test cases that verify them.
- [[data-layer]] — where the importer and repository persistence are explained.

## Sources

- `app/src/main/java/com/example/data/importer/CurriculumImporter.kt` — JSON, CSV, and XML parsers and sample templates.
- `app/src/main/java/com/example/data/repository/CurriculumRepository.kt` — persistence of imported curricula and SRS card creation.
- `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` — import/export UI screen and SAF file picker integration.
- `Docs/psd/curriculum-import.md` — product specification.
- `Docs/tests/curriculum-import.md` — test cases.
