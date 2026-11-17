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

    @Test
    fun `deriveVocabRating maps accuracy and latency boundaries to ReviewGrade 1-4`() {
        assertEquals(ReviewGrade.AGAIN, SpacedRepetitionAlgorithm.deriveVocabRating(isCorrect = false, latencyMs = 1000L))
        assertEquals(ReviewGrade.EASY, SpacedRepetitionAlgorithm.deriveVocabRating(isCorrect = true, latencyMs = 2500L))
        assertEquals(ReviewGrade.GOOD, SpacedRepetitionAlgorithm.deriveVocabRating(isCorrect = true, latencyMs = 2501L))
        assertEquals(ReviewGrade.GOOD, SpacedRepetitionAlgorithm.deriveVocabRating(isCorrect = true, latencyMs = 6000L))
        assertEquals(ReviewGrade.HARD, SpacedRepetitionAlgorithm.deriveVocabRating(isCorrect = true, latencyMs = 6001L))
    }

    @Test
    fun `deriveSentenceRating maps accuracy, latency, tile returns, and grammar drawer penalty`() {
        assertEquals(
            ReviewGrade.AGAIN,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = false, latencyMs = 3000L, tileReturns = 0, grammarOpenedMidAttempt = false)
        )
        assertEquals(
            ReviewGrade.AGAIN,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = true, latencyMs = 3000L, tileReturns = 0, grammarOpenedMidAttempt = true)
        )
        assertEquals(
            ReviewGrade.EASY,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = true, latencyMs = 6000L, tileReturns = 0, grammarOpenedMidAttempt = false)
        )
        assertEquals(
            ReviewGrade.GOOD,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = true, latencyMs = 6000L, tileReturns = 2, grammarOpenedMidAttempt = false)
        )
        assertEquals(
            ReviewGrade.GOOD,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = true, latencyMs = 15000L, tileReturns = 2, grammarOpenedMidAttempt = false)
        )
        assertEquals(
            ReviewGrade.HARD,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = true, latencyMs = 15001L, tileReturns = 0, grammarOpenedMidAttempt = false)
        )
        assertEquals(
            ReviewGrade.HARD,
            SpacedRepetitionAlgorithm.deriveSentenceRating(isCorrect = true, latencyMs = 5000L, tileReturns = 3, grammarOpenedMidAttempt = false)
        )
    }

    @Test
    fun `calculateFsrsNextReview updates state, stability, difficulty 1-10, lapses, and retrievability`() {
        val now = 1_000_000_000L
        val newItem = createReview().copy(state = "NEW", stability = 0f, difficulty = 5.0f, reps = 0, lapses = 0)

        val reviewed = SpacedRepetitionAlgorithm.calculateFsrsNextReview(newItem, ReviewGrade.GOOD, nowMs = now)
        assertEquals("REVIEW", reviewed.state)
        assertTrue(reviewed.stability >= 3.0f)
        assertEquals(1, reviewed.reps)
        assertEquals(0, reviewed.lapses)
        assertEquals(now, reviewed.lastReviewedAt)

        val oneDayLater = now + 24L * 60L * 60L * 1000L
        val lapsed = SpacedRepetitionAlgorithm.calculateFsrsNextReview(reviewed, ReviewGrade.AGAIN, nowMs = oneDayLater)
        assertEquals("RELEARNING", lapsed.state)
        assertEquals(1, lapsed.lapses)
        assertTrue(lapsed.stability < reviewed.stability)
        assertTrue(lapsed.difficulty in 1.0f..10.0f)

        val rNow = SpacedRepetitionAlgorithm.retrievability(reviewed, nowMs = now)
        val rLater = SpacedRepetitionAlgorithm.retrievability(reviewed, nowMs = now + 5L * 24L * 60L * 60L * 1000L)
        assertEquals(1.0f, rNow, 0.001f)
        assertTrue(rLater < rNow && rLater > 0f)
    }

    @Test
    fun `buildDailyBlendedQueue enforces 70-30 split, retrievability order, and 30 per day soft cap`() {
        val now = 1_000_000_000L
        val dayMs = 24L * 60L * 60L * 1000L
        val overdue = (1..20).map { i ->
            createReview().copy(
                id = "over_$i",
                lessonId = "lesson_1",
                state = "REVIEW",
                stability = i.toFloat(),
                lastReviewedAt = now - 5L * dayMs,
                nextReviewTimestamp = now - 1_000L
            )
        }
        val activeLessonItems = (1..10).map { i ->
            createReview().copy(
                id = "act_$i",
                lessonId = "lesson_2",
                state = "NEW",
                stability = 0f,
                nextReviewTimestamp = now + dayMs
            )
        }

        val queue = SpacedRepetitionAlgorithm.buildDailyBlendedQueue(
            overdueItems = overdue,
            activeLessonItems = activeLessonItems,
            reviewedTodayCount = 0,
            nowMs = now
        )
        assertEquals(20, queue.size)
        val overdueInQueue = queue.filter { it.id.startsWith("over_") }
        val activeInQueue = queue.filter { it.id.startsWith("act_") }
        assertEquals(14, overdueInQueue.size)
        assertEquals(6, activeInQueue.size)
        // Sorted by ascending retrievability -> lowest stability first
        assertEquals("over_1", overdueInQueue.first().id)

        val capped = SpacedRepetitionAlgorithm.buildDailyBlendedQueue(
            overdueItems = overdue,
            activeLessonItems = activeLessonItems,
            reviewedTodayCount = 27,
            nowMs = now
        )
        assertEquals(3, capped.size)
    }
}
