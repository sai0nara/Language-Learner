package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity

@Database(
    entities = [
        LessonEntity::class,
        LessonAnswerEntity::class,
        VocabItemEntity::class,
        GrammarGuideEntity::class,
        SentenceEntity::class,
        SrsReviewEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lessonDao(): LessonDao
    abstract fun lessonAnswerDao(): LessonAnswerDao
    abstract fun vocabDao(): VocabDao
    abstract fun grammarDao(): GrammarDao
    abstract fun sentenceDao(): SentenceDao
    abstract fun srsDao(): SrsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "language_learner.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
