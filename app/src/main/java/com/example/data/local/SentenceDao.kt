package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SentenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SentenceDao {
    @Query("SELECT * FROM sentences WHERE lessonId = :lessonId ORDER BY id ASC")
    fun getSentencesForLesson(lessonId: String): Flow<List<SentenceEntity>>

    @Query("SELECT * FROM sentences WHERE lessonId = :lessonId")
    suspend fun getSentencesForLessonSync(lessonId: String): List<SentenceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentences(sentences: List<SentenceEntity>)

    @Query("DELETE FROM sentences WHERE lessonId = :lessonId")
    suspend fun deleteSentencesForLesson(lessonId: String)
}
