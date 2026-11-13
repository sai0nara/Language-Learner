package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.data.model.LessonAnswerEntity

@Dao
interface LessonAnswerDao {
    @Insert
    suspend fun insertAnswer(answer: LessonAnswerEntity)

    @Query("SELECT * FROM lesson_answers WHERE lessonId = :lessonId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentAnswers(lessonId: String, limit: Int = 100): List<LessonAnswerEntity>

    @Query("SELECT COUNT(*) FROM lesson_answers WHERE lessonId = :lessonId")
    suspend fun getTotalAnswersCount(lessonId: String): Int

    @Query("DELETE FROM lesson_answers WHERE lessonId = :lessonId AND id NOT IN (SELECT id FROM lesson_answers WHERE lessonId = :lessonId ORDER BY timestamp DESC LIMIT 100)")
    suspend fun pruneOldAnswers(lessonId: String)

    @Query("DELETE FROM lesson_answers WHERE lessonId = :lessonId")
    suspend fun clearAnswersForLesson(lessonId: String)
}
