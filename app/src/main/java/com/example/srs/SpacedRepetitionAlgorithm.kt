package com.example.srs

import com.example.data.model.SrsReviewEntity
import kotlin.math.max
import kotlin.math.roundToInt

object SpacedRepetitionAlgorithm {
    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L
    private const val MIN_EASE_FACTOR = 1.3f

    /**
     * Applies SM-2 algorithm to compute next interval, ease factor, and due date.
     * @param current The current review record
     * @param quality Recall quality: 0 (blackout) to 5 (instant perfect recall)
     */
    fun calculateNextReview(
        current: SrsReviewEntity,
        quality: Int
    ): SrsReviewEntity {
        val clampedQuality = quality.coerceIn(0, 5)
        var repetition = current.repetition
        var intervalDays = current.intervalDays
        var easeFactor = current.easeFactor

        // Calculate new ease factor
        val qDiff = 5 - clampedQuality
        val newEaseFactor = max(
            MIN_EASE_FACTOR,
            easeFactor + (0.1f - qDiff * (0.08f + qDiff * 0.02f))
        )

        if (clampedQuality < 3) {
            // Failed recall: reset repetition count, schedule for tomorrow
            repetition = 0
            intervalDays = 1
        } else {
            // Successful recall
            intervalDays = when (repetition) {
                0 -> 1
                1 -> 6
                else -> (intervalDays * newEaseFactor).roundToInt().coerceAtLeast(1)
            }
            repetition += 1
        }

        val nextTimestamp = System.currentTimeMillis() + (intervalDays * ONE_DAY_MS)

        return current.copy(
            repetition = repetition,
            intervalDays = intervalDays,
            easeFactor = newEaseFactor,
            nextReviewTimestamp = nextTimestamp,
            lastQuality = clampedQuality
        )
    }

    /**
     * Converts simple 4-button choice into 0-5 quality score:
     * - Again: 1
     * - Hard: 2
     * - Good: 4
     * - Easy: 5
     */
    fun gradeToQuality(grade: ReviewGrade): Int = when (grade) {
        ReviewGrade.AGAIN -> 1
        ReviewGrade.HARD -> 2
        ReviewGrade.GOOD -> 4
        ReviewGrade.EASY -> 5
    }
}

enum class ReviewGrade(val label: String, val intervalDescription: String) {
    AGAIN("Again", "1d"),
    HARD("Hard", "2d"),
    GOOD("Good", "4d"),
    EASY("Easy", "7d")
}
