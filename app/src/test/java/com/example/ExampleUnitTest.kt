package com.example

import com.example.data.importer.CurriculumImporter
import com.example.data.initial.InitialCurriculumData
import com.example.data.model.SrsReviewEntity
import com.example.srs.ReviewGrade
import com.example.srs.SpacedRepetitionAlgorithm
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testGradeCalculationFormula() {
        // Grade formula: (correctCount / 100.0) * 5.0
        fun calculateGrade(correctAnswersInLast100: Int): Float {
            return (correctAnswersInLast100.toFloat() / 100f) * 5.0f
        }

        assertEquals(0.0f, calculateGrade(0), 0.001f)
        assertEquals(1.0f, calculateGrade(20), 0.001f)
        assertEquals(2.5f, calculateGrade(50), 0.001f)
        assertEquals(4.5f, calculateGrade(90), 0.001f)
        assertEquals(5.0f, calculateGrade(100), 0.001f)

        // Grade >= 4.5 unlocks next lesson
        assertTrue(calculateGrade(90) >= 4.5f)
        assertTrue(calculateGrade(89) < 4.5f)
    }

    @Test
    fun testInnerCaseTokenCleaning() {
        fun cleanToken(raw: String): String =
            raw.trim().lowercase().replace(Regex("[\\p{P}\\p{S}]"), "")

        assertEquals("сутра", cleanToken("Сутра,"))
        assertEquals("хоћеш", cleanToken("Хоћеш?"))
        assertEquals("књигу", cleanToken("„Књигу“!"))
        assertEquals("канцеларији", cleanToken("Канцеларији."))
    }

    @Test
    fun testSpacedRepetitionSm2Calculations() {
        val initialItem = SrsReviewEntity(
            id = "test_item",
            itemType = "VOCAB",
            lessonId = "lesson_1",
            prompt = "радити",
            answer = "работать",
            repetition = 0,
            intervalDays = 1,
            easeFactor = 2.5f
        )

        // Perfect recall (Easy -> quality 5)
        val qualityEasy = SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.EASY)
        val reviewedItem = SpacedRepetitionAlgorithm.calculateNextReview(initialItem, qualityEasy)

        assertEquals(1, reviewedItem.repetition)
        assertEquals(1, reviewedItem.intervalDays)
        assertTrue(reviewedItem.easeFactor >= 2.5f)

        // Second recall (Good -> quality 4)
        val reviewedItem2 = SpacedRepetitionAlgorithm.calculateNextReview(
            reviewedItem,
            SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.GOOD)
        )
        assertEquals(2, reviewedItem2.repetition)
        assertEquals(6, reviewedItem2.intervalDays)

        // Failed recall (Again -> quality 1)
        val failedItem = SpacedRepetitionAlgorithm.calculateNextReview(
            reviewedItem2,
            SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.AGAIN)
        )
        assertEquals(0, failedItem.repetition)
        assertEquals(1, failedItem.intervalDays)
    }

    @Test
    fun testCurriculumImporterCsv() {
        val csv = """targetWord,targetWordLatin,sourceTranslation,partOfSpeech,exampleSentence,audioUri
читати,čitati,читать,Глагол,Волим да читам.,
писати,pisati,писать,Глагол,Она пише писмо.,"""

        val result = CurriculumImporter.parseCsv(csv)
        assertEquals(2, result.vocabItems.size)
        assertEquals("читати", result.vocabItems[0].targetWord)
        assertEquals("читать", result.vocabItems[0].sourceTranslation)
        assertEquals("писать", result.vocabItems[1].sourceTranslation)
    }

    @Test
    fun testCurriculumImporterJsonTemplate() {
        val json = CurriculumImporter.getSampleJsonTemplate()
        val result = CurriculumImporter.parseJson(json)

        assertTrue(result.lessons.isNotEmpty())
        assertTrue(result.vocabItems.isNotEmpty())
        assertTrue(result.sentences.isNotEmpty())
        assertEquals("Урок: Модальные глаголы (Хтети / Моћи)", result.lessons[0].title)

        val cyrillicRegex = Regex("[\\p{InCyrillic}]")
        result.sentences.forEach { sentence ->
            assertTrue(
                "Sample JSON sentence tokensJson should be Latin: ${sentence.tokensJson}",
                !cyrillicRegex.containsMatchIn(sentence.tokensJson)
            )
        }
    }

    @Test
    fun testInitialSentencesUseLatinLowercaseTokens() {
        val cyrillicRegex = Regex("[\\p{InCyrillic}]")
        val sentences = InitialCurriculumData.getInitialSentences()
        assertTrue(sentences.isNotEmpty())
        sentences.forEach { sentence ->
            assertTrue(
                "tokensJson should not contain Cyrillic for ${sentence.id}: ${sentence.tokensJson}",
                !cyrillicRegex.containsMatchIn(sentence.tokensJson)
            )
            assertTrue(
                "distractorTokensJson should not contain Cyrillic for ${sentence.id}: ${sentence.distractorTokensJson}",
                !cyrillicRegex.containsMatchIn(sentence.distractorTokensJson)
            )
        }
    }

    @Test
    fun testInitialVocabMeetsLessonVolumeRussianDistractorsAndLatinExamples() {
        val cyrillicRegex = Regex("[\\p{InCyrillic}]")
        val moshi = com.squareup.moshi.Moshi.Builder().build()
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(listType)

        val lessons = InitialCurriculumData.getInitialLessons()
        val vocab = InitialCurriculumData.getInitialVocab()
        assertEquals(16, lessons.size)

        lessons.forEach { lesson ->
            val lessonVocab = vocab.filter { it.lessonId == lesson.id }
            assertTrue(
                "Lesson ${lesson.id} should have 10..15 vocab items, got ${lessonVocab.size}",
                lessonVocab.size in 10..15
            )
            assertEquals(
                "Lesson ${lesson.id} vocabCount should match actual seeded items",
                lessonVocab.size,
                lesson.vocabCount
            )
        }

        vocab.forEach { item ->
            assertTrue(
                "targetWordLatin should not be blank for ${item.id}",
                item.targetWordLatin.isNotBlank() && !cyrillicRegex.containsMatchIn(item.targetWordLatin)
            )
            assertTrue(
                "exampleSentence should be non-blank Latin Serbian for ${item.id}: ${item.exampleSentence}",
                item.exampleSentence.isNotBlank() && !cyrillicRegex.containsMatchIn(item.exampleSentence)
            )
            val distractors = adapter.fromJson(item.distractorsJson) ?: emptyList()
            assertEquals(
                "distractorsJson should have exactly 3 distractors for ${item.id}",
                3,
                distractors.size
            )
            assertEquals(
                "distractorsJson should have 3 distinct entries for ${item.id}",
                3,
                distractors.toSet().size
            )
            assertTrue(
                "distractorsJson should not contain sourceTranslation for ${item.id}",
                item.sourceTranslation !in distractors
            )
            distractors.forEach { d ->
                assertTrue(
                    "Distractor '$d' for ${item.id} should be Russian (Cyrillic)",
                    cyrillicRegex.containsMatchIn(d)
                )
            }
        }
    }

    @Test
    fun testInitialGrammarGuidesHave9CellLatinMatricesForAll16Lessons() {
        val cyrillicRegex = Regex("[\\p{InCyrillic}]")
        val moshi = com.squareup.moshi.Moshi.Builder().build()
        val matrixAdapter = moshi.adapter(com.example.data.model.ConjugationMatrixData::class.java)
        val guides = InitialCurriculumData.getInitialGrammarGuides()
        assertEquals(16, guides.size)

        val expectedTenses = setOf("Past", "Present", "Future")
        val expectedForms = setOf("Affirmative (+)", "Negative (-)", "Interrogative (?)")

        guides.forEach { guide ->
            val matrix = matrixAdapter.fromJson(guide.matrixJson)
            assertTrue("Matrix should parse for ${guide.lessonId}", matrix != null)
            assertEquals("Matrix should have 9 cells for ${guide.lessonId}", 9, matrix!!.cells.size)
            assertEquals(expectedTenses, matrix.cells.map { it.tense }.toSet())
            assertEquals(expectedForms, matrix.cells.map { it.formType }.toSet())
            matrix.cells.forEach { cell ->
                assertTrue(
                    "Cell formula should be non-blank Latin for ${guide.lessonId}: ${cell.formula}",
                    cell.formula.isNotBlank() && !cyrillicRegex.containsMatchIn(cell.formula)
                )
                assertTrue(
                    "Cell exampleSerbianLatin should be non-blank Latin for ${guide.lessonId}: ${cell.exampleSerbianLatin}",
                    cell.exampleSerbianLatin.isNotBlank() && !cyrillicRegex.containsMatchIn(cell.exampleSerbianLatin)
                )
            }
        }
    }

    @Test
    fun testInitialSentencesCoverAll9CoordinatesPerLessonWithLessonSpecificContent() {
        val moshi = com.squareup.moshi.Moshi.Builder().build()
        val listType = com.squareup.moshi.Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(listType)

        val lessons = InitialCurriculumData.getInitialLessons()
        val sentences = InitialCurriculumData.getInitialSentences()
        val expectedCoords = setOf(
            "PAST_AFFIRMATIVE", "PAST_NEGATIVE", "PAST_INTERROGATIVE",
            "PRESENT_AFFIRMATIVE", "PRESENT_NEGATIVE", "PRESENT_INTERROGATIVE",
            "FUTURE_AFFIRMATIVE", "FUTURE_NEGATIVE", "FUTURE_INTERROGATIVE"
        )

        lessons.forEach { lesson ->
            val lessonSentences = sentences.filter { it.lessonId == lesson.id }
            assertEquals("Lesson ${lesson.id} should have 9 sentences", 9, lessonSentences.size)
            assertEquals("Lesson ${lesson.id} sentenceCount should be 9", 9, lesson.sentenceCount)
            assertEquals(expectedCoords, lessonSentences.map { it.matrixCoordinateKey() }.toSet())
            lessonSentences.forEach { s ->
                val distractors = adapter.fromJson(s.distractorTokensJson) ?: emptyList()
                assertTrue(
                    "Sentence ${s.id} should have 1..2 distractors, got ${distractors.size}",
                    distractors.size in 1..2
                )
            }
        }
    }
}
