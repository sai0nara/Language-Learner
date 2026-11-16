package com.example.qa

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.MatrixCellStat
import com.example.data.model.MatrixCellStatus
import com.example.data.model.SentenceEntity
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity
import com.example.data.repository.CurriculumRepository
import com.example.srs.ReviewGrade
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.screens.SentencePracticeScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Automated tests for Polyglot FSRS v5, First-Attempt Scoring Guard,
 * Two-Tier Progression Gates, 3x3 Matrix Heatmap, and Weak-Cell Remediation.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w600dp-h1200dp")
class PolyglotFsrsAndMatrixMasteryTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: CurriculumRepository

    private val moshi = Moshi.Builder().build()
    private val mapType = Types.newParameterizedType(Map::class.java, String::class.java, MatrixCellStat::class.java)
    private val statsAdapter = moshi.adapter<Map<String, MatrixCellStat>>(mapType)

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(app as Context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CurriculumRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `first-attempt scoring guard prevents same-session retest from inflating FSRS stability`() = runBlocking {
        val lesson = LessonEntity("l1", 1, "L1", "Sub", "Focus", isUnlocked = true)
        db.lessonDao().insertLesson(lesson)

        // First attempt: AGAIN -> RELEARNING, stability = 0.5f, lapses = 1 (or 0 if NEW, resets stability)
        repository.recordImplicitSrsAttempt(
            itemId = "v1",
            itemType = "VOCAB",
            lessonId = "l1",
            prompt = "радити",
            answer = "работать",
            rating = ReviewGrade.AGAIN,
            isFirstAttemptInSession = true,
            nowMs = 1_000_000L
        )
        val afterFirst = db.srsDao().getSrsItemById("v1")!!
        assertEquals("RELEARNING", afterFirst.state)
        assertEquals(0.5f, afterFirst.stability, 0.001f)

        // Same-session retest: EASY -> ignored by First-Attempt Scoring Guard
        repository.recordImplicitSrsAttempt(
            itemId = "v1",
            itemType = "VOCAB",
            lessonId = "l1",
            prompt = "радити",
            answer = "работать",
            rating = ReviewGrade.EASY,
            isFirstAttemptInSession = false,
            nowMs = 1_005_000L
        )
        val afterRetest = db.srsDao().getSrsItemById("v1")!!
        assertEquals("RELEARNING", afterRetest.state)
        assertEquals(0.5f, afterRetest.stability, 0.001f)
        assertEquals(afterFirst.nextReviewTimestamp, afterRetest.nextReviewTimestamp)
    }

    @Test
    fun `two-tier gates unlock next lesson at 70 percent and require all 4 conditions for MASTERED`() = runBlocking {
        val l1 = LessonEntity("l1", 1, "L1", "Sub", "Focus", isUnlocked = true)
        val l2 = LessonEntity("l2", 2, "L2", "Sub", "Focus", isUnlocked = false)
        db.lessonDao().insertLessons(listOf(l1, l2))
        db.vocabDao().insertVocabItems(listOf(VocabItemEntity("v1", "l1", "радити", "raditi", "работать", "Verb")))
        db.srsDao().insertOrUpdate(
            SrsReviewEntity("v1", "VOCAB", "l1", "радити", "работать", state = "REVIEW", stability = 4.0f)
        )
        // 7 correct, 3 wrong -> 70% practiceScore -> unlocks L2, but matrix incomplete so NOT MASTERED
        repeat(7) { db.lessonAnswerDao().insertAnswer(LessonAnswerEntity(lessonId = "l1", isCorrect = true)) }
        repeat(3) { db.lessonAnswerDao().insertAnswer(LessonAnswerEntity(lessonId = "l1", isCorrect = false)) }

        repository.evaluateTwoTierGates("l1")
        assertTrue(db.lessonDao().getLessonById("l2")!!.isUnlocked)
        assertFalse(db.lessonDao().getLessonById("l1")!!.isCompleted)
        assertEquals("IN_PROGRESS", db.lessonDao().getLessonById("l1")!!.status)

        // Complete all 9 matrix cells with >= 3 attempts, >= 80% accuracy, <= 12000ms latency
        for (coord in CurriculumRepository.ALL_MATRIX_COORDS) {
            repeat(3) {
                repository.recordMatrixCellAttempt("l1", coord, isCorrect = true, latencyMs = 4_500L)
            }
        }
        val masteredL1 = db.lessonDao().getLessonById("l1")!!
        assertTrue(masteredL1.isCompleted)
        assertEquals("MASTERED", masteredL1.status)
    }

    @Test
    fun `lessonDetailScreen renders 3x3 heatmap and triggers Drill Weak Cells for weak coordinates`() {
        val weakMap = mapOf(
            "PAST_NEGATIVE" to MatrixCellStat(4, 1, 0.25f, 8_000L, MatrixCellStatus.WEAK),
            "PRESENT_AFFIRMATIVE" to MatrixCellStat(5, 5, 1.0f, 3_000L, MatrixCellStatus.MASTERED)
        )
        val lesson = LessonEntity(
            id = "l_heat",
            orderIndex = 1,
            title = "Heatmap Lesson",
            subtitle = "Sub",
            grammarFocus = "Matrix",
            isUnlocked = true,
            matrixStatsJson = statsAdapter.toJson(weakMap)
        )
        var drilledCoords: Set<String> = emptySet()

        composeRule.setContent {
            LanguageLearnerTheme {
                LessonDetailScreen(
                    lesson = lesson,
                    onBack = {},
                    onOpenSentencePractice = {},
                    onOpenVocabDrill = {},
                    onOpenGrammarMatrix = {},
                    onDrillWeakCells = { drilledCoords = it }
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("matrix_heatmap_section").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("heatmap_cell_PAST_NEGATIVE").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("drill_weak_cells_button").performScrollTo().assertIsDisplayed().performClick()

        assertEquals(setOf("PAST_NEGATIVE"), drilledCoords)
    }

    @Test
    fun `sentencePracticeScreen filters active sentences by weakFilterCoords`() {
        val singletonDb = AppDatabase.getInstance(app as Context)
        runBlocking(Dispatchers.IO) {
            singletonDb.clearAllTables()
            CurriculumRepository(singletonDb).ensureInitialDataLoaded()
        }
        val lesson = LessonEntity("l_weak", 200, "Weak Drill", "Sub", "Focus", isUnlocked = true)
        val sWeak = SentenceEntity(
            id = "s_weak_1",
            lessonId = "l_weak",
            sourceSentence = "Я не работал",
            targetSentence = "Ја нисам радио",
            targetSentenceLatin = "Ja nisam radio",
            tokensJson = """["ја","нисам","радио"]""",
            distractorTokensJson = "[]",
            matrixTense = "PAST",
            matrixForm = "NEGATIVE"
        )
        val sStrong = SentenceEntity(
            id = "s_strong_1",
            lessonId = "l_weak",
            sourceSentence = "Я работаю",
            targetSentence = "Ја радим",
            targetSentenceLatin = "Ja radim",
            tokensJson = """["ја","радим"]""",
            distractorTokensJson = "[]",
            matrixTense = "PRESENT",
            matrixForm = "AFFIRMATIVE"
        )
        runBlocking(Dispatchers.IO) {
            singletonDb.lessonDao().insertLesson(lesson)
            singletonDb.sentenceDao().insertSentences(listOf(sWeak, sStrong))
        }
        val vm = LanguageLearnerViewModel(app)
        vm.setWeakCellFilter(setOf("PAST_NEGATIVE"))
        vm.selectLesson(lesson)

        composeRule.setContent {
            LanguageLearnerTheme {
                SentencePracticeScreen(lesson = lesson, viewModel = vm, onBack = {})
            }
        }
        composeRule.waitUntil(5_000) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            vm.activeSentences.value.size == 2
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Я не работал").assertIsDisplayed()
        composeRule.onNodeWithText("Я работаю").assertDoesNotExist()

        runBlocking(Dispatchers.IO) { singletonDb.clearAllTables() }
    }
}
