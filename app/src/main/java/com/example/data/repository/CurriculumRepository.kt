package com.example.data.repository

import com.example.data.importer.ImportResult
import com.example.data.local.AppDatabase
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity
import com.example.data.initial.InitialCurriculumData
import com.example.srs.ReviewGrade
import com.example.srs.SpacedRepetitionAlgorithm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

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
