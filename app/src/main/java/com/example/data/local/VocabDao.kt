package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.VocabItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab_items WHERE lessonId = :lessonId ORDER BY targetWord ASC")
    fun getVocabForLesson(lessonId: String): Flow<List<VocabItemEntity>>

    @Query("SELECT * FROM vocab_items WHERE lessonId = :lessonId")
    suspend fun getVocabForLessonSync(lessonId: String): List<VocabItemEntity>

    @Query("SELECT * FROM vocab_items ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomVocab(limit: Int): List<VocabItemEntity>

    @Query("SELECT * FROM vocab_items")
    fun getAllVocab(): Flow<List<VocabItemEntity>>

    @Query("SELECT * FROM vocab_items WHERE id = :id LIMIT 1")
    suspend fun getVocabById(id: String): VocabItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabItems(items: List<VocabItemEntity>)

    @Query("DELETE FROM vocab_items WHERE lessonId = :lessonId")
    suspend fun deleteVocabForLesson(lessonId: String)
}
