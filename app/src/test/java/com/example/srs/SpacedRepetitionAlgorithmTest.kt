package com.example.srs

import com.example.data.model.SrsReviewEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpacedRepetitionAlgorithmTest {

    private fun createReview(
        repetition: Int = 0,
        intervalDays: Int = 1,
        easeFactor: Float = 2.5f
    ) = SrsReviewEntity(
        id = "srs_1",
        itemType = "VOCAB",
        lessonId = "lesson_1",
        prompt = "радити",
        answer = "работать",
        repetition = repetition,
        intervalDays = intervalDays,
        easeFactor = easeFactor
    )

    @Test
    fun `gradeToQuality maps all ReviewGrade values to SM-2 quality scores`() {
        assertEquals(1, SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.AGAIN))
        assertEquals(2, SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.HARD))
        assertEquals(4, SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.GOOD))
        assertEquals(5, SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.EASY))
    }

    @Test
    fun `successful reviews follow SM-2 interval progression 1d then 6d then ease-multiplied`() {
        val initial = createReview(repetition = 0, intervalDays = 1, easeFactor = 2.5f)

        val afterFirst = SpacedRepetitionAlgorithm.calculateNextReview(initial, quality = 4)
        assertEquals(1, afterFirst.repetition)
        assertEquals(1, afterFirst.intervalDays)
        assertEquals(4, afterFirst.lastQuality)
        assertEquals(2.5f, afterFirst.easeFactor, 0.001f)

        val afterSecond = SpacedRepetitionAlgorithm.calculateNextReview(afterFirst, quality = 4)
        assertEquals(2, afterSecond.repetition)
        assertEquals(6, afterSecond.intervalDays)
        assertEquals(2.5f, afterSecond.easeFactor, 0.001f)

        val afterThird = SpacedRepetitionAlgorithm.calculateNextReview(afterSecond, quality = 5)
        assertEquals(3, afterThird.repetition)
        assertEquals(2.6f, afterThird.easeFactor, 0.001f)
        // 6 * 2.6 = 15.6 -> rounded to 16
        assertEquals(16, afterThird.intervalDays)
    }

    @Test
    fun `failed recall below quality 3 resets repetition to 0 and interval to 1 day`() {
        val established = createReview(repetition = 4, intervalDays = 30, easeFactor = 2.5f)

        for (failingQuality in listOf(0, 1, 2)) {
            val result = SpacedRepetitionAlgorithm.calculateNextReview(established, failingQuality)
            assertEquals("Repetition should reset for quality $failingQuality", 0, result.repetition)
            assertEquals("Interval should reset to 1 for quality $failingQuality", 1, result.intervalDays)
            assertEquals(failingQuality, result.lastQuality)
        }
    }

    @Test
    fun `ease factor never drops below minimum 1_3f on repeated blackouts`() {
        var current = createReview(easeFactor = 1.5f)
        repeat(10) {
            current = SpacedRepetitionAlgorithm.calculateNextReview(current, quality = 0)
        }
        assertEquals(1.3f, current.easeFactor, 0.0001f)
    }

    @Test
    fun `quality is clamped to 0 through 5 range`() {
        val initial = createReview()

        val negativeQuality = SpacedRepetitionAlgorithm.calculateNextReview(initial, quality = -5)
        assertEquals(0, negativeQuality.lastQuality)
        assertEquals(0, negativeQuality.repetition)

        val highQuality = SpacedRepetitionAlgorithm.calculateNextReview(initial, quality = 99)
        assertEquals(5, highQuality.lastQuality)
        assertEquals(1, highQuality.repetition)
        assertEquals(2.6f, highQuality.easeFactor, 0.001f)
    }

    @Test
    fun `nextReviewTimestamp is scheduled approximately intervalDays in the future`() {
        val before = System.currentTimeMillis()
        val result = SpacedRepetitionAlgorithm.calculateNextReview(
            createReview(repetition = 1, intervalDays = 1, easeFactor = 2.5f),
            quality = 4
        )
        val after = System.currentTimeMillis()
        val sixDaysMs = 6L * 24L * 60L * 60L * 1000L

        assertTrue(result.nextReviewTimestamp >= before + sixDaysMs)
        assertTrue(result.nextReviewTimestamp <= after + sixDaysMs)
    }
}
