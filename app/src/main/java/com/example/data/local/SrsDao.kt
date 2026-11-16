package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SrsReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SrsDao {
    @Query("SELECT * FROM srs_reviews WHERE nextReviewTimestamp <= :currentTimestamp ORDER BY nextReviewTimestamp ASC")
    fun getDueItems(currentTimestamp: Long): Flow<List<SrsReviewEntity>>

    @Query("SELECT COUNT(*) FROM srs_reviews WHERE nextReviewTimestamp <= :currentTimestamp")
    fun getDueCount(currentTimestamp: Long): Flow<Int>

    @Query("SELECT * FROM srs_reviews ORDER BY nextReviewTimestamp ASC")
    fun getAllSrsItems(): Flow<List<SrsReviewEntity>>

    @Query("SELECT * FROM srs_reviews WHERE id = :id LIMIT 1")
    suspend fun getSrsItemById(id: String): SrsReviewEntity?

    @Query("SELECT * FROM srs_reviews WHERE lessonId = :lessonId")
    suspend fun getSrsItemsByLessonSync(lessonId: String): List<SrsReviewEntity>

    @Query("SELECT * FROM srs_reviews")
    suspend fun getAllSrsItemsSync(): List<SrsReviewEntity>

    @Query("SELECT COUNT(*) FROM srs_reviews WHERE lastReviewedAt >= :dayStartMs")
    suspend fun countReviewedSince(dayStartMs: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(item: SrsReviewEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SrsReviewEntity>)

    @Update
    suspend fun update(item: SrsReviewEntity)

    @Query("DELETE FROM srs_reviews WHERE id = :id")
    suspend fun delete(id: String)
}
