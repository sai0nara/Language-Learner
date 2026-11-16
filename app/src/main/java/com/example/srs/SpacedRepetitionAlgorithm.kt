package com.example.srs

import com.example.data.model.SrsReviewEntity
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

object SpacedRepetitionAlgorithm {
    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L
    private const val MIN_EASE_FACTOR = 1.3f

    /**
     * FSRS v5 retrievability: R = (1 + (19/81) * (t / S))^-0.5
     */
    fun retrievability(
        item: SrsReviewEntity,
        nowMs: Long = System.currentTimeMillis()
    ): Float {
        val last = item.lastReviewedAt ?: return 0f
        val elapsedDays = max(0.0, (nowMs - last).toDouble() / ONE_DAY_MS.toDouble())
        val stabilityDays = max(0.1, item.stability.toDouble())
        return (1.0 + (19.0 / 81.0) * (elapsedDays / stabilityDays)).pow(-0.5).toFloat()
    }

    /**
     * Implicit Vocabulary (2x2 grid) rating mapping (1-4).
     */
    fun deriveVocabRating(isCorrect: Boolean, latencyMs: Long): ReviewGrade = when {
        !isCorrect -> ReviewGrade.AGAIN
        latencyMs > 6_000L -> ReviewGrade.HARD
        latencyMs > 2_500L -> ReviewGrade.GOOD
        else -> ReviewGrade.EASY
    }

    /**
     * Implicit Sentence Construction (Word Tiles) rating mapping (1-4).
     */
    fun deriveSentenceRating(
        isCorrect: Boolean,
        latencyMs: Long,
        tileReturns: Int,
        grammarOpenedMidAttempt: Boolean
    ): ReviewGrade = when {
        !isCorrect || grammarOpenedMidAttempt -> ReviewGrade.AGAIN
        latencyMs > 15_000L || tileReturns > 2 -> ReviewGrade.HARD
        latencyMs > 6_000L || tileReturns > 0 -> ReviewGrade.GOOD
        else -> ReviewGrade.EASY
    }

    /**
     * FSRS v5 state & interval transition for ReviewGrade (1-4).
     */
    fun calculateFsrsNextReview(
        current: SrsReviewEntity,
        rating: ReviewGrade,
        nowMs: Long = System.currentTimeMillis()
    ): SrsReviewEntity {
        val elapsedDays = if (current.lastReviewedAt != null) {
            ((nowMs - current.lastReviewedAt) / ONE_DAY_MS).toInt().coerceAtLeast(0)
        } else {
            0
        }
        val baseDifficulty = current.difficulty.coerceIn(1.0f, 10.0f)
        val newDifficulty = when (rating) {
            ReviewGrade.AGAIN -> (baseDifficulty + 1.0f).coerceIn(1.0f, 10.0f)
            ReviewGrade.HARD -> (baseDifficulty + 0.4f).coerceIn(1.0f, 10.0f)
            ReviewGrade.GOOD -> baseDifficulty
            ReviewGrade.EASY -> (baseDifficulty - 0.6f).coerceIn(1.0f, 10.0f)
        }

        val wasReview = current.state == "REVIEW"
        var newLapses = current.lapses
        var newReps = current.reps
        val newState: String
        val newStability: Float
        val scheduledDays: Int

        when (rating) {
            ReviewGrade.AGAIN -> {
                if (wasReview || current.reps > 0) newLapses += 1
                newState = "RELEARNING"
                newStability = if (current.stability > 0f) max(0.5f, current.stability * 0.3f) else 0.5f
                scheduledDays = 1
            }
            ReviewGrade.HARD -> {
                newState = if (wasReview) "REVIEW" else "LEARNING"
                val baseS = if (current.stability > 0f) current.stability else 1.0f
                newStability = max(1.0f, baseS * 1.2f)
                scheduledDays = newStability.roundToInt().coerceAtLeast(1)
                newReps += 1
            }
            ReviewGrade.GOOD -> {
                newState = "REVIEW"
                newStability = if (current.stability <= 0f) {
                    3.0f
                } else {
                    max(1.0f, current.stability * (1.8f + (10.0f - newDifficulty) * 0.1f))
                }
                scheduledDays = newStability.roundToInt().coerceAtLeast(1)
                newReps += 1
            }
            ReviewGrade.EASY -> {
                newState = "REVIEW"
                newStability = if (current.stability <= 0f) {
                    7.0f
                } else {
                    max(4.0f, current.stability * (2.5f + (10.0f - newDifficulty) * 0.15f))
                }
                scheduledDays = newStability.roundToInt().coerceAtLeast(4)
                newReps += 1
            }
        }

        val nextTimestamp = nowMs + scheduledDays * ONE_DAY_MS
        return current.copy(
            state = newState,
            stability = newStability,
            difficulty = newDifficulty,
            elapsedDays = elapsedDays,
            scheduledDays = scheduledDays,
            reps = newReps,
            lapses = newLapses,
            lastReviewedAt = nowMs,
            repetition = if (rating == ReviewGrade.AGAIN) 0 else newReps,
            intervalDays = scheduledDays,
            nextReviewTimestamp = nextTimestamp,
            lastQuality = gradeToQuality(rating)
        )
    }

