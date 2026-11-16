package com.example.data.repository

import com.example.data.importer.ImportResult
import com.example.data.local.AppDatabase
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.LessonStatus
import com.example.data.model.MatrixCellStat
import com.example.data.model.MatrixCellStatus
import com.example.data.model.SentenceEntity
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity
import com.example.data.initial.InitialCurriculumData
import com.example.srs.ReviewGrade
import com.example.srs.SpacedRepetitionAlgorithm
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

class CurriculumRepository(private val db: AppDatabase) {
    val allLessons: Flow<List<LessonEntity>> = db.lessonDao().getAllLessons()

    fun getVocabForLesson(lessonId: String): Flow<List<VocabItemEntity>> =
        db.vocabDao().getVocabForLesson(lessonId)

    suspend fun getVocabForLessonSync(lessonId: String): List<VocabItemEntity> =
        db.vocabDao().getVocabForLessonSync(lessonId)

    fun getGrammarGuideForLesson(lessonId: String): Flow<GrammarGuideEntity?> =
        db.grammarDao().getGrammarGuideForLesson(lessonId)

    fun getSentencesForLesson(lessonId: String): Flow<List<SentenceEntity>> =
        db.sentenceDao().getSentencesForLesson(lessonId)

    fun getDueSrsItems(currentTimestamp: Long = System.currentTimeMillis()): Flow<List<SrsReviewEntity>> =
        db.srsDao().getDueItems(currentTimestamp)

    fun getDueSrsCount(currentTimestamp: Long = System.currentTimeMillis()): Flow<Int> =
        db.srsDao().getDueCount(currentTimestamp)

    suspend fun ensureInitialDataLoaded() = withContext(Dispatchers.IO) {
        val existing = db.lessonDao().getAllLessons().first()
        if (existing.isEmpty()) {
            val lessons = InitialCurriculumData.getInitialLessons()
            val vocab = InitialCurriculumData.getInitialVocab()
            val guides = InitialCurriculumData.getInitialGrammarGuides()
            val sentences = InitialCurriculumData.getInitialSentences()

            db.lessonDao().insertLessons(lessons)
            db.vocabDao().insertVocabItems(vocab)
            db.grammarDao().insertGrammarGuides(guides)
            db.sentenceDao().insertSentences(sentences)

            // Seed initial SRS cards from vocab
            val srsItems = vocab.mapIndexed { idx, v ->
                SrsReviewEntity(
                    id = v.id,
                    itemType = "VOCAB",
                    lessonId = v.lessonId,
                    prompt = v.targetWord,
                    answer = v.sourceTranslation,
                    secondaryText = v.targetWordLatin,
                    repetition = 0,
                    intervalDays = 1,
                    easeFactor = 2.5f,
                    nextReviewTimestamp = System.currentTimeMillis() - (idx * 60_000L) // Some due right away
                )
            }
            db.srsDao().insertAll(srsItems)
        }
    }

    /**
     * Records an answer (right or wrong) for the given lesson.
     * Keeps the last 100 answers, calculates score = (correctCount / 100) * 5.0.
     * When score >= 4.5 (90+ correct out of 100), unlocks the next lesson.
     */
    suspend fun recordAnswer(lessonId: String, isCorrect: Boolean): Float = withContext(Dispatchers.IO) {
        if (db.lessonDao().getLessonById(lessonId) == null) return@withContext 0f
        // 1. Record answer in history
        db.lessonAnswerDao().insertAnswer(
            LessonAnswerEntity(lessonId = lessonId, isCorrect = isCorrect)
        )

        // 2. Prune answers older than 100
        db.lessonAnswerDao().pruneOldAnswers(lessonId)

        // 3. Calculate score based on last 100
        val recentAnswers = db.lessonAnswerDao().getRecentAnswers(lessonId, limit = 100)
        val correctCount = recentAnswers.count { it.isCorrect }
        val totalCount = recentAnswers.size

        // Total count of right answers divided by 100 and multiplied by 5
        val score = (correctCount.toFloat() / 100f) * 5.0f
        val isCompleted = score >= 4.5f

        db.lessonDao().updateGradeStats(
            id = lessonId,
            completed = isCompleted,
            score = score,
            correct = correctCount,
            total = totalCount
        )

        // If grade is 4.5 or higher, unlock the next lesson
        if (isCompleted) {
            val current = db.lessonDao().getLessonById(lessonId)
            if (current != null) {
                db.lessonDao().unlockLessonByOrder(current.orderIndex + 1)
            }
        }

        score
    }

