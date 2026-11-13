package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.GrammarGuideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GrammarDao {
    @Query("SELECT * FROM grammar_guides WHERE lessonId = :lessonId LIMIT 1")
    fun getGrammarGuideForLesson(lessonId: String): Flow<GrammarGuideEntity?>

    @Query("SELECT * FROM grammar_guides WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getGrammarGuideForLessonSync(lessonId: String): GrammarGuideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrammarGuide(guide: GrammarGuideEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrammarGuides(guides: List<GrammarGuideEntity>)

    @Query("DELETE FROM grammar_guides WHERE lessonId = :lessonId")
    suspend fun deleteGrammarGuideForLesson(lessonId: String)
}