    /**
     * Builds the DAILY_REVIEW blended queue:
     * - 20-item session capped at 30 REVIEW items/day
     * - Up to 70% (max 14) overdue items sorted by ascending retrievability R
     * - At least 30% (min 6) active items from the current IN_PROGRESS lesson
     */
    fun buildDailyBlendedQueue(
        overdueItems: List<SrsReviewEntity>,
        activeLessonItems: List<SrsReviewEntity>,
        reviewedTodayCount: Int,
        nowMs: Long = System.currentTimeMillis(),
        sessionSize: Int = 20,
        dailyCap: Int = 30
    ): List<SrsReviewEntity> {
        val remainingToday = (dailyCap - reviewedTodayCount).coerceAtLeast(0)
        val targetSize = minOf(sessionSize, remainingToday)
        if (targetSize == 0) return emptyList()

        val minActiveSlots = minOf(targetSize, activeLessonItems.size, maxOf(6, (targetSize * 3) / 10))
        val maxOverdueSlots = minOf((targetSize * 7) / 10, targetSize - minActiveSlots).coerceAtLeast(0)
        val sortedOverdue = overdueItems
            .filter { it.nextReviewTimestamp <= nowMs }
            .sortedBy { retrievability(it, nowMs) }
            .take(maxOverdueSlots)

        val chosenIds = sortedOverdue.map { it.id }.toHashSet()
        val activeTake = activeLessonItems
            .filter { it.id !in chosenIds }
            .take(targetSize - sortedOverdue.size)

        return sortedOverdue + activeTake
    }

    /**
     * Applies SM-2-compatible quality review (used by standalone grading) while keeping FSRS fields synced.
     */
    fun calculateNextReview(
        current: SrsReviewEntity,
        quality: Int
    ): SrsReviewEntity {
        val clampedQuality = quality.coerceIn(0, 5)
        var repetition = current.repetition
        var intervalDays = current.intervalDays
        val easeFactor = current.easeFactor

        val qDiff = 5 - clampedQuality
        val newEaseFactor = max(
            MIN_EASE_FACTOR,
            easeFactor + (0.1f - qDiff * (0.08f + qDiff * 0.02f))
        )

        if (clampedQuality < 3) {
            repetition = 0
            intervalDays = 1
        } else {
            intervalDays = when (repetition) {
                0 -> 1
                1 -> 6
                else -> (intervalDays * newEaseFactor).roundToInt().coerceAtLeast(1)
            }
            repetition += 1
        }

        val nowMs = System.currentTimeMillis()
        val nextTimestamp = nowMs + (intervalDays * ONE_DAY_MS)
        val grade = when {
            clampedQuality < 3 -> ReviewGrade.AGAIN
            clampedQuality == 3 -> ReviewGrade.HARD
            clampedQuality == 4 -> ReviewGrade.GOOD
            else -> ReviewGrade.EASY
        }
        val fsrs = calculateFsrsNextReview(current, grade, nowMs)

        return current.copy(
            repetition = repetition,
            intervalDays = intervalDays,
            easeFactor = newEaseFactor,
            nextReviewTimestamp = nextTimestamp,
            lastQuality = clampedQuality,
            state = fsrs.state,
            stability = fsrs.stability,
            difficulty = fsrs.difficulty,
            elapsedDays = fsrs.elapsedDays,
            scheduledDays = intervalDays,
            reps = fsrs.reps,
            lapses = fsrs.lapses,
            lastReviewedAt = nowMs
        )
    }

    fun gradeToQuality(grade: ReviewGrade): Int = when (grade) {
        ReviewGrade.AGAIN -> 1
        ReviewGrade.HARD -> 2
        ReviewGrade.GOOD -> 4
        ReviewGrade.EASY -> 5
    }
}

enum class ReviewGrade(val label: String, val intervalDescription: String, val ratingValue: Int) {
    AGAIN("Again", "1d", 1),
    HARD("Hard", "2d", 2),
    GOOD("Good", "4d", 3),
    EASY("Easy", "7d", 4)
}
