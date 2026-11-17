# Specification: Remove Script Switch Button and Keep Latin Alphabet Only

## Overview
Since not every language has two alphabets, the dual-script toggle (`isLatinScript` / `toggleScript()`) and its UI switch button (`toggle_script_button`) are unnecessary. Remove the script switch button and `isLatinScript` state, and render the UI exclusively using the Latin alphabet (with fallback to primary fields if a Latin field is blank).

## Functional Requirements
1. **ViewModel Cleanup (`LanguageLearnerViewModel.kt`)**:
   - Remove `isLatinScript` `StateFlow` and `toggleScript()` completely.
2. **Curriculum Screen (`CurriculumScreen.kt`)**:
   - Remove `toggle_script_button` from the top bar.
   - Update top-bar subtitle to `"Srpski jezik (B1)"` (no alphabet suffix).
3. **Latin-Only Rendering Across Screens**:
   - Update `VocabDrillScreen.kt`, `GrammarMatrixScreen.kt`, `SentencePracticeScreen.kt`, and `SpacedRepetitionScreen.kt` to always render Latin fields (falling back to primary fields if blank) without observing `isLatinScript`.
4. **Initial Curriculum Data (`InitialCurriculumData.kt`)**:
   - Update `SentenceEntity` `tokensJson` and `distractorTokensJson` to Latin lowercase tokens matching `targetSentenceLatin`.
5. **Tests & Documentation Sync**:
   - Update existing unit, Compose UI, and QA tests (`CurriculumProgressionTest`, `VocabDrillTest`, `GrammarMatrixTest`, `SentencePracticeTest`, `SpacedRepetitionTest`, `CurriculumAndImportUiTest`) for Latin-only behavior.
   - Update `qa-artifacts/`, `Docs/`, and `wiki/` to reflect the removal of the script switch button and Latin-only UI.

## Out of Scope
- Dropping or migrating Room/Moshi schema columns (`targetWord`, `targetSentence`, `exampleSerbian`); those schema fields remain intact.

## Acceptance Criteria
- `toggle_script_button`, `isLatinScript`, and `toggleScript()` are completely removed from the codebase.
- `CurriculumScreen` subtitle displays `"Srpski jezik (B1)"`.
- All learning screens (`VocabDrillScreen`, `GrammarMatrixScreen`, `SentencePracticeScreen`, `SpacedRepetitionScreen`) render Latin text (with primary fallback if blank).
- `InitialCurriculumData` sentence tokens and distractors use Latin lowercase tokens.
- `./gradlew testDebugUnitTest` passes.
- `python3 wiki/lint.py` exits with code 0.
