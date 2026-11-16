package com.example.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.importer.ImportResult
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity
import com.example.data.repository.CurriculumRepository
import com.example.srs.ReviewGrade
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RoomPersistenceAndRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: CurriculumRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CurriculumRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `lessonDao inserts, orders, unlocks, and cascades deletes`() = runTest {
        val lesson2 = LessonEntity("l2", orderIndex = 2, title = "Lesson 2", subtitle = "", grammarFocus = "Cases", isUnlocked = false)
        val lesson1 = LessonEntity("l1", orderIndex = 1, title = "Lesson 1", subtitle = "", grammarFocus = "Verbs", isUnlocked = true)
        db.lessonDao().insertLessons(listOf(lesson2, lesson1))

        val all = db.lessonDao().getAllLessons().first()
        assertEquals(listOf("l1", "l2"), all.map { it.id })

        db.vocabDao().insertVocabItems(
            listOf(VocabItemEntity("v1", "l1", "радити", "raditi", "работать", "Verb"))
        )
        db.sentenceDao().insertSentences(
            listOf(SentenceEntity("s1", "l1", "Я работаю", "Ја радим", "Ja radim", "[]", "[]"))
        )
        db.grammarDao().insertGrammarGuide(
            GrammarGuideEntity("g1", "l1", "Present", "Summary", "Markdown", "{}")
        )

        db.lessonDao().unlockLessonByOrder(2)
        assertTrue(db.lessonDao().getLessonById("l2")!!.isUnlocked)

        db.lessonDao().deleteLesson("l1")
        assertNull(db.lessonDao().getLessonById("l1"))
        assertTrue(db.vocabDao().getVocabForLessonSync("l1").isEmpty())
        assertTrue(db.sentenceDao().getSentencesForLessonSync("l1").isEmpty())
        assertNull(db.grammarDao().getGrammarGuideForLessonSync("l1"))
    }

    @Test
    fun `srsDao filters due items and updates records`() = runTest {
        val now = 1_000_000L
        val dueItem = SrsReviewEntity("s1", "VOCAB", "l1", "a", "b", nextReviewTimestamp = now - 1000L)
        val futureItem = SrsReviewEntity("s2", "VOCAB", "l1", "c", "d", nextReviewTimestamp = now + 10_000L)
        db.srsDao().insertAll(listOf(dueItem, futureItem))

        val dueList = db.srsDao().getDueItems(now).first()
        assertEquals(1, dueList.size)
        assertEquals("s1", dueList[0].id)
        assertEquals(1, db.srsDao().getDueCount(now).first())

        db.srsDao().delete("s1")
        assertNull(db.srsDao().getSrsItemById("s1"))
        assertEquals(0, db.srsDao().getDueCount(now).first())
    }

    @Test
    fun `lessonAnswerDao prunes history to latest 100 answers`() = runTest {
        db.lessonDao().insertLesson(LessonEntity("l1", 1, "Lesson 1", "", "Focus", isUnlocked = true))
        repeat(105) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = "l1", isCorrect = idx >= 5, timestamp = 1000L + idx)
            )
        }
        db.lessonAnswerDao().pruneOldAnswers("l1")

        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount("l1"))
        val recent = db.lessonAnswerDao().getRecentAnswers("l1", limit = 100)
        assertTrue(recent.all { it.isCorrect })
    }

    @Test
    fun `repository ensureInitialDataLoaded seeds curriculum idempotently`() = runTest {
        repository.ensureInitialDataLoaded()
        val firstLoadLessons = repository.allLessons.first()
        assertTrue(firstLoadLessons.isNotEmpty())
        assertTrue(db.srsDao().getAllSrsItems().first().isNotEmpty())

        // Calling again should not duplicate data
        repository.ensureInitialDataLoaded()
        assertEquals(firstLoadLessons.size, repository.allLessons.first().size)
    }

    @Test
    fun `repository recordAnswer unlocks next lesson when mastery score reaches 4_5`() = runTest {
        db.lessonDao().insertLessons(
            listOf(
                LessonEntity("l1", 1, "L1", "", "F1", isUnlocked = true),
                LessonEntity("l2", 2, "L2", "", "F2", isUnlocked = false)
            )
        )

        repeat(89) {
            repository.recordAnswer("l1", isCorrect = true)
        }
        assertFalse(db.lessonDao().getLessonById("l2")!!.isUnlocked)

        val score = repository.recordAnswer("l1", isCorrect = true)
        assertEquals(4.5f, score, 0.001f)
        assertTrue(db.lessonDao().getLessonById("l1")!!.isCompleted)
        assertTrue(db.lessonDao().getLessonById("l2")!!.isUnlocked)
    }

    @Test
    fun `repository recordSrsResult and enrollInSrs persist SM-2 updates`() = runTest {
        val vocab = VocabItemEntity("v1", "l1", "хтети", "hteti", "хотеть", "Verb")
        repository.enrollInSrs(vocab)
        val enrolled = db.srsDao().getSrsItemById("v1")
        assertNotNull(enrolled)
        assertEquals(0, enrolled!!.repetition)

        // Re-enrolling existing item should not overwrite it
        repository.recordSrsResult(enrolled, ReviewGrade.GOOD)
        repository.enrollInSrs(vocab)

        val updated = db.srsDao().getSrsItemById("v1")!!
        assertEquals(1, updated.repetition)
        assertEquals(4, updated.lastQuality)
    }

    @Test
    fun `repository importCurriculum unlocks first lesson and creates SRS cards`() = runTest {
        val result = ImportResult(
            lessons = listOf(
                LessonEntity("imp1", 1, "Imported 1", "", "G1", isUnlocked = false),
                LessonEntity("imp2", 2, "Imported 2", "", "G2", isUnlocked = false)
            ),
            vocabItems = listOf(
                VocabItemEntity("iv1", "imp1", "град", "grad", "город", "Noun")
            )
        )

        repository.importCurriculum(result)

        assertTrue(db.lessonDao().getLessonById("imp1")!!.isUnlocked)
        assertFalse(db.lessonDao().getLessonById("imp2")!!.isUnlocked)
        assertEquals(1, db.vocabDao().getVocabForLessonSync("imp1").size)
        assertNotNull(db.srsDao().getSrsItemById("iv1"))
    }

    @Test
    fun `evolved entities persist FSRS v5 fields, matrixCoordinate, and matrixStatsJson`() = runTest {
        val matrixStat = com.example.data.model.MatrixCellStat(
            attempts = 4,
            correct = 4,
            accuracy = 1.0f,
            avgLatencyMs = 4200L,
            cellStatus = com.example.data.model.MatrixCellStatus.MASTERED
        )
        val lesson = LessonEntity(
            id = "l_fsrs",
            orderIndex = 1,
            title = "Lesson 1",
            subtitle = "Core Matrix",
            grammarFocus = "Regular Verbs",
            isUnlocked = true,
            status = "IN_PROGRESS",
            vocabMastery = 0.85f,
            practiceScore = 0.75f,
            unresolvedLapses = 0,
            medianAssemblyMs = 5500L,
            matrixStatsJson = """{"PAST_AFFIRMATIVE":{"attempts":4,"correct":4,"accuracy":1.0,"avgLatencyMs":4200,"cellStatus":"MASTERED"}}"""
        )
        val sentence = SentenceEntity(
            id = "s_coord",
            lessonId = "l_fsrs",
            sourceSentence = "Я работал",
            targetSentence = "Ја сам радио",
            targetSentenceLatin = "Ja sam radio",
            tokensJson = """["ја","сам","радио"]""",
            distractorTokensJson = "[]",
            matrixTense = "PAST",
            matrixForm = "AFFIRMATIVE",
            personGroup = "1SG"
        )
        val srs = SrsReviewEntity(
            id = "srs_fsrs",
            itemType = "VOCAB",
            lessonId = "l_fsrs",
            prompt = "радити",
            answer = "работать",
            state = "REVIEW",
            stability = 4.5f,
            difficulty = 3.2f,
            elapsedDays = 2,
            scheduledDays = 5,
            reps = 3,
            lapses = 1,
            lastReviewedAt = 123456789L
        )
        db.lessonDao().insertLesson(lesson)
        db.sentenceDao().insertSentences(listOf(sentence))
        db.srsDao().insertOrUpdate(srs)

        val loadedLesson = db.lessonDao().getLessonById("l_fsrs")!!
        val loadedSentence = db.sentenceDao().getSentencesForLessonSync("l_fsrs").single()
        val loadedSrs = db.srsDao().getSrsItemById("srs_fsrs")!!

        assertEquals(com.example.data.model.MatrixCellStatus.MASTERED, matrixStat.cellStatus)
        assertEquals("IN_PROGRESS", loadedLesson.status)
        assertEquals(0.85f, loadedLesson.vocabMastery, 0.001f)
        assertEquals(0.75f, loadedLesson.practiceScore, 0.001f)
        assertEquals(5500L, loadedLesson.medianAssemblyMs)
        assertTrue(loadedLesson.matrixStatsJson.contains("PAST_AFFIRMATIVE"))
        assertEquals("PAST_AFFIRMATIVE", loadedSentence.matrixCoordinateKey())
        assertEquals("1SG", loadedSentence.personGroup)
        assertEquals("REVIEW", loadedSrs.state)
        assertEquals(4.5f, loadedSrs.stability, 0.001f)
        assertEquals(3.2f, loadedSrs.difficulty, 0.001f)
        assertEquals(1, loadedSrs.lapses)
        assertEquals(123456789L, loadedSrs.lastReviewedAt)
    }

    @Test
    fun `initialCurriculumData seeds all 16 progressive lessons with matrixCoordinate sentences`() {
        val lessons = com.example.data.initial.InitialCurriculumData.getInitialLessons()
        val sentences = com.example.data.initial.InitialCurriculumData.getInitialSentences()
        val vocab = com.example.data.initial.InitialCurriculumData.getInitialVocab()
        val guides = com.example.data.initial.InitialCurriculumData.getInitialGrammarGuides()

        assertEquals(16, lessons.size)
        assertEquals((1..16).toList(), lessons.map { it.orderIndex })
        assertTrue(lessons.first().isUnlocked)
        assertTrue(lessons.drop(1).none { it.isUnlocked })
        assertTrue(lessons.all { lesson ->
            vocab.any { it.lessonId == lesson.id } &&
                sentences.any { it.lessonId == lesson.id } &&
                guides.any { it.lessonId == lesson.id }
        })
        assertTrue(sentences.all {
            it.matrixTense in setOf("PAST", "PRESENT", "FUTURE") &&
                it.matrixForm in setOf("AFFIRMATIVE", "NEGATIVE", "INTERROGATIVE")
        })
    }
}
