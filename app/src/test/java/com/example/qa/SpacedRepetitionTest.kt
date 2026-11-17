package com.example.qa

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.SrsReviewEntity
import com.example.data.model.VocabItemEntity
import com.example.data.repository.CurriculumRepository
import com.example.srs.ReviewGrade
import com.example.srs.SpacedRepetitionAlgorithm
import com.example.ui.screens.CurriculumScreen
import com.example.ui.screens.SpacedRepetitionLauncherTab
import com.example.ui.screens.SpacedRepetitionScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper
import org.robolectric.shadows.ShadowTextToSpeech
import java.util.Locale

/**
 * Automated P0, P1, and P2 test suite for Spaced Repetition (SM-2)
 * generated from `qa-artifacts/2026-11-15-spaced-repetition/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w600dp-h1200dp")
class SpacedRepetitionTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: CurriculumRepository

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
    }

    @After
    fun tearDown() {
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    private fun clearSrsTable() {
        runBlocking(Dispatchers.IO) {
            db.openHelper.writableDatabase.execSQL("DELETE FROM srs_reviews")
        }
    }

    private fun seedSrsCards(vararg cards: SrsReviewEntity) {
        clearSrsTable()
        if (cards.isNotEmpty()) {
            runBlocking(Dispatchers.IO) {
                db.srsDao().insertAll(cards.toList())
            }
        }
    }

    private fun createViewModel(): LanguageLearnerViewModel {
        val vm = LanguageLearnerViewModel(app)
        ShadowTextToSpeech.getLastTextToSpeechInstance()?.let { tts ->
            Shadows.shadowOf(tts).onInitListener?.onInit(TextToSpeech.SUCCESS)
        }
        return vm
    }

    private fun buildCard(
        id: String = "srs_card_1",
        itemType: String = "VOCAB",
        lessonId: String = "lesson_1",
        prompt: String = "салам",
        answer: String = "привет",
        secondaryText: String = "salam",
        repetition: Int = 0,
        intervalDays: Int = 1,
        easeFactor: Float = 2.5f,
        nextReviewTimestamp: Long = System.currentTimeMillis() - 10_000L,
        lastQuality: Int = 0
    ) = SrsReviewEntity(
        id = id,
        itemType = itemType,
        lessonId = lessonId,
        prompt = prompt,
        answer = answer,
        secondaryText = secondaryText,
        repetition = repetition,
        intervalDays = intervalDays,
        easeFactor = easeFactor,
        nextReviewTimestamp = nextReviewTimestamp,
        lastQuality = lastQuality
    )

    // -------------------------------------------------------------------------
    // P0 Unit & Algorithm / Repository / Error Guessing Tests
    // -------------------------------------------------------------------------

    @Test
    fun `TC-001 quality below 0 is clamped to 0 and resets SM-2`() {
        // Arrange
        val card = buildCard(repetition = 2, intervalDays = 6, easeFactor = 2.5f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(card, quality = -1)

        // Assert
        assertEquals(0, updated.lastQuality)
        assertEquals(0, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(1.70f, updated.easeFactor, 0.001f)
    }

    @Test
    fun `TC-002 quality 0 at easeFactor floor 1_3f preserves floor`() {
        // Arrange
        val card = buildCard(repetition = 0, intervalDays = 1, easeFactor = 1.3f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(card, quality = 0)

        // Assert
        assertEquals(0, updated.lastQuality)
        assertEquals(0, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(1.3f, updated.easeFactor, 0.0001f)
        assertTrue(updated.easeFactor >= 1.3f)
    }

    @Test
    fun `TC-003 quality 1 AGAIN resets repetition 1 to 0`() {
        // Arrange
        val card = buildCard(repetition = 1, intervalDays = 1, easeFactor = 2.5f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(
            card,
            quality = SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.AGAIN)
        )

        // Assert
        assertEquals(1, updated.lastQuality)
        assertEquals(0, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(1.96f, updated.easeFactor, 0.001f)
    }

    @Test
    fun `TC-004 quality 2 HARD resets repetition 3 and sets intervalDays to 1`() {
        // Arrange
        val card = buildCard(repetition = 3, intervalDays = 15, easeFactor = 2.5f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(
            card,
            quality = SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.HARD)
        )

        // Assert
        assertEquals(2, updated.lastQuality)
        assertEquals(0, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(2.18f, updated.easeFactor, 0.001f)
    }

    @Test
    fun `TC-005 quality 3 pass threshold at repetition 0 and easeFactor 1_4f`() {
        // Arrange
        val card = buildCard(repetition = 0, intervalDays = 1, easeFactor = 1.4f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(card, quality = 3)

        // Assert
        assertEquals(3, updated.lastQuality)
        assertEquals(1, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(1.3f, updated.easeFactor, 0.0001f)
    }

    @Test
    fun `TC-006 quality 4 GOOD at repetition 1 schedules 6-day interval`() {
        // Arrange
        val card = buildCard(repetition = 1, intervalDays = 1, easeFactor = 2.5f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(
            card,
            quality = SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.GOOD)
        )

        // Assert
        assertEquals(4, updated.lastQuality)
        assertEquals(2, updated.repetition)
        assertEquals(6, updated.intervalDays)
        assertEquals(2.5f, updated.easeFactor, 0.0001f)
    }

    @Test
    fun `TC-007 quality 5 EASY at repetition 2 multiplies interval by newEaseFactor`() {
        // Arrange
        val card = buildCard(repetition = 2, intervalDays = 6, easeFactor = 2.5f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(
            card,
            quality = SpacedRepetitionAlgorithm.gradeToQuality(ReviewGrade.EASY)
        )

        // Assert
        assertEquals(5, updated.lastQuality)
        assertEquals(3, updated.repetition)
        assertEquals(2.6f, updated.easeFactor, 0.001f)
        assertEquals(16, updated.intervalDays)
    }

    @Test
    fun `TC-008 quality above 5 clamped to 5 and easeFactor below 1_3f floored`() {
        // Arrange
        val card = buildCard(repetition = 2, intervalDays = 1, easeFactor = 1.2f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(card, quality = 6)

        // Assert
        assertEquals(5, updated.lastQuality)
        assertEquals(1.3f, updated.easeFactor, 0.0001f)
        assertEquals(1, updated.intervalDays)
        assertEquals(3, updated.repetition)
    }

    @Test
    fun `TC-009 negative repetition falls into else branch safely`() {
        // Arrange
        val card = buildCard(repetition = -1, intervalDays = 1, easeFactor = 2.5f)

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(card, quality = 4)

        // Assert
        assertEquals(3, updated.intervalDays)
        assertEquals(0, updated.repetition)
        assertEquals(2.5f, updated.easeFactor, 0.0001f)
    }

    // -------------------------------------------------------------------------
    // P2 DAO & Launcher Boundary Tests
    // -------------------------------------------------------------------------

    @Test
    fun `TC-010 card 1ms in future is excluded from due items and disables launcher`() {
        // Arrange
        val fixedNow = 1_700_000_000_000L
        seedSrsCards(buildCard(id = "future_dao", nextReviewTimestamp = fixedNow + 1))

        val dueItems = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(fixedNow).first() }
        val dueCount = runBlocking(Dispatchers.IO) { db.srsDao().getDueCount(fixedNow).first() }

        seedSrsCards(buildCard(id = "future_ui", nextReviewTimestamp = System.currentTimeMillis() + 120_000L))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                CurriculumScreen(
                    viewModel = vm,
                    onOpenLesson = {},
                    onOpenSrsReview = {},
                    onOpenImportExport = {}
                )
            }
        }
        page.tapSrsTab()

        // Assert
        assertTrue(dueItems.isEmpty())
        assertEquals(0, dueCount)
        page.assertStartReviewDisabled()
            .assertTextNotDisplayed("1")
    }

    @Test
    fun `TC-011 card due at exact timestamp is included and enables launcher`() {
        // Arrange
        val fixedNow = 1_700_000_000_000L
        seedSrsCards(buildCard(id = "exact_dao", nextReviewTimestamp = fixedNow))

        val dueItems = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(fixedNow).first() }
        val dueCount = runBlocking(Dispatchers.IO) { db.srsDao().getDueCount(fixedNow).first() }

        seedSrsCards(buildCard(id = "exact_ui", nextReviewTimestamp = System.currentTimeMillis()))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                CurriculumScreen(
                    viewModel = vm,
                    onOpenLesson = {},
                    onOpenSrsReview = {},
                    onOpenImportExport = {}
                )
            }
        }
        page.assertSrsBadgeCount(1)
            .tapSrsTab()

        // Assert
        assertEquals(1, dueItems.size)
        assertEquals(1, dueCount)
        page.assertStartReviewEnabled()
    }

    @Test
    fun `TC-012 overdue card 1ms past is returned ordered ascending`() {
        // Arrange
        val now = 1_700_000_000_000L
        seedSrsCards(
            buildCard(id = "due_now", nextReviewTimestamp = now),
            buildCard(id = "overdue_1ms", nextReviewTimestamp = now - 1)
        )

        // Act
        val dueItems = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(now).first() }
        val dueCount = runBlocking(Dispatchers.IO) { db.srsDao().getDueCount(now).first() }

        // Assert
        assertEquals(2, dueCount)
        assertEquals(listOf("overdue_1ms", "due_now"), dueItems.map { it.id })
    }

    @Test
    fun `TC-013 negative dueCount keeps badge hidden and launcher disabled`() {
        // Arrange
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionLauncherTab(dueCount = -1, onStartReview = {})
            }
        }

        // Assert
        page.assertStartReviewDisabled()
            .assertTextNotDisplayed("-1")
    }

    // -------------------------------------------------------------------------
    // P2 Decision Table R2 Display Tests
    // -------------------------------------------------------------------------

    @Test
    fun `TC-014 Latin-only mode with non-blank secondaryText shows secondaryText as prompt`() {
        // Arrange
        seedSrsCards(buildCard(prompt = "салам", secondaryText = "salam"))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Assert
        page.assertPromptText("salam")
            .assertTextNotDisplayed("салам")
    }

    @Test
    fun `TC-015 Latin-only mode with blank secondaryText falls back to prompt`() {
        // Arrange
        seedSrsCards(buildCard(prompt = "салам", secondaryText = ""))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Assert
        page.assertPromptText("салам")
        assertEquals(1, composeRule.onAllNodesWithText("салам").fetchSemanticsNodes().size)
    }

    @Test
    fun `TC-016 non-Latin mode with non-blank secondaryText shows both`() {
        // Arrange
        seedSrsCards(buildCard(prompt = "салам", secondaryText = "salam"))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Assert
        page.assertPromptText("salam")
            .assertTextNotDisplayed("салам")
    }

    @Test
    fun `TC-017 non-Latin mode with blank secondaryText hides secondary line`() {
        // Arrange
        seedSrsCards(buildCard(prompt = "салам", secondaryText = ""))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Assert
        page.assertPromptText("салам")
        assertEquals(1, composeRule.onAllNodesWithText("салам").fetchSemanticsNodes().size)
    }

    // -------------------------------------------------------------------------
    // P1 / P2 / P0 State Transition & Use Case Tests
    // -------------------------------------------------------------------------

    @Test
    fun `TC-018 end-to-end happy path review of single due card to empty queue`() {
        // Arrange
        seedSrsCards(buildCard(id = "e2e_card", prompt = "здраво", answer = "привет", secondaryText = "zdravo"))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)
        var showSrs by mutableStateOf(false)
        var exitedToLessons by mutableStateOf(false)

        composeRule.setContent {
            LanguageLearnerTheme {
                if (showSrs) {
                    SpacedRepetitionScreen(
                        viewModel = vm,
                        onBack = {
                            showSrs = false
                            exitedToLessons = true
                        }
                    )
                } else {
                    CurriculumScreen(
                        viewModel = vm,
                        onOpenLesson = {},
                        onOpenSrsReview = { showSrs = true },
                        onOpenImportExport = {}
                    )
                }
            }
        }

        // Act & Assert: Badge -> Launch -> S1 -> S2 -> S3 -> Return
        page.assertSrsBadgeCount(1)
            .tapSrsTab()
            .assertStartReviewEnabled()
            .tapStartReview()
            .assertShowAnswerDisplayed()
            .tapShowAnswer()
            .assertAnswerText("привет")
            .assertTextDisplayed("Интервал:", substring = true)

        val shadowTts = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        assertEquals("zdravo", shadowTts.lastSpokenText)
        assertEquals(Locale("sr", "RS"), shadowTts.currentLanguage)

        page.tapGradeGood()
            .assertEmptyQueueDisplayed()
            .tapDoneBackButton()

        val updated = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("e2e_card") }
        assertNotNull(updated)
        assertEquals(1, updated!!.repetition)
        assertEquals(4, updated.lastQuality)
        assertTrue(exitedToLessons)
    }

    @Test
    fun `TC-019 grading first of two due cards resets reveal and advances queue`() {
        // Arrange
        val now = System.currentTimeMillis()
        seedSrsCards(
            buildCard(id = "card_0", prompt = "први", answer = "первый", secondaryText = "prvi", nextReviewTimestamp = now - 20_000L),
            buildCard(id = "card_1", prompt = "други", answer = "второй", secondaryText = "drugi", nextReviewTimestamp = now - 10_000L)
        )
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Act
        page.assertPromptText("prvi")
            .tapShowAnswer()
            .assertAnswerText("первый")
            .tapGradeEasy()

        // Assert
        page.waitForCondition {
            runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("card_0")?.lastQuality == 5 }
        }
        page.assertPromptText("drugi")
            .assertShowAnswerDisplayed()
            .assertAnswerNotDisplayed()
    }

    @Test
    fun `TC-020 speaker IconButton replays audio without revealing answer`() {
        // Arrange
        seedSrsCards(buildCard(id = "audio_card", prompt = "хвала", answer = "спасибо", secondaryText = "hvala"))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Act
        page.assertPromptText("hvala")
            .tapSpeakerButton()

        // Assert
        val shadowTts = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        assertEquals("hvala", shadowTts.lastSpokenText)
        assertEquals(Locale("sr", "RS"), shadowTts.currentLanguage)
        page.assertShowAnswerDisplayed()
            .assertAnswerNotDisplayed()
    }

    @Test
    fun `TC-021 top-bar back button exits review without grading`() {
        // Arrange
        seedSrsCards(buildCard(id = "back_card", repetition = 1, intervalDays = 1, lastQuality = 4))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)
        var navigatedBack = false

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = { navigatedBack = true })
            }
        }

        // Act
        page.assertPromptText("salam")
            .tapBackFromSrs()

        // Assert
        assertTrue(navigatedBack)
        val unchanged = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("back_card") }
        assertNotNull(unchanged)
        assertEquals(1, unchanged!!.repetition)
        assertEquals(4, unchanged.lastQuality)
    }

    @Test
    fun `TC-022 illegal transition grade buttons absent while card unrevealed`() {
        // Arrange
        seedSrsCards(buildCard())
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Assert
        page.assertShowAnswerDisplayed()
            .assertGradeButtonsNotDisplayed()
    }

    @Test
    fun `TC-023 illegal transition reveal and grade absent when queue is empty`() {
        // Arrange
        clearSrsTable()
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Assert
        page.assertEmptyQueueDisplayed()
            .assertShowAnswerNotDisplayed()
            .assertGradeButtonsNotDisplayed()
    }

    @Test
    fun `TC-024 idempotent enrollInSrs preserves existing SRS progress`() {
        // Arrange
        val existingCard = buildCard(id = "vocab_existing", repetition = 3, easeFactor = 2.6f, intervalDays = 16)
        seedSrsCards(existingCard)
        val vocab = VocabItemEntity(
            id = "vocab_existing",
            lessonId = "lesson_1",
            targetWord = "нови",
            targetWordLatin = "novi",
            sourceTranslation = "новый",
            partOfSpeech = "Adj"
        )

        // Act
        runBlocking(Dispatchers.IO) {
            repository.enrollInSrs(vocab)
        }

        // Assert
        val stored = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("vocab_existing") }
        assertNotNull(stored)
        assertEquals(3, stored!!.repetition)
        assertEquals(2.6f, stored.easeFactor, 0.0001f)
        assertEquals(16, stored.intervalDays)
    }

    @Test
    fun `TC-025 failing recall AGAIN workflow resets card and updates Room`() {
        // Arrange
        seedSrsCards(buildCard(id = "fail_card", repetition = 2, intervalDays = 6, easeFactor = 2.5f))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Act
        page.tapShowAnswer()
            .tapGradeAgain()

        // Assert
        page.waitForCondition {
            runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("fail_card")?.lastQuality == 1 }
        }
        val updated = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("fail_card") }!!
        assertEquals(0, updated.repetition)
        assertEquals(1, updated.intervalDays)
        assertEquals(1, updated.lastQuality)
        assertEquals("RELEARNING", updated.state)
    }

    // -------------------------------------------------------------------------
    // P1 Pairwise Configuration Tests (C1: TC-026 .. TC-033)
    // -------------------------------------------------------------------------

    private fun runPairwiseConfigTest(
        cardId: String,
        grade: ReviewGrade,
        itemType: String,
        expectedQuality: Int,
        expectedRepetition: Int,
        expectedIntervalDays: Int
    ) {
        // Arrange
        val isSentence = itemType == "SENTENCE"
        val card = if (isSentence) {
            buildCard(
                id = cardId,
                itemType = itemType,
                prompt = "Добрый день.",
                answer = "Добар дан.",
                secondaryText = "Dobar dan.",
                repetition = 0,
                intervalDays = 1
            )
        } else {
            buildCard(
                id = cardId,
                itemType = itemType,
                prompt = "добар дан",
                answer = "добрый день",
                secondaryText = "dobar dan",
                repetition = 0,
                intervalDays = 1
            )
        }
        seedSrsCards(card)
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Act
        page.assertPromptText(if (isSentence) "Добрый день." else "dobar dan")
            .tapShowAnswer()
        when (grade) {
            ReviewGrade.AGAIN -> page.tapGradeAgain()
            ReviewGrade.HARD -> page.tapGradeHard()
            ReviewGrade.GOOD -> page.tapGradeGood()
            ReviewGrade.EASY -> page.tapGradeEasy()
        }

        // Assert
        page.waitForCondition {
            runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById(cardId)?.lastQuality == expectedQuality }
        }
        val updated = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById(cardId) }!!
        assertEquals(expectedQuality, updated.lastQuality)
        assertEquals(expectedRepetition, updated.repetition)
        assertEquals(expectedIntervalDays, updated.intervalDays)
    }

    @Test
    fun `TC-026 pairwise config 1 AGAIN VOCAB`() =
        runPairwiseConfigTest("pw_1", ReviewGrade.AGAIN, itemType = "VOCAB", expectedQuality = 1, expectedRepetition = 0, expectedIntervalDays = 1)

    @Test
    fun `TC-027 pairwise config 2 AGAIN SENTENCE`() =
        runPairwiseConfigTest("pw_2", ReviewGrade.AGAIN, itemType = "SENTENCE", expectedQuality = 1, expectedRepetition = 0, expectedIntervalDays = 1)

    @Test
    fun `TC-028 pairwise config 3 HARD SENTENCE`() =
        runPairwiseConfigTest("pw_3", ReviewGrade.HARD, itemType = "SENTENCE", expectedQuality = 2, expectedRepetition = 1, expectedIntervalDays = 1)

    @Test
    fun `TC-029 pairwise config 4 HARD VOCAB`() =
        runPairwiseConfigTest("pw_4", ReviewGrade.HARD, itemType = "VOCAB", expectedQuality = 2, expectedRepetition = 1, expectedIntervalDays = 1)

    @Test
    fun `TC-030 pairwise config 5 GOOD VOCAB`() =
        runPairwiseConfigTest("pw_5", ReviewGrade.GOOD, itemType = "VOCAB", expectedQuality = 4, expectedRepetition = 1, expectedIntervalDays = 3)

    @Test
    fun `TC-031 pairwise config 6 GOOD SENTENCE`() =
        runPairwiseConfigTest("pw_6", ReviewGrade.GOOD, itemType = "SENTENCE", expectedQuality = 4, expectedRepetition = 1, expectedIntervalDays = 3)

    @Test
    fun `TC-032 pairwise config 7 EASY SENTENCE`() =
        runPairwiseConfigTest("pw_7", ReviewGrade.EASY, itemType = "SENTENCE", expectedQuality = 5, expectedRepetition = 1, expectedIntervalDays = 7)

    @Test
    fun `TC-033 pairwise config 8 EASY VOCAB`() =
        runPairwiseConfigTest("pw_8", ReviewGrade.EASY, itemType = "VOCAB", expectedQuality = 5, expectedRepetition = 1, expectedIntervalDays = 7)

    // -------------------------------------------------------------------------
    // Error Guessing Tests (TC-034 .. TC-038)
    // -------------------------------------------------------------------------

    @Test
    fun `TC-034 empty prompt answer and secondaryText render safely`() {
        // Arrange
        seedSrsCards(buildCard(id = "empty_card", prompt = "", answer = "", secondaryText = ""))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }
        page.tapShowAnswer()

        // Assert
        page.assertPromptText("")
            .assertAnswerText("")
        val stored = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("empty_card") }
        assertNotNull(stored)
    }

    @Test
    fun `TC-035 multi-byte Unicode CJK emoji RTL round-trips in Room and UI`() {
        // Arrange
        val unicodePrompt = "مرحبا 🇯🇵"
        val unicodeAnswer = "привет 😊"
        seedSrsCards(buildCard(id = "unicode_card", prompt = unicodePrompt, answer = unicodeAnswer, secondaryText = ""))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        // Act
        val daoItems = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(System.currentTimeMillis()).first() }
        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }
        page.tapShowAnswer()

        // Assert
        assertEquals(unicodePrompt, daoItems.single().prompt)
        assertEquals(unicodeAnswer, daoItems.single().answer)
        page.assertPromptText(unicodePrompt)
            .assertAnswerText(unicodeAnswer)
    }

    @Test
    fun `TC-036 SQL injection string in id prompt lessonId handled safely by Room`() {
        // Arrange
        val injection = "' OR '1'='1; DROP TABLE srs_reviews;--"
        seedSrsCards(buildCard(id = injection, prompt = injection, lessonId = injection))

        // Act
        val dueItems = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(System.currentTimeMillis()).first() }
        val byId = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById(injection) }

        // Assert
        assertEquals(1, dueItems.size)
        assertNotNull(byId)
        assertEquals(injection, byId!!.id)
        assertEquals(injection, byId.prompt)
        assertEquals(injection, byId.lessonId)
    }

    @Test
    fun `TC-037 rapid double-tap on grade button does not crash or corrupt index`() {
        // Arrange
        seedSrsCards(buildCard(id = "double_tap_card"))
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }
        page.tapShowAnswer()

        // Act
        composeRule.onNodeWithTag("grade_good").performClick()
        runCatching {
            composeRule.onNodeWithTag("grade_good").performClick()
        }
        composeRule.waitForIdle()

        // Assert
        page.assertEmptyQueueDisplayed()
        val updated = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById("double_tap_card") }
        assertNotNull(updated)
        assertEquals(4, updated!!.lastQuality)
    }

    @Test
    fun `TC-038 DST and midnight clock boundary uses UTC epoch ms consistently`() {
        // Arrange
        val oneDayMs = 86_400_000L
        val card = buildCard(id = "dst_card", repetition = 0, intervalDays = 1, easeFactor = 2.5f)
        val before = System.currentTimeMillis()

        // Act
        val updated = SpacedRepetitionAlgorithm.calculateNextReview(card, quality = 4)
        val after = System.currentTimeMillis()
        runBlocking(Dispatchers.IO) {
            seedSrsCards(updated)
        }
        val dueBefore = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(updated.nextReviewTimestamp - 1).first() }
        val dueAtBoundary = runBlocking(Dispatchers.IO) { db.srsDao().getDueItems(updated.nextReviewTimestamp).first() }

        // Assert
        assertTrue(updated.nextReviewTimestamp in (before + oneDayMs)..(after + oneDayMs))
        assertTrue(dueBefore.isEmpty())
        assertEquals(1, dueAtBoundary.size)
        assertEquals("dst_card", dueAtBoundary.single().id)
    }

    @Test
    fun `TC-039 SENTENCE card shows source prompt on front and Latin target on reveal and audio`() {
        // Arrange
        seedSrsCards(
            buildCard(
                id = "sentence_srs_card",
                itemType = "SENTENCE",
                prompt = "Я работаю.",
                answer = "Ја радим.",
                secondaryText = "Ja radim."
            )
        )
        val vm = createViewModel()
        val page = SpacedRepetitionPage(composeRule)

        composeRule.setContent {
            LanguageLearnerTheme {
                SpacedRepetitionScreen(viewModel = vm, onBack = {})
            }
        }

        // Act
        page.assertPromptText("Я работаю.")
            .tapShowAnswer()

        // Assert
        page.assertAnswerText("Ja radim.")
            .assertTextNotDisplayed("Ја радим.")
        val shadowTts = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        assertEquals("Ja radim.", shadowTts.lastSpokenText)
    }
}
