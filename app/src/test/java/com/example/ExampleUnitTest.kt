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
    fun testInitialVocabUsesLatinDistractorsAndExamples() {
        val cyrillicRegex = Regex("[\\p{InCyrillic}]")
        val vocab = InitialCurriculumData.getInitialVocab()
        assertTrue(vocab.isNotEmpty())
        vocab.forEach { item ->
            assertTrue(
                "distractorsJson should not contain Cyrillic for ${item.id}: ${item.distractorsJson}",
                !cyrillicRegex.containsMatchIn(item.distractorsJson)
            )
            assertTrue(
                "exampleSentence should not contain Cyrillic for ${item.id}: ${item.exampleSentence}",
                !cyrillicRegex.containsMatchIn(item.exampleSentence)
            )
        }
    }
}
