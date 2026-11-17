# Specification: Revise Lessons Content to Suit Requirements

## Overview
Revise the offline seed curriculum (`InitialCurriculumData.kt`) and sample importer templates (`CurriculumImporter.kt`) across all 16 Polyglot lessons so that vocabulary drills, 3x3 grammar guides/matrices, and sentence construction practice align with the product requirements (`Docs/prd/vocab-drills.md`, `Docs/prd/grammar-matrices.md`, `Docs/prd/sentence-practice.md`, `Docs/prd/curriculum-progression.md`) and Defect #2 in `Docs/Defects.md`.

## Functional Requirements
1. **Vocabulary Items (`InitialCurriculumData.getInitialVocab()`)**:
   - Provide **10–15 core vocabulary items per lesson** across all **16 lessons** (`lesson_1`..`lesson_16`).
   - Each `VocabItemEntity` must have:
     - `targetWordLatin`: non-blank Latin Serbian word/phrase.
     - `sourceTranslation`: Russian translation.
     - `distractorsJson`: **3 distinct Russian translation distractors** (same language and part-of-speech category as `sourceTranslation`, never Serbian words in Cyrillic or Latin) so the 2x2 multiple-choice grid presents 4 plausible Russian translations.
     - `exampleSentence`: contextual example sentence in **Latin Serbian** (no Cyrillic characters).
   - Update `CurriculumImporter.getSampleJsonTemplate()` vocab `distractors` to Russian translations as well.
2. **Grammar Guides & 3x3 Conjugation Matrices (`InitialCurriculumData.getInitialGrammarGuides()`)**:
   - Provide a complete `GrammarGuideEntity` for each of the **16 lessons** (`g_1`..`g_16`).
   - All Serbian words/examples inside `title`, `summary`, and `explanationMarkdown` must use the **Latin alphabet** (while Russian explanations remain in Russian).
   - Every lesson's `matrixJson` must serialize a valid 9-cell `ConjugationMatrixData` (`Past`, `Present`, `Future` × `Affirmative (+)`, `Negative (-)`, `Interrogative (?)`) tailored to that lesson's verb/grammar focus, with Latin `formula` and `exampleSerbianLatin`.
3. **Sentence Practice Items (`InitialCurriculumData.getInitialSentences()`)**:
   - Provide **9 lesson-specific sentences per lesson** across all **16 lessons** (144 sentences total), covering all 9 `(matrixTense × matrixForm)` coordinates (`PAST|PRESENT|FUTURE` × `AFFIRMATIVE|NEGATIVE|INTERROGATIVE`) per lesson.
   - Each `SentenceEntity` must have:
     - `sourceSentence` in Russian and `targetSentenceLatin` in Latin Serbian.
     - `tokensJson`: lowercase, punctuation-stripped Latin tokens matching `targetSentenceLatin`.
     - `distractorTokensJson`: **1–2 lowercase Latin morphological distractors** per sentence.
     - `grammarHint`: Russian grammatical hint with all Serbian terms/examples in Latin.
4. **Lesson Metadata & Database Reseed**:
   - Update `LessonEntity` `vocabCount` and `sentenceCount` in `getInitialLessons()` to match the actual seeded counts per lesson.
   - Bump `AppDatabase` version so existing development installs re-seed the revised curriculum cleanly.

## Acceptance Criteria
- Every lesson (`lesson_1`..`lesson_16`) has 10–15 vocabulary items, a non-empty 9-cell 3x3 `ConjugationMatrixData` grammar guide, and 9 lesson-specific sentences covering all 9 matrix coordinates.
- All `VocabItemEntity.distractorsJson` entries across all 16 lessons contain 3 Russian translation distractors (and do not duplicate `sourceTranslation`).
- No Serbian text in `targetWordLatin`, `exampleSentence`, `tokensJson`, `distractorTokensJson`, `grammarHint`, `explanationMarkdown`, or `ConjugationCell` (`formula`, `exampleSerbianLatin`, `notes`) uses Cyrillic characters.
- `./gradlew --no-daemon testDebugUnitTest` passes and `python3 wiki/lint.py` exits with code 0.

## Out of Scope
- Modifying the UI layout of `VocabDrillScreen`, `GrammarMatrixScreen`, or `SentencePracticeScreen`.
- Changing Room entity column definitions.
