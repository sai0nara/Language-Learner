# Implementation Plan: Remove Script Switch Button and Keep Latin Alphabet Only

## Phase 1: ViewModel & CurriculumScreen Cleanup

- [x] Task: Remove `isLatinScript` / `toggleScript()` from `LanguageLearnerViewModel` and script toggle UI from `CurriculumScreen` 995e796
  - [x] Write Tests: Update/add unit and Compose UI tests (`CurriculumAndImportUiTest`, `CurriculumProgressionTest`) verifying `toggle_script_button` is absent, subtitle reads `"Srpski jezik (B1)"`, and `LanguageLearnerViewModel` no longer exposes `isLatinScript` or `toggleScript()`
  - [x] Implement: Remove `isLatinScript` `StateFlow` and `toggleScript()` from `app/src/main/java/com/example/ui/viewmodel/LanguageLearnerViewModel.kt`, remove `toggle_script_button` from `app/src/main/java/com/example/ui/screens/CurriculumScreen.kt`, and update top-bar subtitle to `"Srpski jezik (B1)"`
- [x] Task: Conductor - User Manual Verification 'Phase 1: ViewModel & CurriculumScreen Cleanup' (Protocol in workflow.md) 995e796

## Phase 2: Latin-Only Screen Rendering & Initial Curriculum Tokens

- [x] Task: Update drill, matrix, sentence, and SRS screens to always render Latin fields (with primary fallback if blank) 995e796
  - [x] Write Tests: Update/add tests (`VocabDrillTest`, `GrammarMatrixTest`, `SentencePracticeTest`, `SpacedRepetitionTest`) asserting Latin-only display and fallback behavior when Latin fields are blank
  - [x] Implement: Update `VocabDrillScreen.kt`, `GrammarMatrixScreen.kt`, `SentencePracticeScreen.kt`, and `SpacedRepetitionScreen.kt` to remove `isLatinScript` observation and always render Latin fields with fallback to primary fields if blank
- [x] Task: Update `InitialCurriculumData` sentence tokens to Latin lowercase 995e796
  - [x] Write Tests: Add/update assertions verifying `SentenceEntity` `tokensJson` and `distractorTokensJson` contain Latin lowercase tokens matching `targetSentenceLatin`
  - [x] Implement: Update `SentenceEntity` `tokensJson` and `distractorTokensJson` in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` to Latin lowercase tokens
- [x] Task: Conductor - User Manual Verification 'Phase 2: Latin-Only Screen Rendering & Initial Curriculum Tokens' (Protocol in workflow.md) 995e796

## Phase 3: Documentation, QA Artifacts, & Full Verification

- [x] Task: Sync documentation, wiki pages, and QA artifacts for Latin-only behavior 1f08cda
  - [x] Update `Docs/`, `wiki/`, and `qa-artifacts/` references to reflect removal of the script toggle and Latin-only UI
- [x] Task: Run full test suite and wiki linter 995e796
  - [x] Run `./gradlew testDebugUnitTest` and verify all unit & Compose UI tests pass
  - [x] Run `python3 wiki/lint.py` and verify exit code 0
- [x] Task: Conductor - User Manual Verification 'Phase 3: Documentation, QA Artifacts, & Full Verification' (Protocol in workflow.md)
