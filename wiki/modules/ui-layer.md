# UI Layer

> Jetpack Compose screens, Material 3 theme, and `LanguageLearnerViewModel`.

Last updated: 2026-11-13

## Screens & Theme

The UI is built with Jetpack Compose and Material 3 across dedicated screen composables for curriculum browsing, lesson details, vocabulary drills, 3x3 grammar matrices, sentence practice, spaced repetition reviews, and curriculum import/export.

## Cross-references

- [[overview]] — how screens connect to `LanguageLearnerViewModel`.
- [[srs-and-audio]] — audio playback and SRS grading UI integrations.
- [[unit-and-ui-tests]] — Robolectric Compose UI tests verifying screen rendering and interactions.

## Sources

- `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt` — screen state and user-action handlers.
- `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt` — curriculum list and lesson selection screen.
- `app/src/main/java/com/example/ui/screens/ImportExportScreen.kt` — JSON/CSV/XML import and template export screen.
- `app/src/main/java/com/example/ui/theme/Theme.kt` — Material 3 color scheme and typography wiring.

Back to [[overview]]