    suspend fun recordSrsResult(item: SrsReviewEntity, grade: ReviewGrade) = withContext(Dispatchers.IO) {
        val quality = SpacedRepetitionAlgorithm.gradeToQuality(grade)
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(item, quality)
        db.srsDao().insertOrUpdate(updated)
    }

    /**
     * First-Attempt Scoring Guard:
     * Re-tests of AGAIN items within the same session do not inflate FSRS stability/intervals.
     */
    suspend fun recordImplicitSrsAttempt(
        itemId: String,
        itemType: String,
        lessonId: String,
        prompt: String,
        answer: String,
        secondaryText: String = "",
        rating: ReviewGrade,
        isFirstAttemptInSession: Boolean,
        nowMs: Long = System.currentTimeMillis()
    ) = withContext(Dispatchers.IO) {
        if (!isFirstAttemptInSession) return@withContext
        val existing = db.srsDao().getSrsItemById(itemId) ?: SrsReviewEntity(
            id = itemId,
            itemType = itemType,
            lessonId = lessonId,
            prompt = prompt,
            answer = answer,
            secondaryText = secondaryText,
            nextReviewTimestamp = nowMs
        )
        val updated = SpacedRepetitionAlgorithm.calculateFsrsNextReview(existing, rating, nowMs)
        db.srsDao().insertOrUpdate(updated)
    }

