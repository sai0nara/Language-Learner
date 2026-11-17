# Implementation Plan: Revise Lessons Content to Suit Requirements

## Phase 1: Vocabulary Content Revision (Lessons 1–16)

- [ ] Task: Revise `InitialCurriculumData.getInitialVocab()` and `CurriculumImporter` sample template to provide 10–15 vocab items per lesson with 3 Russian translation distractors and Latin Serbian example sentences
  - [ ] Write Tests: Add/update unit tests (`ExampleUnitTest`, `CurriculumImporterTest`, `RoomPersistenceAndRepositoryTest`) verifying each lesson (`lesson_1`..`lesson_16`) has 10–15 vocab items, every `distractorsJson` has 3 distinct Russian translation strings (not matching `sourceTranslation`), and `exampleSentence` is non-blank Latin Serbian
  - [ ] Implement: Update `getInitialVocab()` and `getInitialLessons()` (`vocabCount`, `sentenceCount`) in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt`, and update sample vocab `distractors` in `app/src/main/java/com/example/data/importer/CurriculumImporter.kt`
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Vocabulary Content Revision (Lessons 1–16)' (Protocol in workflow.md)

## Phase 2: Grammar Guides, 3x3 Matrices, & Sentence Practice Revision (Lessons 1–16)

- [ ] Task: Populate full 3x3 `ConjugationMatrixData` and Latin Serbian examples in `getInitialGrammarGuides()` for all 16 lessons
  - [ ] Write Tests: Add unit tests verifying all 16 `GrammarGuideEntity` entries have non-empty 9-cell `ConjugationMatrixData` covering all 3 tenses × 3 forms, with Latin `formula` and `exampleSerbianLatin`, and no Cyrillic Serbian examples in `explanationMarkdown`
  - [ ] Implement: Author lesson-specific 3x3 `ConjugationMatrixData` and Latin-Serbian `explanationMarkdown` for `lesson_1`..`lesson_16` in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt`
- [ ] Task: Author 9 lesson-specific sentences per lesson across all 9 matrix coordinates in `getInitialSentences()`
  - [ ] Write Tests: Add unit tests verifying each lesson (`lesson_1`..`lesson_16`) has 9 distinct sentences covering all 9 `(matrixTense × matrixForm)` coordinates, with lowercase Latin `tokensJson`, 1–2 lowercase Latin `distractorTokensJson`, and Latin Serbian terms in `grammarHint`
  - [ ] Implement: Replace generic `raditi` template fallbacks with lesson-specific sentences across all 16 lessons in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` and bump `AppDatabase` version
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Grammar Guides, 3x3 Matrices, & Sentence Practice Revision (Lessons 1–16)' (Protocol in workflow.md)

## Phase 3: Documentation Sync & Full Verification

- [ ] Task: Update documentation, defects register, and run full test & lint verification
  - [ ] Update `Docs/Defects.md`, `Docs/psd/`, and `Docs/tests/` for revised 16-lesson curriculum counts and Russian vocab distractors
  - [ ] Run `./gradlew --no-daemon testDebugUnitTest` and verify all tests pass
  - [ ] Run `python3 wiki/lint.py` and verify exit code 0
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Documentation Sync & Full Verification' (Protocol in workflow.md)
