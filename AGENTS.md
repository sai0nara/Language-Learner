# Language Learner

Offline-first Android language learning app with custom XML/CSV/JSON curriculum import, SM-2 spaced repetition, 3x3 grammar matrices, and dual-audio pronunciation playback. Built with Kotlin + Jetpack Compose (Material 3), Room persistence, MVVM with a repository pattern (`CurriculumRepository`), and Kotlin Coroutines/Flow. ViewModels and dependencies are constructed manually in `MainActivity` / `LanguageLearnerViewModel` without a DI framework.

## Driving the project: `conductor/`

`conductor/` is the project-management layer. Before starting work, read the
track workflow (`conductor/workflow.md`) and current track status
(`conductor/tracks.md` — one `[x]`/`[~]`/`[ ]` line per track). Each track has its
own plan under `conductor/tracks/<track_id>/plan.md` (or `conductor/archive/` for
finished ones). Work follows TDD: red (failing test) → green → refactor → UI test.
Aim for >80% coverage and UI tests on every user-facing feature.

## Building & testing

The project is configured for **JDK 25** (`org.gradle.java.home` in `gradle.properties`).

- Build debug APK: `gradle assembleDebug`
- Install debug build on a device/emulator: `gradle installDebug`
- JVM unit & Robolectric Compose UI tests: `gradle testDebugUnitTest`
- Instrumented tests (require a connected device/emulator): `gradle connectedAndroidTest`
- Always shut the emulator down after tests: `adb emu kill`.
- `wiki/lint.py` and related scripts use stock `python3`; no extra test framework required.

## Documentation

- `Docs/<type>/` is the documented-feature set, one feature = a lower-kebab-case
  slug in three files: `Docs/prd/<slug>.md` (requirements), `Docs/psd/<slug>.md`
  (design/spec), `Docs/tests/<slug>.md` (test cases). The catalog is
  `Docs/index.md`; a feature must list all three paths there or the wiki lint's
  `MISSING` check fails. A not-yet-built feature is PRD-only and marked `— planned`
  in the index. Templates live in `Docs/_templates/`.
- `Docs/Review.md` and `Docs/Defects.md` are the review/defect registers.

## Code layout

Under `app/src/main/java/com/example/`:

- `ui/` — Compose screens (`CurriculumScreen`, `LessonDetailScreen`, `VocabDrillScreen`, `GrammarMatrixScreen`, `SentencePracticeScreen`, `SpacedRepetitionScreen`, `ImportExportScreen`), `LanguageLearnerViewModel`, and Material 3 `theme/` (`Theme.kt`, `Color.kt`, `Type.kt`).
- `data/model/` — Room entities (`Entities.kt`) and 3x3 grammar matrix models (`GrammarMatrix.kt`).
- `data/local/` — `AppDatabase` (Room), `Converters`, and DAOs (`LessonDao`, `LessonAnswerDao`, `VocabDao`, `GrammarDao`, `SentenceDao`, `SrsDao`).
- `data/repository/` — `CurriculumRepository` orchestrating Room DAOs, lesson mastery/unlocking, SRS enrollment/updates, and curriculum imports.
- `data/importer/` — `CurriculumImporter` parsing JSON, CSV, and XML curricula and providing sample export templates.
- `data/initial/` — `InitialCurriculumData` offline seed curriculum.
- `srs/` — `SpacedRepetitionAlgorithm` (SM-2 scheduling and `ReviewGrade` mapping).
- `audio/` — `AudioController` for dual-audio pronunciation playback.

## Wiki (LLMwiki)

The operational contract for the vault lives in `wiki/meta/schema.md`; the full
contract and rationale is `wiki/meta/spec.md`. Read the operational schema
before modifying code or wiki content.

Three rules govern every change:
1. **Agent owns the vault** — when a change touches source that a wiki page
   cites, update the page and keep both in sync.
2. **Cite, don't copy** — pages explain durable structure and cite mutable
   values to the owning file via a backticked relative path such as
   `` `app/src/main/java/com/example/.../File.kt` ``; never transcribe a value into a page.
3. **The lint exits 0 before the turn ends** — run it before finishing.

Commands:
- Lint: `python3 wiki/lint.py` (or `./wiki/lint.sh` from the repo root)
- Hook self-tests: `python3 wiki/hooks/test_hooks.py`
- Lint self-tests: `python3 wiki/test_lint.py`

Content boundary: the wiki describes this repo's architecture and behavior.
No secrets, personal data, or issue-tracker references go in wiki pages. New
pages must follow the structure in `wiki/meta/schema.md`. The `Docs/` product docs
are covered by the same lint (freshness + path checks) but are exempt from the
wikilink/navigation checks.