    suspend fun buildDailyBlendedReviewQueue(
        nowMs: Long = System.currentTimeMillis()
    ): List<SrsReviewEntity> = withContext(Dispatchers.IO) {
        val lessons = db.lessonDao().getAllLessons().first()
        val unlockedIds = lessons.filter { it.isUnlocked }.map { it.id }.toSet()
        val activeLesson = lessons.firstOrNull { it.isUnlocked && !it.isCompleted }
            ?: lessons.firstOrNull { it.isUnlocked }

        val allItems = db.srsDao().getAllSrsItemsSync()
        val overdue = allItems.filter { it.lessonId in unlockedIds && it.nextReviewTimestamp <= nowMs }
        val activeItems = if (activeLesson != null) {
            allItems.filter { it.lessonId == activeLesson.id }
        } else {
            emptyList()
        }

        val cal = Calendar.getInstance().apply {
            timeInMillis = nowMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val reviewedToday = db.srsDao().countReviewedSince(cal.timeInMillis)
        SpacedRepetitionAlgorithm.buildDailyBlendedQueue(
            overdueItems = overdue,
            activeLessonItems = activeItems,
            reviewedTodayCount = reviewedToday,
            nowMs = nowMs
        )
    }

    private val moshi = Moshi.Builder().build()
    private val matrixStatsMapType = Types.newParameterizedType(
        Map::class.java,
        String::class.java,
        MatrixCellStat::class.java
    )
    private val matrixStatsAdapter = moshi.adapter<Map<String, MatrixCellStat>>(matrixStatsMapType)

    fun parseMatrixStats(json: String): Map<String, MatrixCellStat> {
        if (json.isBlank() || json == "{}") return emptyMap()
        return try {
            matrixStatsAdapter.fromJson(json) ?: emptyMap()
        } catch (_: Exception) {
            emptyMap()
        }
    }

    suspend fun recordMatrixCellAttempt(
        lessonId: String,
        coordinateKey: String,
        isCorrect: Boolean,
        latencyMs: Long
    ) = withContext(Dispatchers.IO) {
        val lesson = db.lessonDao().getLessonById(lessonId) ?: return@withContext
        val currentMap = parseMatrixStats(lesson.matrixStatsJson).toMutableMap()
        val prev = currentMap[coordinateKey] ?: MatrixCellStat()
        val newAttempts = prev.attempts + 1
        val newCorrect = prev.correct + if (isCorrect) 1 else 0
        val newAccuracy = newCorrect.toFloat() / newAttempts.toFloat()
        val newAvgLatency = ((prev.avgLatencyMs * prev.attempts) + latencyMs) / newAttempts
        val cellStatus = computeCellStatus(newAttempts, newAccuracy, newAvgLatency)
        currentMap[coordinateKey] = MatrixCellStat(
            attempts = newAttempts,
            correct = newCorrect,
            accuracy = newAccuracy,
            avgLatencyMs = newAvgLatency,
            cellStatus = cellStatus
        )
        evaluateTwoTierGatesInternal(lesson.copy(matrixStatsJson = matrixStatsAdapter.toJson(currentMap)))
    }

    suspend fun evaluateTwoTierGates(lessonId: String) = withContext(Dispatchers.IO) {
        val lesson = db.lessonDao().getLessonById(lessonId) ?: return@withContext
        evaluateTwoTierGatesInternal(lesson)
    }

    private suspend fun evaluateTwoTierGatesInternal(lesson: LessonEntity) {
        val vocabItems = db.vocabDao().getVocabForLessonSync(lesson.id)
        val srsItems = db.srsDao().getSrsItemsByLessonSync(lesson.id)
        val vocabMastery = if (vocabItems.isEmpty()) {
            lesson.vocabMastery
        } else {
            val stableCount = srsItems.count { it.itemType == "VOCAB" && it.state == "REVIEW" && it.stability >= 3f }
            stableCount.toFloat() / vocabItems.size.toFloat()
        }
        val unresolvedLapses = srsItems.count { it.state == "RELEARNING" }

        val recent = db.lessonAnswerDao().getRecentAnswers(lesson.id, limit = 100)
        val practiceScore = if (recent.isEmpty()) {
            lesson.practiceScore
        } else {
            recent.count { it.isCorrect }.toFloat() / recent.size.toFloat()
        }

        val matrixMap = parseMatrixStats(lesson.matrixStatsJson)
        val allNineMastered = ALL_MATRIX_COORDS.all { coord ->
            val stat = matrixMap[coord]
            stat != null && stat.attempts >= 3 && stat.accuracy >= 0.80f
        }
        val medianLatency = if (matrixMap.isEmpty()) {
            lesson.medianAssemblyMs
        } else {
            val sorted = matrixMap.values.filter { it.attempts > 0 }.map { it.avgLatencyMs }.sorted()
            if (sorted.isEmpty()) 0L else sorted[sorted.size / 2]
        }

        val isMastered = vocabMastery >= 0.85f &&
            allNineMastered &&
            unresolvedLapses == 0 &&
            medianLatency in 1L..12_000L

        val unlockNext = practiceScore >= 0.70f || lesson.masteryScore >= 4.5f
        if (unlockNext) {
            db.lessonDao().unlockLessonByOrder(lesson.orderIndex + 1)
        }

        val newStatus = when {
            isMastered -> LessonStatus.MASTERED.name
            !lesson.isUnlocked -> LessonStatus.LOCKED.name
            recent.isNotEmpty() || matrixMap.isNotEmpty() -> LessonStatus.IN_PROGRESS.name
            else -> LessonStatus.AVAILABLE.name
        }

        db.lessonDao().updatePolyglotProgress(
            id = lesson.id,
            status = newStatus,
            completed = isMastered || lesson.isCompleted,
            unlocked = lesson.isUnlocked,
            vocabMastery = vocabMastery,
            practiceScore = practiceScore,
            unresolvedLapses = unresolvedLapses,
            medianAssemblyMs = medianLatency,
            matrixStatsJson = lesson.matrixStatsJson
        )
    }

    fun getWeakMatrixCoordinates(lesson: LessonEntity): Set<String> =
        parseMatrixStats(lesson.matrixStatsJson)
            .filterValues { it.cellStatus == MatrixCellStatus.WEAK }
            .keys

    fun filterWeakSentences(
        sentences: List<SentenceEntity>,
        weakCoords: Set<String>,
        limit: Int = 8
    ): List<SentenceEntity> =
        sentences.filter { it.matrixCoordinateKey() in weakCoords }.take(limit)

    companion object {
        val ALL_MATRIX_COORDS = listOf(
            "PAST_AFFIRMATIVE", "PAST_NEGATIVE", "PAST_INTERROGATIVE",
            "PRESENT_AFFIRMATIVE", "PRESENT_NEGATIVE", "PRESENT_INTERROGATIVE",
            "FUTURE_AFFIRMATIVE", "FUTURE_NEGATIVE", "FUTURE_INTERROGATIVE"
        )

        fun computeCellStatus(attempts: Int, accuracy: Float, avgLatencyMs: Long): MatrixCellStatus = when {
            attempts < 3 -> MatrixCellStatus.UNTESTED
            accuracy < 0.70f -> MatrixCellStatus.WEAK
            accuracy >= 0.85f && avgLatencyMs <= 12_000L -> MatrixCellStatus.MASTERED
            else -> MatrixCellStatus.PROFICIENT
        }
    }

    suspend fun enrollInSrs(vocab: VocabItemEntity) = withContext(Dispatchers.IO) {
        val existing = db.srsDao().getSrsItemById(vocab.id)
        if (existing == null) {
            val newItem = SrsReviewEntity(
                id = vocab.id,
                itemType = "VOCAB",
                lessonId = vocab.lessonId,
                prompt = vocab.targetWord,
                answer = vocab.sourceTranslation,
                secondaryText = vocab.targetWordLatin,
                repetition = 0,
                intervalDays = 1,
                easeFactor = 2.5f,
                nextReviewTimestamp = System.currentTimeMillis()
            )
            db.srsDao().insertOrUpdate(newItem)
        }
    }

    suspend fun importCurriculum(result: ImportResult) = withContext(Dispatchers.IO) {
        if (result.lessons.isNotEmpty()) {
            // Ensure first imported lesson is unlocked by default
            val adjusted = result.lessons.mapIndexed { idx, lesson ->
                lesson.copy(isUnlocked = if (idx == 0) true else lesson.isUnlocked, masteryScore = 0f)
            }
            db.lessonDao().insertLessons(adjusted)
        }
        if (result.vocabItems.isNotEmpty()) {
            db.vocabDao().insertVocabItems(result.vocabItems)
            val srsItems = result.vocabItems.map { v ->
                SrsReviewEntity(
                    id = v.id,
                    itemType = "VOCAB",
                    lessonId = v.lessonId,
                    prompt = v.targetWord,
                    answer = v.sourceTranslation,
                    secondaryText = v.targetWordLatin,
                    repetition = 0,
                    intervalDays = 1,
                    easeFactor = 2.5f,
                    nextReviewTimestamp = System.currentTimeMillis()
                )
            }
            db.srsDao().insertAll(srsItems)
        }
        if (result.grammarGuides.isNotEmpty()) {
            db.grammarDao().insertGrammarGuides(result.grammarGuides)
        }
        if (result.sentences.isNotEmpty()) {
            db.sentenceDao().insertSentences(result.sentences)
        }
    }

    suspend fun clearAndResetToDefaults() = withContext(Dispatchers.IO) {
        db.lessonDao().clearAll()
        ensureInitialDataLoaded()
    }
}
