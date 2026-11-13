# Dual-Audio & TTS Pronunciation Playback — Test Cases

> Verifies `audioUri` persistence across imports and safe `AudioController` initialization under Robolectric Compose UI tests.

Last updated: 2026-11-13

## Automated coverage

| Stack | Test file | Scope |
|---|---|---|
| Robolectric unit | `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` | Preservation of `audioUri` fields when importing JSON, CSV, and XML curricula. |
| Robolectric Compose UI | `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` | `LanguageLearnerViewModel` + `AudioController` initialization and safe execution when TTS is uninitialized in test environments. |

## Test cases

| ID | Scenario | Preconditions | Steps | Expected |
|---|---|---|---|---|
| T-1 | `audioUri` parsing in importers | JSON/CSV/XML payloads with `audioUri` / `audio` attributes | Parse via `CurriculumImporter` | `VocabItemEntity.audioUri` and `SentenceEntity.audioUri` are populated. |
| T-2 | Safe TTS fallback when engine unavailable | Robolectric environment without native TTS voice | Instantiate `LanguageLearnerViewModel` and render screens | `AudioController` initializes safely without throwing exceptions. |

## Manual checks

- Device playback of Serbian (`sr_RS`) and Russian (`ru_RU`) TTS utterances and custom local `.mp3`/`.wav` `audioUri` files.

## Cross-references

- `Docs/prd/dual-audio-playback.md` — the requirements under test.
- `Docs/psd/dual-audio-playback.md` — the design the cases verify.
- [[unit-and-ui-tests]] — test suite overview.

## Sources

- `app/src/test/java/com/example/data/importer/CurriculumImporterTest.kt` — importer `audioUri` tests.
- `app/src/test/java/com/example/ui/screens/CurriculumAndImportUiTest.kt` — ViewModel and `AudioController` lifecycle under Robolectric.
- `Docs/prd/dual-audio-playback.md` — requirements under test.
- `Docs/psd/dual-audio-playback.md` — design the cases verify.
