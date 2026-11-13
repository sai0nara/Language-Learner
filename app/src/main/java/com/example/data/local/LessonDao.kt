package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons ORDER BY orderIndex ASC")
    fun getAllLessons(): Flow<List<LessonEntity>>

    @Query("SELECT * FROM lessons WHERE id = :id LIMIT 1")
    suspend fun getLessonById(id: String): LessonEntity?

    @Query("SELECT * FROM lessons WHERE orderIndex = :orderIndex LIMIT 1")
    suspend fun getLessonByOrder(orderIndex: Int): LessonEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLesson(lesson: LessonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessons(lessons: List<LessonEntity>)

    @Update
    suspend fun updateLesson(lesson: LessonEntity)

    @Query("UPDATE lessons SET isCompleted = :completed, masteryScore = :score, correctAnswersInLast100 = :correct, totalAnswersInLast100 = :total WHERE id = :id")
    suspend fun updateGradeStats(id: String, completed: Boolean, score: Float, correct: Int, total: Int)

    @Query("UPDATE lessons SET isUnlocked = 1 WHERE orderIndex = :orderIndex")
    suspend fun unlockLessonByOrder(orderIndex: Int)

    @Query("DELETE FROM lessons WHERE id = :id")
    suspend fun deleteLesson(id: String)

    @Query("DELETE FROM lessons")
    suspend fun clearAll()
}
