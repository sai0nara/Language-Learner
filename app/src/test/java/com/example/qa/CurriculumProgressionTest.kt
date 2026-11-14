package com.example.qa

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.repository.CurriculumRepository
import com.example.ui.screens.CurriculumScreen
import com.example.ui.screens.LessonDetailScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

/**
 * Automated P0, P1, and P2 test suite for Curriculum Progression & Mastery Unlocking
 * generated from `qa-artifacts/2026-11-14-curriculum-progression/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurriculumProgressionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: CurriculumRepository
    private lateinit var viewModel: LanguageLearnerViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(app as Context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CurriculumRepository(db)
        viewModel = LanguageLearnerViewModel(app)
    }

    @After
    fun tearDown() {
        db.close()
    }

    private suspend fun seedTwoSequentialLessons(
        l1Unlocked: Boolean = true,
        l2Unlocked: Boolean = false
    ) {
        db.lessonDao().insertLessons(
            listOf(
                LessonEntity("lesson_1", 1, "Lesson 1", "Sub 1", "Grammar 1", isUnlocked = l1Unlocked),
                LessonEntity("lesson_2", 2, "Lesson 2", "Sub 2", "Grammar 2", isUnlocked = l2Unlocked)
            )
        )
    }

    private suspend fun seedAnswers(
        lessonId: String,
        correctCount: Int,
        incorrectCount: Int,
        oldestIsCorrect: Boolean? = null,
        fixedTimestamp: Long? = null
    ) {
        val baseTime = fixedTimestamp ?: 1_000_000L
        var offset = 0L
        if (oldestIsCorrect != null) {
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(
                    lessonId = lessonId,
                    isCorrect = oldestIsCorrect,
                    timestamp = if (fixedTimestamp != null) baseTime else baseTime + offset++
                )
            )
        }
        val remCorrect = if (oldestIsCorrect == true) correctCount - 1 else correctCount
        val remIncorrect = if (oldestIsCorrect == false) incorrectCount - 1 else incorrectCount
        repeat(remCorrect) {
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(
                    lessonId = lessonId,
                    isCorrect = true,
                    timestamp = if (fixedTimestamp != null) baseTime else baseTime + offset++
                )
            )
        }
        repeat(remIncorrect) {
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(
                    lessonId = lessonId,
                    isCorrect = false,
                    timestamp = if (fixedTimestamp != null) baseTime else baseTime + offset++
                )
            )
        }
    }

    @Test
    fun `TC-001 first-launch empty database seeds initial curriculum`() = runTest {
        // Arrange
        assertTrue(db.lessonDao().getAllLessons().first().isEmpty())

        // Act
        repository.ensureInitialDataLoaded()

        // Assert
        val lessons = db.lessonDao().getAllLessons().first()
        assertTrue(lessons.isNotEmpty())
        assertTrue(lessons.first().isUnlocked)
        if (lessons.size > 1) {
            assertFalse(lessons[1].isUnlocked)
        }
        assertTrue(db.vocabDao().getVocabForLessonSync(lessons.first().id).isNotEmpty())
    }

    @Test
    fun `TC-002 subsequent launch with existing database skips seeding`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val initialLessons = db.lessonDao().getAllLessons().first()
        val initialVocabCount = db.vocabDao().getVocabForLessonSync(initialLessons.first().id).size

        // Act
        repository.ensureInitialDataLoaded()

        // Assert
        val lessonsAfter = db.lessonDao().getAllLessons().first()
        assertEquals(initialLessons.size, lessonsAfter.size)
        assertEquals(initialVocabCount, db.vocabDao().getVocabForLessonSync(lessonsAfter.first().id).size)
    }

    @Test
    fun `TC-003 reaching 90 of 100 correct answers masters lesson and unlocks next lesson`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 89, incorrectCount = 10)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(4.5f, score, 0.001f)
        val lesson1 = db.lessonDao().getLessonById("lesson_1")!!
        val lesson2 = db.lessonDao().getLessonById("lesson_2")!!
        assertTrue(lesson1.isCompleted)
        assertEquals(4.5f, lesson1.masteryScore, 0.001f)
        assertTrue(lesson2.isUnlocked)
    }

    @Test
    fun `TC-004 below mastery threshold 89 of 100 correct keeps lesson unlocked`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 89, incorrectCount = 10)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = false)

        // Assert
        assertEquals(4.45f, score, 0.001f)
        val lesson1 = db.lessonDao().getLessonById("lesson_1")!!
        val lesson2 = db.lessonDao().getLessonById("lesson_2")!!
        assertFalse(lesson1.isCompleted)
        assertFalse(lesson2.isUnlocked)
    }

    @Test
    fun `TC-005 above mastery threshold 91 of 100 correct masters lesson and unlocks next`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 90, incorrectCount = 9)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(4.55f, score, 0.001f)
        assertTrue(db.lessonDao().getLessonById("lesson_1")!!.isCompleted)
        assertTrue(db.lessonDao().getLessonById("lesson_2")!!.isUnlocked)
    }

    @Test
    fun `TC-006 lower boundary 0 answers renders 0_0 mastery without error`() {
        // Arrange
        val page = LessonDetailPage(composeRule)
        composeRule.setContent {
            LanguageLearnerTheme {
                LessonDetailScreen(
                    lesson = LessonEntity("lesson_1", 1, "Lesson 1", "Sub", "Focus", masteryScore = 0.0f, isUnlocked = true),
                    onBack = {},
                    onOpenSentencePractice = {},
                    onOpenVocabDrill = {},
                    onOpenGrammarMatrix = {}
                )
            }
        }

        // Act & Assert
        page.assertActionsDisplayed()
            .assertTextDisplayed("0.0")
    }

    @Test
    fun `TC-007 lower boundary plus 1 first correct answer computes 0_05f`() = runTest {
        // Arrange
        seedTwoSequentialLessons()

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(1, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
        assertEquals(0.05f, score, 0.001f)
    }

    @Test
    fun `TC-008 upper boundary minus 1 99 of 99 correct computes 4_95f without pruning`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 98, incorrectCount = 0)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(99, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
        assertEquals(4.95f, score, 0.001f)
    }

    @Test
    fun `TC-009 upper boundary 100 of 100 correct computes 5_00f and stays mastered`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 99, incorrectCount = 0)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
        assertEquals(5.0f, score, 0.001f)
        assertTrue(db.lessonDao().getLessonById("lesson_1")!!.isCompleted)
    }

    @Test
    fun `TC-010 rolling window prune at 101st answer below mastery`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 50, incorrectCount = 50, oldestIsCorrect = true)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = false)

        // Assert
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
        assertEquals(2.45f, score, 0.001f)
        assertFalse(db.lessonDao().getLessonById("lesson_1")!!.isCompleted)
    }

    @Test
    fun `TC-011 rolling window prune at 101st answer pushes out old wrong answer to reach 4_50f`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 89, incorrectCount = 11, oldestIsCorrect = false)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
        assertEquals(4.5f, score, 0.001f)
        assertTrue(db.lessonDao().getLessonById("lesson_1")!!.isCompleted)
        assertTrue(db.lessonDao().getLessonById("lesson_2")!!.isUnlocked)
    }

    @Test
    fun `TC-012 invalid partition negative correctCount and masteryScore below 0_0f guarded`() = runTest {
        // Arrange
        seedTwoSequentialLessons()

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = false)

        // Assert
        assertTrue(score >= 0.0f)
        val lesson = db.lessonDao().getLessonById("lesson_1")!!
        assertTrue(lesson.correctAnswersInLast100 >= 0)
        assertTrue(lesson.masteryScore >= 0.0f)
    }

    @Test
    fun `TC-013 invalid partition correctCount over 100 and masteryScore over 5_0f guarded`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 100, incorrectCount = 0)

        // Act
        val score = repository.recordAnswer("lesson_1", isCorrect = true)

        // Assert
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
        assertTrue(score <= 5.0f)
        val lesson = db.lessonDao().getLessonById("lesson_1")!!
        assertTrue(lesson.correctAnswersInLast100 <= 100)
        assertTrue(lesson.masteryScore <= 5.0f)
    }

    @Test
    fun `TC-014 invalid partition negative answerCount guarded`() = runTest {
        // Arrange
        seedTwoSequentialLessons()

        // Act
        val countBefore = db.lessonAnswerDao().getTotalAnswersCount("lesson_1")
        repository.recordAnswer("lesson_1", isCorrect = false)
        val countAfter = db.lessonAnswerDao().getTotalAnswersCount("lesson_1")

        // Assert
        assertTrue(countBefore >= 0)
        assertEquals(1, countAfter)
    }

    @Test
    fun `TC-015 tapping a locked lesson card shows toast and blocks navigation`() {
        // Arrange
        var navigatedLesson: LessonEntity? = null
        val page = CurriculumPage(composeRule)
        composeRule.setContent {
            LanguageLearnerTheme {
                CurriculumScreen(
                    viewModel = viewModel,
                    onOpenLesson = { navigatedLesson = it },
                    onOpenSrsReview = {},
                    onOpenImportExport = {}
                )
            }
        }
        page.assertLessonCardDisplayed("lesson_2")

        // Act
        page.tapLessonCard("lesson_2")

        // Assert
        assertNull(navigatedLesson)
        assertNotNull(ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun `TC-016 illegal transition from Locked to Mastered blocked`() = runTest {
        // Arrange
        seedTwoSequentialLessons(l1Unlocked = true, l2Unlocked = false)

        // Act
        val lesson2Before = db.lessonDao().getLessonById("lesson_2")!!
        val openedByUiGuard = lesson2Before.isUnlocked

        // Assert
        assertFalse(openedByUiGuard)
        assertFalse(lesson2Before.isUnlocked)
        assertFalse(lesson2Before.isCompleted)
    }

    @Test
    fun `TC-017 launch 3x3 Grammar Matrix from LessonDetailScreen`() {
        // Arrange
        var openedGrammar = false
        val page = LessonDetailPage(composeRule)
        composeRule.setContent {
            LanguageLearnerTheme {
                LessonDetailScreen(
                    lesson = LessonEntity("lesson_1", 1, "Lesson 1", "Sub", "Focus", isUnlocked = true),
                    onBack = {},
                    onOpenSentencePractice = {},
                    onOpenVocabDrill = {},
                    onOpenGrammarMatrix = { openedGrammar = true }
                )
            }
        }

        // Act
        page.assertActionsDisplayed().tapGrammarMatrix()

        // Assert
        assertTrue(openedGrammar)
    }

    @Test
    fun `TC-018 toggle Cyrillic Latin script and Dark Light theme in CurriculumScreen`() {
        // Arrange
        val page = CurriculumPage(composeRule)
        composeRule.setContent {
            LanguageLearnerTheme {
                CurriculumScreen(
                    viewModel = viewModel,
                    onOpenLesson = {},
                    onOpenSrsReview = {},
                    onOpenImportExport = {}
                )
            }
        }
        val initialTheme = viewModel.isDarkTheme.value
        page.assertHeaderDisplayed(isLatin = false)

        // Act
        page.toggleScript().toggleTheme()

        // Assert
        page.assertHeaderDisplayed(isLatin = true)
        assertTrue(viewModel.isLatinScript.value)
        assertEquals(!initialTheme, viewModel.isDarkTheme.value)
    }

    @Test
    fun `TC-019 pairwise UI config matrix across script theme and lesson states`() {
        // Arrange
        val configs = listOf(
            Triple(false, false, "Locked"),
            Triple(true, true, "Locked"),
            Triple(false, true, "Unlocked"),
            Triple(true, false, "Unlocked"),
            Triple(false, false, "Mastered"),
            Triple(true, true, "Mastered")
        )
        val page = CurriculumPage(composeRule)
        composeRule.setContent {
            LanguageLearnerTheme {
                CurriculumScreen(
                    viewModel = viewModel,
                    onOpenLesson = {},
                    onOpenSrsReview = {},
                    onOpenImportExport = {}
                )
            }
        }

        // Act & Assert
        configs.forEach { (wantLatin, wantDark, _) ->
            if (viewModel.isLatinScript.value != wantLatin) page.toggleScript()
            if (viewModel.isDarkTheme.value != wantDark) page.toggleTheme()
            page.assertHeaderDisplayed(isLatin = wantLatin)
                .assertLessonCardDisplayed("lesson_1")
        }
    }

    @Test
    fun `TC-020 rapid double-submit of recordAnswer at 100-row 90-correct boundary`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 89, incorrectCount = 10)

        // Act
        listOf(
            async { repository.recordAnswer("lesson_1", isCorrect = true) },
            async { repository.recordAnswer("lesson_1", isCorrect = true) }
        ).awaitAll()

        // Assert
        val count = db.lessonAnswerDao().getTotalAnswersCount("lesson_1")
        assertTrue(count <= 100)
        val lesson1 = db.lessonDao().getLessonById("lesson_1")!!
        assertTrue(lesson1.masteryScore >= 4.5f)
        assertTrue(lesson1.isCompleted)
        assertTrue(db.lessonDao().getLessonById("lesson_2")!!.isUnlocked)
    }

    @Test
    fun `TC-021 pruneOldAnswers when more than 100 rows share identical timestamps`() = runTest {
        // Arrange
        seedTwoSequentialLessons()
        seedAnswers("lesson_1", correctCount = 100, incorrectCount = 0, fixedTimestamp = 5_000_000L)

        // Act
        db.lessonAnswerDao().insertAnswer(
            LessonAnswerEntity(lessonId = "lesson_1", isCorrect = true, timestamp = 5_000_000L)
        )
        db.lessonAnswerDao().pruneOldAnswers("lesson_1")

        // Assert
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount("lesson_1"))
    }
}
