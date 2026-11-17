package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

enum class LessonStatus {
    LOCKED,
    AVAILABLE,
    IN_PROGRESS,
    MASTERED
}

enum class MatrixCellStatus {
    UNTESTED,
    WEAK,
    PROFICIENT,
    MASTERED
}

@JsonClass(generateAdapter = true)
data class MatrixCellStat(
    val attempts: Int = 0,
    val correct: Int = 0,
    val accuracy: Float = 0f,
    val avgLatencyMs: Long = 0L,
    val cellStatus: MatrixCellStatus = MatrixCellStatus.UNTESTED
)

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: String,
    val orderIndex: Int,
    val title: String,
    val subtitle: String,
    val grammarFocus: String,
    val level: String = "B1",
    val isCompleted: Boolean = false,
    val masteryScore: Float = 0f, // 0.0 - 5.0 (calculated as (correctAnswers / 100) * 5)
    val isUnlocked: Boolean = false, // Lesson 1 unlocked initially, subsequent require grade >= 4.5 or >= 70%
    val correctAnswersInLast100: Int = 0,
    val totalAnswersInLast100: Int = 0,
    val vocabCount: Int = 0,
    val sentenceCount: Int = 0,
    val status: String = if (isCompleted) "MASTERED" else if (isUnlocked) "AVAILABLE" else "LOCKED",
    val vocabMastery: Float = 0f,
    val practiceScore: Float = 0f,
    val unresolvedLapses: Int = 0,
    val medianAssemblyMs: Long = 0L,
    val matrixStatsJson: String = "{}"
)

@Entity(
    tableName = "lesson_answers",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId"), Index("timestamp")]
)
data class LessonAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val lessonId: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "vocab_items",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class VocabItemEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val targetWord: String,       // Serbian (e.g., "разговарати")
    val targetWordLatin: String,  // Serbian Latin script (e.g., "razgovarati")
    val sourceTranslation: String,// Russian (e.g., "разговаривать")
    val partOfSpeech: String,     // Verb, Noun, Adj, etc.
    val phonetic: String = "",
    val exampleSentence: String = "",
    val distractorsJson: String = "[]", // List of false choices for 2x2 drills
    val audioUri: String = ""     // Local file path or custom URI
)

@Entity(
    tableName = "grammar_guides",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class GrammarGuideEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val title: String,
    val summary: String,
    val explanationMarkdown: String,
    val matrixJson: String // Serialized 3x3 conjugation matrix
)

@Entity(
    tableName = "sentences",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class SentenceEntity(
    @PrimaryKey val id: String,
    val lessonId: String,
    val sourceSentence: String,       // Prompt in Russian: "Ты пойдёшь завтра в кино?"
    val targetSentence: String,       // Serbian: "Хоћеш ли ићи сутра у биоскоп?"
    val targetSentenceLatin: String,  // "Hoćeš li ići sutra u bioskop?"
    val tokensJson: String,           // Correct tokens (lowercase): ["хоћеш", "ли", "ићи", "сутра", "у", "биоскоп"]
    val distractorTokensJson: String, // Distractor tokens (lowercase): ["јеси", "отишао", "сутрадан"]
    val grammarHint: String = "",
    val audioUri: String = "",
    val matrixTense: String = "PRESENT",      // PAST, PRESENT, FUTURE
    val matrixForm: String = "AFFIRMATIVE",   // AFFIRMATIVE, NEGATIVE, INTERROGATIVE
    val personGroup: String = "1SG"           // 1SG, 2SG_PL, 3SG
) {
    fun matrixCoordinateKey(): String = "${matrixTense}_${matrixForm}"
}

@Entity(tableName = "srs_reviews")
data class SrsReviewEntity(
    @PrimaryKey val id: String,       // Matches vocab or sentence id
    val itemType: String,             // "VOCAB" or "SENTENCE"
    val lessonId: String,
    val prompt: String,
    val answer: String,
    val secondaryText: String = "",
    val repetition: Int = 0,
    val intervalDays: Int = 1,
    val easeFactor: Float = 2.5f,     // Legacy SM-2 ease factor (kept for compatibility)
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val lastQuality: Int = 0,         // 0 to 5
    val state: String = "NEW",        // NEW, LEARNING, REVIEW, RELEARNING
    val stability: Float = 0f,        // FSRS stability in days
    val difficulty: Float = 5.0f,     // FSRS difficulty (1.0 - 10.0)
    val elapsedDays: Int = 0,
    val scheduledDays: Int = 0,
    val reps: Int = 0,
    val lapses: Int = 0,
    val lastReviewedAt: Long? = null
)
