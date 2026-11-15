package com.example.qa

import android.app.Application
import android.content.Context
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.SentenceEntity
import com.example.data.repository.CurriculumRepository
import com.example.ui.screens.SentencePracticeScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowTextToSpeech

/**
 * Automated P0, P1, and P2 test suite for Sentence Construction Practice
 * generated from `qa-artifacts/2026-11-15-sentence-practice/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w600dp-h1200dp")
class SentencePracticeTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: CurriculumRepository
    private lateinit var viewModel: LanguageLearnerViewModel

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        db = AppDatabase.getInstance(app as Context)
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
        repository = CurriculumRepository(db)
        runBlocking(Dispatchers.IO) {
            repository.ensureInitialDataLoaded()
        }
        viewModel = LanguageLearnerViewModel(app)
        ShadowTextToSpeech.getLastTextToSpeechInstance()?.let { tts ->
            Shadows.shadowOf(tts).onInitListener?.onInit(TextToSpeech.SUCCESS)
        }
    }

    @After
    fun tearDown() {
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    private fun buildLesson(
        id: String = "qa_sp_lesson_1",
        orderIndex: Int = 100,
        masteryScore: Float = 0.0f,
        correctInLast100: Int = 0,
        totalInLast100: Int = 0,
        isCompleted: Boolean = false,
        isUnlocked: Boolean = true
    ) = LessonEntity(
        id = id,
        orderIndex = orderIndex,
        title = "QA Sentence Lesson $orderIndex",
        subtitle = "QA Subtitle",
        grammarFocus = "QA Grammar",
        isCompleted = isCompleted,
        masteryScore = masteryScore,
        isUnlocked = isUnlocked,
        correctAnswersInLast100 = correctInLast100,
        totalAnswersInLast100 = totalInLast100
    )

    private fun buildSentence(
        id: String = "qa_sp_sent_1",
        lessonId: String = "qa_sp_lesson_1",
        sourceSentence: String = "Я студент",
        targetSentence: String = "Ја сам студент",
        targetSentenceLatin: String = "Ja sam student",
        tokensJson: String = """["ја","сам","студент"]""",
        distractorTokensJson: String = """["ти","си"]""",
        grammarHint: String = "Краткая форма глагола бити стоит на втором месте.",
        audioUri: String = ""
    ) = SentenceEntity(
        id = id,
        lessonId = lessonId,
        sourceSentence = sourceSentence,
        targetSentence = targetSentence,
        targetSentenceLatin = targetSentenceLatin,
        tokensJson = tokensJson,
        distractorTokensJson = distractorTokensJson,
        grammarHint = grammarHint,
        audioUri = audioUri
    )

    private fun launchSentencePractice(
        lesson: LessonEntity,
        sentences: List<SentenceEntity>,
        isLatin: Boolean = false,
        insertLesson: Boolean = true,
        onBack: () -> Unit = {}
    ): SentencePracticePage {
        runBlocking(Dispatchers.IO) {
            if (insertLesson) {
                db.lessonDao().insertLesson(lesson)
            }
            if (sentences.isNotEmpty()) {
                db.sentenceDao().insertSentences(sentences)
            }
        }
        if (viewModel.isLatinScript.value != isLatin) {
            viewModel.toggleScript()
        }
        viewModel.selectLesson(lesson)
        composeRule.setContent {
            LanguageLearnerTheme {
                SentencePracticeScreen(
                    lesson = lesson,
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
        }
        composeRule.waitForIdle()
        if (sentences.isNotEmpty()) {
            composeRule.waitUntil(timeoutMillis = 5_000) {
                runCatching {
                    composeRule.onNodeWithTag("source_sentence_text").fetchSemanticsNode()
                    true
                }.getOrDefault(false)
            }
        }
        return SentencePracticePage(composeRule)
    }

    private fun seedAnswers(lessonId: String, correctCount: Int, incorrectCount: Int) {
        runBlocking(Dispatchers.IO) {
            val baseTime = 1_000_000L
            var offset = 0L
            val answers = mutableListOf<LessonAnswerEntity>()
            repeat(incorrectCount) {
                answers.add(
                    LessonAnswerEntity(
                        lessonId = lessonId,
                        isCorrect = false,
                        timestamp = baseTime + (++offset)
                    )
                )
            }
            repeat(correctCount) {
                answers.add(
                    LessonAnswerEntity(
                        lessonId = lessonId,
                        isCorrect = true,
                        timestamp = baseTime + (++offset)
                    )
                )
            }
            answers.forEach { db.lessonAnswerDao().insertAnswer(it) }
        }
    }

    // TC-001 (P2): cleanToken normalizes uppercase, whitespace, and Unicode punctuation/symbols
    @Test
    fun tc001_cleanToken_normalizesUppercaseWhitespaceAndPunctuation() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            tokensJson = """["  Привет! ", "«Мир»—", "Čaša?"]""",
            distractorTokensJson = "[]"
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence))

        // Assert
        page.assertPoolChipCount("привет", 1)
            .assertPoolChipCount("мир", 1)
            .assertPoolChipCount("čaša", 1)
    }

    // TC-002 (P2): cleanToken filters out empty, whitespace-only, and punctuation-only tokens
    @Test
    fun tc002_cleanToken_filtersOutEmptyWhitespaceAndPunctuationOnlyTokens() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            tokensJson = """["", "   ", ".,!?—", "да"]""",
            distractorTokensJson = "[]"
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence))

        // Assert
        page.assertPoolChipCount("да", 1)
            .assertNoBlankPoolChips()
    }

    // TC-003 (P1): masteryScore at 0.0f and 0.1f displays formatted score without unlock
    @Test
    fun tc003_masteryScore_zeroAndZeroPointOne_displaysFormattedScoreWithoutUnlock() {
        // Arrange
        val lesson1 = buildLesson(id = "qa_sp_l1", orderIndex = 100, masteryScore = 0.0f, correctInLast100 = 0)
        val lesson2 = buildLesson(id = "qa_sp_l2", orderIndex = 101, isUnlocked = false)
        runBlocking(Dispatchers.IO) { db.lessonDao().insertLesson(lesson2) }

        // Act - 0.0f
        val page = launchSentencePractice(lesson1, emptyList())

        // Assert - 0.0f
        page.assertGradeDisplayed("0.0")
            .assertSummaryDisplayed()
            .assertUnlockBannerAbsent()
        val dbL1Zero = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        val dbL2Zero = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l2")!! }
        assertFalse(dbL1Zero.isCompleted)
        assertFalse(dbL2Zero.isUnlocked)

        // Act - 0.1f (2 correct answers out of 100 = 0.1f)
        runBlocking(Dispatchers.IO) {
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            viewModel.activeLesson.value?.correctAnswersInLast100 == 2
        }
        composeRule.waitForIdle()

        // Assert - 0.1f
        page.assertGradeDisplayed("0.1")
            .assertUnlockBannerAbsent()
        val dbL1PointOne = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        val dbL2PointOne = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l2")!! }
        assertEquals(0.1f, dbL1PointOne.masteryScore, 0.001f)
        assertFalse(dbL1PointOne.isCompleted)
        assertFalse(dbL2PointOne.isUnlocked)
    }

    // TC-004 (P1): masteryScore below lower bound (-0.1f) is rejected/clamped
    @Test
    fun tc004_masteryScore_belowLowerBound_neverProducesNegativeAndStaysIncomplete() {
        // Arrange
        val lesson1 = buildLesson(id = "qa_sp_l1", orderIndex = 100)
        val lesson2 = buildLesson(id = "qa_sp_l2", orderIndex = 101, isUnlocked = false)
        runBlocking(Dispatchers.IO) {
            db.lessonDao().insertLessons(listOf(lesson1, lesson2))
        }

        // Act
        val score = runBlocking(Dispatchers.IO) {
            repeat(5) { repository.recordAnswer("qa_sp_l1", isCorrect = false) }
            repository.recordAnswer("qa_sp_l1", isCorrect = false)
        }

        // Assert
        assertTrue("Rolling score must never be negative, got $score", score >= 0.0f)
        assertEquals(0.0f, score, 0.001f)
        val updatedL1 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        val updatedL2 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l2")!! }
        assertFalse(updatedL1.isCompleted)
        assertFalse(updatedL2.isUnlocked)
    }

    // TC-005 (P1): masteryScore at 4.4f (just below 4.5f threshold) keeps lesson incomplete
    @Test
    fun tc005_masteryScore_fourPointFour_keepsLessonIncompleteAndNextLocked() {
        // Arrange
        val lesson1 = buildLesson(id = "qa_sp_l1", orderIndex = 100)
        val lesson2 = buildLesson(id = "qa_sp_l2", orderIndex = 101, isUnlocked = false)
        runBlocking(Dispatchers.IO) {
            db.lessonDao().insertLessons(listOf(lesson1, lesson2))
        }
        seedAnswers("qa_sp_l1", correctCount = 87, incorrectCount = 12)
        val score = runBlocking(Dispatchers.IO) {
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
        }
        assertEquals(4.4f, score, 0.001f)

        // Act
        val updatedL1 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        val page = launchSentencePractice(updatedL1, emptyList())

        // Assert
        page.assertGradeDisplayed("4.4")
            .assertSummaryDisplayed()
            .assertUnlockBannerAbsent()
        val updatedL2 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l2")!! }
        assertFalse(updatedL1.isCompleted)
        assertFalse(updatedL2.isUnlocked)
    }

    // TC-006 (P1): masteryScore at 4.5f and 4.6f marks lesson complete and unlocks next lesson
    @Test
    fun tc006_masteryScore_fourPointFiveAndFourPointSix_completesAndUnlocksNextLesson() {
        // Arrange
        val lesson1 = buildLesson(id = "qa_sp_l1", orderIndex = 100)
        val lesson2 = buildLesson(id = "qa_sp_l2", orderIndex = 101, isUnlocked = false)
        runBlocking(Dispatchers.IO) {
            db.lessonDao().insertLessons(listOf(lesson1, lesson2))
        }
        seedAnswers("qa_sp_l1", correctCount = 89, incorrectCount = 10)

        // Act - 4.5f (90/100)
        val score45 = runBlocking(Dispatchers.IO) {
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
        }
        val updatedL1At45 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        val page = launchSentencePractice(updatedL1At45, emptyList(), insertLesson = false)

        // Assert - 4.5f
        assertEquals(4.5f, score45, 0.001f)
        page.assertGradeDisplayed("4.5")
            .assertUnlockBannerDisplayed()
        assertTrue(updatedL1At45.isCompleted)
        assertTrue(runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l2")!!.isUnlocked })

        // Act - 4.6f (92/100)
        runBlocking(Dispatchers.IO) {
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            viewModel.activeLesson.value?.correctAnswersInLast100 == 92
        }
        composeRule.waitForIdle()

        // Assert - 4.6f
        page.assertGradeDisplayed("4.6")
            .assertUnlockBannerDisplayed()
        val updatedL1At46 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        assertEquals(4.6f, updatedL1At46.masteryScore, 0.001f)
        assertTrue(updatedL1At46.isCompleted)
    }

    // TC-007 (P1): masteryScore at 4.9f and 5.0f (valid upper bound)
    @Test
    fun tc007_masteryScore_fourPointNineAndFivePointZero_validUpperBound() {
        // Arrange
        val lesson1 = buildLesson(id = "qa_sp_l1", orderIndex = 100)
        runBlocking(Dispatchers.IO) { db.lessonDao().insertLesson(lesson1) }
        seedAnswers("qa_sp_l1", correctCount = 97, incorrectCount = 2)

        // Act - 4.9f (98/100)
        val score49 = runBlocking(Dispatchers.IO) {
            repository.recordAnswer("qa_sp_l1", isCorrect = true)
        }
        val updatedAt49 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        val page = launchSentencePractice(updatedAt49, emptyList(), insertLesson = false)

        // Assert - 4.9f
        assertEquals(4.9f, score49, 0.001f)
        page.assertGradeDisplayed("4.9")
            .assertUnlockBannerDisplayed()
        assertTrue(updatedAt49.isCompleted)

        // Act - 5.0f (100/100)
        runBlocking(Dispatchers.IO) {
            repeat(100) { repository.recordAnswer("qa_sp_l1", isCorrect = true) }
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            viewModel.activeLesson.value?.correctAnswersInLast100 == 100
        }
        composeRule.waitForIdle()

        // Assert - 5.0f
        page.assertGradeDisplayed("5.0")
            .assertUnlockBannerDisplayed()
        val updatedAt50 = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        assertEquals(5.0f, updatedAt50.masteryScore, 0.001f)
        assertTrue(updatedAt50.isCompleted)
    }

    // TC-008 (P1): masteryScore above upper bound (5.1f) is invalid
    @Test
    fun tc008_masteryScore_aboveUpperBound_capsAtFivePointZero() {
        // Arrange
        val lesson1 = buildLesson(id = "qa_sp_l1", orderIndex = 100)
        runBlocking(Dispatchers.IO) { db.lessonDao().insertLesson(lesson1) }

        // Act
        val score = runBlocking(Dispatchers.IO) {
            var latest = 0f
            repeat(105) {
                latest = repository.recordAnswer("qa_sp_l1", isCorrect = true)
            }
            latest
        }

        // Assert
        assertEquals(5.0f, score, 0.001f)
        assertTrue("Rolling score must cap at 5.0f, got $score", score <= 5.0f)
        val updated = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById("qa_sp_l1")!! }
        assertEquals(100, updated.totalAnswersInLast100)
        assertEquals(100, updated.correctAnswersInLast100)
    }

    // TC-009 (P2): Empty rawSentences list renders immediate summary/empty-state view
    @Test
    fun tc009_emptySentencesList_rendersImmediateSummaryState() {
        // Arrange
        val lesson = buildLesson()

        // Act
        val page = launchSentencePractice(lesson, emptyList())

        // Assert
        page.assertSummaryDisplayed()
            .assertDrillingControlsAbsent()
    }

    // TC-010 (P1): Single-sentence session completes directly to summary after one correct check
    @Test
    fun tc010_singleSentenceSession_completesToSummaryAfterCorrectCheck() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Ја сам",
            tokensJson = """["ја","сам"]""",
            distractorTokensJson = "[]"
        )
        val page = launchSentencePractice(lesson, listOf(sentence))

        // Act
        page.tapPoolChip("ја")
            .tapPoolChip("сам")
            .tapCheckAnswer()

        // Assert - immediate feedback
        page.assertFeedbackDisplayed("Правильно!")

        // Act - advance past delay(1200)
        page.advanceAutoAdvanceDelay(1300L)

        // Assert - summary & DB
        page.assertSummaryDisplayed()
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)?.correctAnswersInLast100 == 1 }
        }
        val updated = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(1, updated.correctAnswersInLast100)
        assertEquals(1, updated.totalAnswersInLast100)
    }

    // TC-011 (P0): check_answer_button disabled when assembly_bay is empty (DT Col 1 / Illegal T12)
    @Test
    fun tc011_checkAnswerButton_disabledWhenAssemblyBayEmpty() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence()
        val page = launchSentencePractice(lesson, listOf(sentence))
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())

        // Act
        page.assertCheckButtonDisabled()
            .tapCheckAnswer()

        // Assert
        page.assertCheckButtonDisabled()
            .assertFeedbackAbsent("Правильно!")
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(0, dbLesson.totalAnswersInLast100)
        assertNull(ttsShadow.lastSpokenText)
    }

    // TC-012 (P0): Correct Cyrillic answer check triggers feedback, recordAnswer(true), haptic, audio, and auto-advance (DT Col 2)
    @Test
    fun tc012_correctCyrillicAnswer_triggersFeedbackRecordAudioHapticAndAutoAdvance() {
        // Arrange
        val lesson = buildLesson()
        val s1 = buildSentence(
            id = "qa_sp_s1",
            sourceSentence = "Я здесь",
            targetSentence = "Ја сам овде",
            targetSentenceLatin = "Ja sam ovde",
            tokensJson = """["ја","сам","овде"]""",
            distractorTokensJson = """["тамо"]"""
        )
        val s2 = buildSentence(
            id = "qa_sp_s2",
            sourceSentence = "Ты дома",
            targetSentence = "Ти си код куће",
            targetSentenceLatin = "Ti si kod kuće",
            tokensJson = """["ти","си","код","куће"]""",
            distractorTokensJson = """["овде"]"""
        )
        val page = launchSentencePractice(lesson, listOf(s1, s2), isLatin = false)
        val firstIsS1 = runCatching {
            composeRule.onNodeWithTag("source_sentence_text").assertExists()
            page.assertSourceSentenceDisplayed("Я здесь")
            true
        }.getOrDefault(false)
        val activeTokens = if (firstIsS1) listOf("ја", "сам", "овде") else listOf("ти", "си", "код", "куће")
        val activeTarget = if (firstIsS1) s1.targetSentence else s2.targetSentence
        val nextSource = if (firstIsS1) s2.sourceSentence else s1.sourceSentence

        // Act
        activeTokens.forEach { page.tapPoolChip(it) }
        page.tapCheckAnswer()

        // Assert - feedback, audio, haptic, DB
        page.assertFeedbackDisplayed("Правильно!")
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        assertEquals(activeTarget, ttsShadow.lastSpokenText)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))
        assertEquals(50L, vibratorShadow.milliseconds)

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)?.correctAnswersInLast100 == 1 }
        }

        // Act - auto-advance
        page.advanceAutoAdvanceDelay(1300L)

        // Assert - second sentence loaded
        page.assertSourceSentenceDisplayed(nextSource)
    }

    // TC-013 (P0): Incorrect Cyrillic answer check reveals target, records false, vibrates 120 ms, and re-queues sentence (DT Col 3)
    @Test
    fun tc013_incorrectCyrillicAnswer_revealsTargetRecordsFalseVibratesAndRequeues() {
        // Arrange
        val lesson = buildLesson()
        val s1 = buildSentence(
            id = "qa_sp_s1",
            sourceSentence = "Я студент",
            targetSentence = "Ја сам студент",
            tokensJson = """["ја","сам","студент"]""",
            distractorTokensJson = """["нисам"]"""
        )
        val page = launchSentencePractice(lesson, listOf(s1), isLatin = false)
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())

        // Act - submit wrong token
        page.tapPoolChip("нисам")
            .tapCheckAnswer()

        // Assert - wrong feedback, 120ms vibration, no TTS, DB false
        page.assertFeedbackDisplayed("Правильный ответ: Ја сам студент")
        assertNull(ttsShadow.lastSpokenText)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))
        assertEquals(120L, vibratorShadow.milliseconds)

        composeRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)?.totalAnswersInLast100 == 1 }
        }
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(0, dbLesson.correctAnswersInLast100)

        // Act - fix and complete first pass; re-queued s1 should appear again
        page.tapPlacedChip("нисам")
            .tapPoolChip("ја")
            .tapPoolChip("сам")
            .tapPoolChip("студент")
            .tapCheckAnswer()
            .advanceAutoAdvanceDelay(1300L)

        // Assert - sentence was appended to sentenceQueue, so we are still in drilling state on s1
        page.assertSourceSentenceDisplayed("Я студент")
    }

    // TC-014 (P0): Correct Latin answer check validates against targetSentenceLatin (DT Col 4)
    @Test
    fun tc014_correctLatinAnswer_validatesAgainstTargetSentenceLatin() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            sourceSentence = "Я студент",
            targetSentence = "Ја сам студент",
            targetSentenceLatin = "Ja sam student",
            tokensJson = """["ja","sam","student"]""",
            distractorTokensJson = """["ti"]"""
        )
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = true)

        // Act
        page.tapPoolChip("ja")
            .tapPoolChip("sam")
            .tapPoolChip("student")
            .tapCheckAnswer()

        // Assert
        page.assertFeedbackDisplayed("Правильно!")
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        assertEquals("Ja sam student", ttsShadow.lastSpokenText)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)?.correctAnswersInLast100 == 1 }
        }
    }

    // TC-015 (P0): Incorrect Latin answer check reveals targetSentenceLatin and records false (DT Col 5)
    @Test
    fun tc015_incorrectLatinAnswer_revealsTargetSentenceLatinAndRecordsFalse() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            sourceSentence = "Я студент",
            targetSentence = "Ја сам студент",
            targetSentenceLatin = "Ja sam student",
            tokensJson = """["ja","sam","student"]""",
            distractorTokensJson = """["nisam"]"""
        )
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = true)

        // Act
        page.tapPoolChip("nisam")
            .tapCheckAnswer()

        // Assert
        page.assertFeedbackDisplayed("Правильный ответ: Ja sam student")
        composeRule.waitUntil(timeoutMillis = 5_000) {
            runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)?.totalAnswersInLast100 == 1 }
        }
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(0, dbLesson.correctAnswersInLast100)
    }

    // TC-016 (P2): Tapping chips moves tokens between tile_pool and assembly_bay (S2 -> S3 -> S3 -> S2)
    @Test
    fun tc016_tappingChips_movesTokensBetweenPoolAndAssemblyBay() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Ја сам",
            tokensJson = """["ја","сам"]""",
            distractorTokensJson = "[]"
        )
        val page = launchSentencePractice(lesson, listOf(sentence))
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())

        // Act & Assert - S2 -> S3
        page.assertCheckButtonDisabled()
            .tapPoolChip("ја")
            .assertCheckButtonEnabled()
            .assertPlacedChipCount("ја", 1)
            .assertPoolChipCount("ја", 0)

        // S3 -> S3
        page.tapPoolChip("сам")
            .assertCheckButtonEnabled()
            .assertPlacedChipCount("сам", 1)
            .tapPlacedChip("сам")
            .assertCheckButtonEnabled()
            .assertPoolChipCount("сам", 1)

        // S3 -> S2
        page.tapPlacedChip("ја")
            .assertCheckButtonDisabled()
            .assertPoolChipCount("ја", 1)
        assertNull(ttsShadow.lastSpokenText)
    }

    // TC-017 (P2): Modifying tokens after incorrect validation clears error feedback (S5 -> S3 -> S2)
    @Test
    fun tc017_modifyingTokensAfterWrongValidation_clearsErrorFeedback() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Ја сам",
            tokensJson = """["ја","сам"]""",
            distractorTokensJson = """["ти"]"""
        )
        val page = launchSentencePractice(lesson, listOf(sentence))
        page.tapPoolChip("ти")
            .tapCheckAnswer()
            .assertFeedbackDisplayed("Правильный ответ: Ја сам")

        // Act - S5 -> S3
        page.tapPoolChip("ја")

        // Assert - feedback cleared, button still enabled
        page.assertFeedbackAbsent("Правильный ответ: Ја сам")
            .assertCheckButtonEnabled()

        // Act - S3 -> S2
        page.tapPlacedChip("ти")
            .tapPlacedChip("ја")

        // Assert - button disabled
        page.assertCheckButtonDisabled()
    }

    // TC-018 (P2): Help dialog opens with grammarHint and dismisses via "Понятно" or onDismissRequest
    @Test
    fun tc018_helpDialog_opensWithHintAndDismissesCleanly() {
        // Arrange
        val lesson = buildLesson()
        val hint = "Помните о порядке энклитик."
        val sentence = buildSentence(grammarHint = hint)
        val page = launchSentencePractice(lesson, listOf(sentence))
        page.tapPoolChip("ја")

        // Act - open & dismiss via button
        page.tapHintButton()
            .assertHintDialogDisplayed(hint)
            .tapDismissHintDialog()

        // Assert - closed & placedTokens preserved
        page.assertHintDialogAbsent()
            .assertPlacedChipCount("ја", 1)

        // Act - open & dismiss via onDismissRequest (back press)
        page.tapHintButton()
            .assertHintDialogDisplayed(hint)
            .dismissDialogViaBackPress()

        // Assert
        page.assertHintDialogAbsent()
            .assertPlacedChipCount("ја", 1)
    }

    // TC-019 (P2): Drilling controls are inaccessible in Finished/Empty summary state (Illegal T13)
    @Test
    fun tc019_drillingControls_absentInSummaryState() {
        // Arrange
        val lesson = buildLesson()

        // Act
        val page = launchSentencePractice(lesson, emptyList())

        // Assert
        page.assertDrillingControlsAbsent()
            .assertSummaryDisplayed()
    }

    // TC-020 (P2): Pairwise Config 2: Cyrillic, Malformed tokensJson, Empty distractors, Non-blank hint
    @Test
    fun tc020_pairwiseConfig2_cyrillicMalformedTokensEmptyDistractorsNonBlankHint() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Добар дан",
            tokensJson = "{bad",
            distractorTokensJson = "[]",
            grammarHint = "Hint"
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = false)

        // Assert
        page.assertPoolChipCount("добар", 1)
            .assertPoolChipCount("дан", 1)
            .tapHintButton()
            .assertHintDialogDisplayed("Hint")
            .tapDismissHintDialog()
    }

    // TC-021 (P2): Pairwise Config 3: Cyrillic, Valid tokensJson, Malformed distractors, Blank hint
    @Test
    fun tc021_pairwiseConfig3_cyrillicValidTokensMalformedDistractorsBlankHint() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Добар дан",
            tokensJson = """["добар","дан"]""",
            distractorTokensJson = "bad_json",
            grammarHint = "   "
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = false)

        // Assert
        page.assertPoolChipCount("добар", 1)
            .assertPoolChipCount("дан", 1)
            .tapHintButton()
            .assertHintDialogDisplayed("Обратите внимание на порядок слов и позицию кратких форм вспомогательных глаголов.")
            .tapDismissHintDialog()
    }

    // TC-022 (P2): Pairwise Config 4: Latin, Malformed tokensJson, Non-empty distractors, Blank hint
    @Test
    fun tc022_pairwiseConfig4_latinMalformedTokensNonEmptyDistractorsBlankHint() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Добар дан",
            targetSentenceLatin = "Dobar dan",
            tokensJson = "{bad",
            distractorTokensJson = """["noć"]""",
            grammarHint = ""
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = true)

        // Assert
        page.assertAnyPoolChipExists()
            .assertPoolChipCount("noć", 1)
            .tapHintButton()
            .assertHintDialogDisplayed("Обратите внимание на порядок слов и позицию кратких форм вспомогательных глаголов.")
            .tapDismissHintDialog()
    }

    // TC-023 (P2): Pairwise Config 5: Latin, Valid tokensJson, Empty distractors, Blank hint
    @Test
    fun tc023_pairwiseConfig5_latinValidTokensEmptyDistractorsBlankHint() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Добар дан",
            targetSentenceLatin = "Dobar dan",
            tokensJson = """["dobar","dan"]""",
            distractorTokensJson = "[]",
            grammarHint = ""
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = true)

        // Assert
        page.assertPoolChipCount("dobar", 1)
            .assertPoolChipCount("dan", 1)
            .tapHintButton()
            .assertHintDialogDisplayed("Обратите внимание на порядок слов и позицию кратких форм вспомогательных глаголов.")
            .tapDismissHintDialog()
    }

    // TC-024 (P2): Pairwise Config 6: Latin, Malformed tokensJson, Malformed distractors, Non-blank hint
    @Test
    fun tc024_pairwiseConfig6_latinMalformedTokensMalformedDistractorsNonBlankHint() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "Добар дан",
            targetSentenceLatin = "Dobar dan",
            tokensJson = "[",
            distractorTokensJson = "[",
            grammarHint = "Latin hint"
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = true)

        // Assert
        page.assertAnyPoolChipExists()
            .tapHintButton()
            .assertHintDialogDisplayed("Latin hint")
            .tapDismissHintDialog()
    }

    // TC-025 (P2): Speaker IconButton plays target sentence audio without recording an answer
    @Test
    fun tc025_speakerIconButton_playsTargetAudioWithoutRecordingAnswer() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(targetSentence = "Ја сам студент")
        val page = launchSentencePractice(lesson, listOf(sentence), isLatin = false)

        // Act
        page.tapSpeakerButton()

        // Assert
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        assertEquals("Ја сам студент", ttsShadow.lastSpokenText)
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(0, dbLesson.totalAnswersInLast100)
    }

    // TC-026 (P2): Top bar back button exits practice without recording unanswered attempt
    @Test
    fun tc026_backButton_exitsPracticeWithoutRecordingAnswer() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence()
        var backInvoked = false
        val page = launchSentencePractice(lesson, listOf(sentence)) {
            backInvoked = true
        }

        // Act
        page.tapPoolChip("ја")
            .tapBackButton()

        // Assert
        assertTrue(backInvoked)
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(0, dbLesson.totalAnswersInLast100)
    }

    // TC-027 (P0): SQL injection / special characters in sourceSentence and tokensJson handled safely
    @Test
    fun tc027_sqlInjectionAndSpecialChars_handledSafely() {
        // Arrange
        val lesson = buildLesson()
        val adversarialSource = "'; DROP TABLE sentences;--"
        val sentence = buildSentence(
            sourceSentence = adversarialSource,
            tokensJson = """["SELECT*","1=1"]""",
            distractorTokensJson = "[]"
        )

        // Act
        val page = launchSentencePractice(lesson, listOf(sentence))

        // Assert
        page.assertSourceSentenceDisplayed(adversarialSource)
            .assertPoolChipCount("select", 1)
            .assertPoolChipCount("11", 1)
        val persisted = runBlocking(Dispatchers.IO) { db.sentenceDao().getSentencesForLessonSync(lesson.id) }
        assertEquals(1, persisted.size)
        assertEquals(adversarialSource, persisted[0].sourceSentence)
    }

    // TC-028 (P2): Duplicate identical tokens in tokensJson move individually by index
    @Test
    fun tc028_duplicateIdenticalTokens_moveIndividuallyByIndex() {
        // Arrange
        val lesson = buildLesson()
        val sentence = buildSentence(
            targetSentence = "И ти и ја",
            tokensJson = """["и","ти","и","ја"]""",
            distractorTokensJson = "[]"
        )
        val page = launchSentencePractice(lesson, listOf(sentence))

        // Act
        page.tapPoolChip("и", index = 0)

        // Assert
        page.assertPlacedChipCount("и", 1)
            .assertPoolChipCount("и", 1)
    }
}
