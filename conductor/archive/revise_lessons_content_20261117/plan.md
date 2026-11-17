# Implementation Plan: Revise Lessons Content to Suit Requirements

## Phase 1: Vocabulary Content Revision (Lessons 1–16)

- [x] Task: Revise `InitialCurriculumData.getInitialVocab()` and `CurriculumImporter` sample template to provide 10–15 vocab items per lesson with 3 Russian translation distractors and Latin Serbian example sentences 6ec2905
  - [x] Write Tests: Add/update unit tests (`ExampleUnitTest`, `CurriculumImporterTest`, `RoomPersistenceAndRepositoryTest`) verifying each lesson (`lesson_1`..`lesson_16`) has 10–15 vocab items, every `distractorsJson` has 3 distinct Russian translation strings (not matching `sourceTranslation`), and `exampleSentence` is non-blank Latin Serbian
  - [x] Implement: Update `getInitialVocab()` and `getInitialLessons()` (`vocabCount`, `sentenceCount`) in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt`, and update sample vocab `distractors` in `app/src/main/java/com/example/data/importer/CurriculumImporter.kt`
- [x] Task: Conductor - User Manual Verification 'Phase 1: Vocabulary Content Revision (Lessons 1–16)' (Protocol in workflow.md) 6ec2905

## Phase 2: Grammar Guides, 3x3 Matrices, & Sentence Practice Revision (Lessons 1–16)

- [x] Task: Populate full 3x3 `ConjugationMatrixData` and Latin Serbian examples in `getInitialGrammarGuides()` for all 16 lessons 1fc932b
  - [x] Write Tests: Add unit tests verifying all 16 `GrammarGuideEntity` entries have non-empty 9-cell `ConjugationMatrixData` covering all 3 tenses × 3 forms, with Latin `formula` and `exampleSerbianLatin`, and no Cyrillic Serbian examples in `explanationMarkdown`
  - [x] Implement: Author lesson-specific 3x3 `ConjugationMatrixData` and Latin-Serbian `explanationMarkdown` for `lesson_1`..`lesson_16` in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt`
- [x] Task: Author 9 lesson-specific sentences per lesson across all 9 matrix coordinates in `getInitialSentences()` 1fc932b
  - [x] Write Tests: Add unit tests verifying each lesson (`lesson_1`..`lesson_16`) has 9 distinct sentences covering all 9 `(matrixTense × matrixForm)` coordinates, with lowercase Latin `tokensJson`, 1–2 lowercase Latin `distractorTokensJson`, and Latin Serbian terms in `grammarHint`
  - [x] Implement: Replace generic `raditi` template fallbacks with lesson-specific sentences across all 16 lessons in `app/src/main/java/com/example/data/initial/InitialCurriculumData.kt` and bump `AppDatabase` version
- [x] Task: Conductor - User Manual Verification 'Phase 2: Grammar Guides, 3x3 Matrices, & Sentence Practice Revision (Lessons 1–16)' (Protocol in workflow.md) 1fc932b

## Phase 3: Documentation Sync & Full Verification

- [x] Task: Update documentation, defects register, and run full test & lint verification 51ebfd9
  - [x] Update `Docs/Defects.md`, `Docs/psd/`, and `Docs/tests/` for revised 16-lesson curriculum counts and Russian vocab distractors
  - [x] Run `./gradlew --no-daemon testDebugUnitTest` and verify all tests pass
  - [x] Run `python3 wiki/lint.py` and verify exit code 0
- [x] Task: Conductor - User Manual Verification 'Phase 3: Documentation Sync & Full Verification' (Protocol in workflow.md)
