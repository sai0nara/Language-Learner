package com.example.qa

import android.app.Application
import android.content.Context
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.LessonAnswerEntity
import com.example.data.model.LessonEntity
import com.example.data.model.VocabItemEntity
import com.example.data.repository.CurriculumRepository
import com.example.ui.screens.VocabDrillProgressBar
import com.example.ui.screens.VocabDrillScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
import java.io.File

/**
 * Automated P0, P1, and P2 test suite for Vocabulary Matching Drills (`vocab-drills`)
 * generated from `qa-artifacts/2026-11-15-vocab-drills/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w600dp-h1200dp")
class VocabDrillTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var repository: CurriculumRepository
    private lateinit var viewModel: LanguageLearnerViewModel
    private val tempFiles = mutableListOf<File>()

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
        composeRule.mainClock.autoAdvance = true
        viewModel.viewModelScope.cancel()
        tempFiles.forEach { it.delete() }
        tempFiles.clear()
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
        }
    }

    private fun buildLesson(
        id: String = "qa_vd_lesson_1",
        orderIndex: Int = 100,
        isCompleted: Boolean = false,
        isUnlocked: Boolean = true
    ) = LessonEntity(
        id = id,
        orderIndex = orderIndex,
        title = "QA Vocab Lesson $orderIndex",
        subtitle = "QA Subtitle",
        grammarFocus = "QA Grammar",
        isCompleted = isCompleted,
        masteryScore = 0f,
        isUnlocked = isUnlocked,
        correctAnswersInLast100 = 0,
        totalAnswersInLast100 = 0
    )

    private fun buildVocabItem(
        id: String = "qa_vd_item_1",
        lessonId: String = "qa_vd_lesson_1",
        targetWord: String = "здраво",
        targetWordLatin: String = "zdravo",
        sourceTranslation: String = "привет",
        partOfSpeech: String = "",
        exampleSentence: String = "",
        distractorsJson: String = """["пока"]""",
        audioUri: String = ""
    ) = VocabItemEntity(
        id = id,
        lessonId = lessonId,
        targetWord = targetWord,
        targetWordLatin = targetWordLatin,
        sourceTranslation = sourceTranslation,
        partOfSpeech = partOfSpeech,
        exampleSentence = exampleSentence,
        distractorsJson = distractorsJson,
        audioUri = audioUri
    )

    private val activeTestLesson = mutableStateOf<LessonEntity?>(null)

    private fun launchVocabDrill(
        lesson: LessonEntity,
        items: List<VocabItemEntity>,
        isLatin: Boolean = false,
        onBack: () -> Unit = {}
    ): VocabDrillPage {
        composeRule.mainClock.autoAdvance = true
        if (activeTestLesson.value != null) {
            viewModel.viewModelScope.cancel()
            viewModel = LanguageLearnerViewModel(app)
        }
        runBlocking(Dispatchers.IO) {
            db.lessonDao().insertLesson(lesson)
            if (items.isNotEmpty()) {
                db.vocabDao().insertVocabItems(items)
            }
        }
        viewModel.selectLesson(lesson)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            viewModel.activeVocab.value.map { it.id }.toSet() == items.map { it.id }.toSet()
        }
        val firstMount = activeTestLesson.value == null
        activeTestLesson.value = lesson
        if (firstMount) {
            composeRule.setContent {
                val currentLesson = activeTestLesson.value
                if (currentLesson != null) {
                    key(currentLesson.id) {
                        LanguageLearnerTheme {
                            VocabDrillScreen(
                                lesson = currentLesson,
                                viewModel = viewModel,
                                onBack = onBack
                            )
                        }
                    }
                }
            }
        }
        ShadowLooper.idleMainLooper()
        composeRule.waitForIdle()
        if (items.isNotEmpty()) {
            composeRule.waitUntil(timeoutMillis = 5_000) {
                ShadowLooper.idleMainLooper()
                runCatching {
                    composeRule.onNodeWithTag("target_word_prompt").fetchSemanticsNode()
                    true
                }.getOrDefault(false)
            }
        } else {
            composeRule.waitUntil(timeoutMillis = 5_000) {
                ShadowLooper.idleMainLooper()
                runCatching {
                    composeRule.onNodeWithTag("restart_drill_button").fetchSemanticsNode()
                    true
                }.getOrDefault(false)
            }
        }
        return VocabDrillPage(composeRule)
    }

    // TC-001 (P2): Empty lesson renders completion view immediately
    @Test
    fun tc001_emptyLesson_rendersCompletionViewImmediately() {
        // Arrange
        val lesson = buildLesson(id = "qa_vd_empty")
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())

        // Act
        val page = launchVocabDrill(lesson, emptyList())

        // Assert
        page.assertCompletionViewDisplayed(expectedTotal = 0)
            .assertTargetWordAbsent()
        assertNull(ttsShadow.lastSpokenText)
    }

    // TC-002 (P0): Single-item happy path — Cyrillic auto-audio, correct choice, 40ms haptic, 500ms completion
    @Test
    fun tc002_singleItemHappyPath_playsTargetAudioVibrates40msAndCompletes() {
        // Arrange
        val lesson = buildLesson()
        val item1 = buildVocabItem(
            id = "qa_vd_item_1",
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]"""
        )
        val page = launchVocabDrill(lesson, listOf(item1), isLatin = false)
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))

        // Assert initial load
        page.assertTargetWordDisplayed("zdravo")
        assertEquals("zdravo", ttsShadow.lastSpokenText)

        // Act - tap correct option with manual clock to observe intermediate feedback
        composeRule.mainClock.autoAdvance = false
        page.tapOption("привет")
            .advanceFrame()

        // Assert - intermediate state: progress 100.0% (1 / 1), 40ms haptic, no Russian TTS
        page.assertProgressDisplayed("100.0% (1 / 1)")
        assertEquals(40L, vibratorShadow.milliseconds)
        assertEquals("zdravo", ttsShadow.lastSpokenText)

        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            runBlocking(Dispatchers.IO) {
                db.lessonDao().getLessonById(lesson.id)?.correctAnswersInLast100 == 1
            }
        }
        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            runBlocking(Dispatchers.IO) {
                db.srsDao().getSrsItemById(item1.id) != null
            }
        }
        val srsEntry = runBlocking(Dispatchers.IO) { db.srsDao().getSrsItemById(item1.id) }
        org.junit.Assert.assertNotNull(srsEntry)

        // Act - advance 500ms delay
        page.advanceDelay(550L)

        // Assert - completion view
        page.assertCompletionViewDisplayed(expectedTotal = 1)
    }

    // TC-003 (P1): Multi-item happy path — shuffled queue and incremental progress
    @Test
    fun tc003_multiItemHappyPath_advancesQueueAndUpdatesProgress() {
        // Arrange
        val lesson = buildLesson()
        val item1 = buildVocabItem(
            id = "qa_vd_1",
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]"""
        )
        val item2 = buildVocabItem(
            id = "qa_vd_2",
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            distractorsJson = """["пожалуйста"]"""
        )
        val page = launchVocabDrill(lesson, listOf(item1, item2), isLatin = false)
        val firstPrompt = page.currentTargetWord()
        val (firstCorrect, secondCorrect) = if (firstPrompt == item1.targetWordLatin) {
            item1.sourceTranslation to item2.sourceTranslation
        } else {
            item2.sourceTranslation to item1.sourceTranslation
        }

        // Act - answer 1st item correctly and advance 500ms
        composeRule.mainClock.autoAdvance = false
        page.tapOption(firstCorrect)
            .advanceFrame()
            .assertProgressDisplayed("50.0% (1 / 2)")
            .advanceDelay(550L)

        // Assert - 2nd item displayed with 50.0% (1 / 2)
        val secondPrompt = page.currentTargetWord()
        assertTrue(secondPrompt != firstPrompt)
        page.assertProgressDisplayed("50.0% (1 / 2)")

        // Act - answer 2nd item correctly
        page.tapOption(secondCorrect)
            .advanceFrame()
            .assertProgressDisplayed("100.0% (2 / 2)")
            .advanceDelay(550L)

        // Assert - completion view and 2 DB answers
        page.assertCompletionViewDisplayed(expectedTotal = 2)
        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            runBlocking(Dispatchers.IO) {
                db.lessonDao().getLessonById(lesson.id)?.correctAnswersInLast100 == 2
            }
        }
    }

    // TC-004 (P1): Incorrect choice — 120ms haptic, re-queue to end, 800ms advance, eventual mastery
    @Test
    fun tc004_incorrectChoice_vibrates120msRequeuesItemAndCompletesAfterReview() {
        // Arrange
        val lesson = buildLesson()
        val itemA = buildVocabItem(
            id = "qa_vd_a",
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]"""
        )
        val itemB = buildVocabItem(
            id = "qa_vd_b",
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            distractorsJson = """["нет"]"""
        )
        val page = launchVocabDrill(lesson, listOf(itemA, itemB), isLatin = false)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))

        val firstTarget = page.currentTargetWord()
        val (firstItem, secondItem) = if (firstTarget == itemA.targetWordLatin) itemA to itemB else itemB to itemA
        val firstDistractor = if (firstItem.id == itemA.id) "пока" else "нет"

        // Act - tap incorrect option on first item
        composeRule.mainClock.autoAdvance = false
        page.tapOption(firstDistractor)
            .advanceFrame()

        // Assert - 120ms haptic, progress stays 0.0% (0 / 2), DB false recorded
        assertEquals(120L, vibratorShadow.milliseconds)
        page.assertProgressDisplayed("0.0% (0 / 2)")
        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            runBlocking(Dispatchers.IO) {
                db.lessonDao().getLessonById(lesson.id)?.totalAnswersInLast100 == 1
            }
        }
        assertEquals(0, runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!!.correctAnswersInLast100 })

        // Act - advance 800ms to second item, answer second item correctly
        page.advanceDelay(850L)
            .assertTargetWordDisplayed(secondItem.targetWordLatin)
            .tapOption(secondItem.sourceTranslation)
            .advanceFrame()
            .assertProgressDisplayed("50.0% (1 / 2)")
            .advanceDelay(550L)

        // Assert - re-queued first item appears again
        page.assertTargetWordDisplayed(firstItem.targetWordLatin)

        // Act - answer re-queued item correctly
        page.tapOption(firstItem.sourceTranslation)
            .advanceFrame()
            .assertProgressDisplayed("100.0% (2 / 2)")
            .advanceDelay(550L)

        // Assert - drill completes
        page.assertCompletionViewDisplayed(expectedTotal = 2)
    }

    // TC-005 (P1): Correct and incorrect choices on API < 26 skip Vibrator
    @Test
    @Config(sdk = [24])
    fun tc005_apiBelow26_skipsVibratorOnIncorrectAndCorrectChoices() {
        // Arrange
        val lesson = buildLesson()
        val item1 = buildVocabItem(
            id = "qa_vd_1",
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]"""
        )
        val item2 = buildVocabItem(
            id = "qa_vd_2",
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            distractorsJson = """["нет"]"""
        )
        val page = launchVocabDrill(lesson, listOf(item1, item2), isLatin = false)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))

        val firstTarget = page.currentTargetWord()
        val (firstItem, secondItem) = if (firstTarget == item1.targetWordLatin) item1 to item2 else item2 to item1
        val firstDistractor = if (firstItem.id == item1.id) "пока" else "нет"

        // Act - tap incorrect on first item, then correct on second item
        composeRule.mainClock.autoAdvance = false
        page.tapOption(firstDistractor)
            .advanceFrame()

        // Assert - no vibration on API 24
        assertFalse(vibratorShadow.isVibrating)
        assertEquals(0L, vibratorShadow.milliseconds)

        page.advanceDelay(850L)
            .tapOption(secondItem.sourceTranslation)
            .advanceFrame()

        assertFalse(vibratorShadow.isVibrating)
        assertEquals(0L, vibratorShadow.milliseconds)

        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            runBlocking(Dispatchers.IO) {
                db.lessonDao().getLessonById(lesson.id)?.totalAnswersInLast100 == 2
            }
        }
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(1, dbLesson.correctAnswersInLast100)
    }

    // TC-006 (P0): Option taps ignored while selectedOption != null (T9, T10)
    @Test
    fun tc006_optionTapsIgnored_whileSelectedOptionNonNull() {
        // Arrange
        val lesson = buildLesson()
        val item1 = buildVocabItem(
            id = "qa_vd_1",
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]"""
        )
        val item2 = buildVocabItem(
            id = "qa_vd_2",
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            distractorsJson = """["нет"]"""
        )
        val page = launchVocabDrill(lesson, listOf(item1, item2), isLatin = false)
        val firstTarget = page.currentTargetWord()
        val firstItem = if (firstTarget == item1.targetWordLatin) item1 else item2
        val firstDistractor = if (firstItem.id == item1.id) "пока" else "нет"

        // Act - tap correct option, then rapidly tap distractor & correct option again before 500ms elapses
        composeRule.mainClock.autoAdvance = false
        page.tapOption(firstItem.sourceTranslation)
            .advanceFrame()
            .tapOption(firstDistractor)
            .advanceFrame()
            .tapOption(firstItem.sourceTranslation)
            .advanceFrame()

        // Assert - progress incremented only once, DB recorded only once
        page.assertProgressDisplayed("50.0% (1 / 2)")
        composeRule.waitUntil(timeoutMillis = 5_000) {
            ShadowLooper.idleMainLooper()
            runBlocking(Dispatchers.IO) {
                db.lessonDao().getLessonById(lesson.id)?.totalAnswersInLast100 == 1
            }
        }
        val dbLesson = runBlocking(Dispatchers.IO) { db.lessonDao().getLessonById(lesson.id)!! }
        assertEquals(1, dbLesson.correctAnswersInLast100)
        assertEquals(1, dbLesson.totalAnswersInLast100)
    }

    // TC-007 (P2): Replay target audio via vocab_pronounce_button with Latin script
    @Test
    fun tc007_pronounceButton_replaysLatinTargetWordAudio() {
        // Arrange
        val lesson = buildLesson()
        val item1 = buildVocabItem(
            id = "qa_vd_latin",
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]""",
            audioUri = ""
        )
        val page = launchVocabDrill(lesson, listOf(item1), isLatin = true)
        val ttsShadow = Shadows.shadowOf(ShadowTextToSpeech.getLastTextToSpeechInstance())

        // Assert initial Latin display & auto-audio
        page.assertTargetWordDisplayed("zdravo")
        assertEquals("zdravo", ttsShadow.lastSpokenText)

        // Act - tap speaker button
        page.tapPronounceButton()
        composeRule.waitForIdle()

        // Assert - still in AwaitingAnswer and TTS re-invoked with Latin word
        page.assertTargetWordDisplayed("zdravo")
        assertEquals("zdravo", ttsShadow.lastSpokenText)
    }

    // TC-008 (P2): Restart drill from Completed state on non-empty vs empty lesson (T8, T11)
    @Test
    fun tc008_restartDrill_resetsNonEmptyLessonAndKeepsEmptyLessonCompleted() {
        // Arrange (a): 2-item lesson completed
        val lesson = buildLesson(id = "qa_vd_restart_1")
        val item1 = buildVocabItem(
            id = "qa_vd_r1",
            lessonId = lesson.id,
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            distractorsJson = """["пока"]"""
        )
        val item2 = buildVocabItem(
            id = "qa_vd_r2",
            lessonId = lesson.id,
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            distractorsJson = """["нет"]"""
        )
        val page = launchVocabDrill(lesson, listOf(item1, item2), isLatin = false)
        composeRule.mainClock.autoAdvance = false
        repeat(2) {
            val target = page.currentTargetWord()
            val answer = if (target == item1.targetWordLatin) item1.sourceTranslation else item2.sourceTranslation
            page.tapOption(answer).advanceFrame().advanceDelay(550L)
        }
        page.assertCompletionViewDisplayed(expectedTotal = 2)

        // Act (a) - tap restart_drill_button
        page.tapRestartButton()
            .advanceFrame()

        // Assert (a) - back in AwaitingAnswer with 0.0% (0 / 2)
        page.assertProgressDisplayed("0.0% (0 / 2)")

        // Arrange (b): empty lesson in Completed state
        val emptyLesson = buildLesson(id = "qa_vd_restart_empty", orderIndex = 101)
        runBlocking(Dispatchers.IO) { db.clearAllTables() }
        composeRule.mainClock.autoAdvance = true
        val emptyPage = launchVocabDrill(emptyLesson, emptyList())
        emptyPage.assertCompletionViewDisplayed(expectedTotal = 0)

        // Act (b) - tap restart_drill_button on empty lesson
        emptyPage.tapRestartButton()
        composeRule.waitForIdle()

        // Assert (b) - remains in Completed state
        emptyPage.assertCompletionViewDisplayed(expectedTotal = 0)
    }

    // TC-009 (P2): Zero distractors ("[]") renders 1 option card with odd-row Spacer
    @Test
    fun tc009_zeroDistractors_rendersSingleOptionCard() {
        // Arrange
        val lesson = buildLesson()
        val item = buildVocabItem(
            sourceTranslation = "привет",
            distractorsJson = "[]"
        )

        // Act
        val page = launchVocabDrill(lesson, listOf(item))

        // Assert
        page.assertOptionCount(1)
            .assertOptionTagCount("привет", 1)
    }

    // TC-010 (P2): 4 and 5 distractors render 5 options (odd + Spacer) and 6 options (even, 3 full rows)
    @Test
    fun tc010_fourAndFiveDistractors_renderFiveAndSixOptions() {
        // Arrange Case A: 4 distractors -> 5 options
        val lessonA = buildLesson(id = "qa_vd_10a")
        val itemA = buildVocabItem(
            id = "qa_vd_item_10a",
            lessonId = lessonA.id,
            sourceTranslation = "привет",
            distractorsJson = """["d1","d2","d3","d4"]"""
        )

        // Act & Assert Case A
        val pageA = launchVocabDrill(lessonA, listOf(itemA))
        pageA.assertOptionCount(5)

        // Arrange Case B: 5 distractors -> 6 options
        val lessonB = buildLesson(id = "qa_vd_10b", orderIndex = 101)
        val itemB = buildVocabItem(
            id = "qa_vd_item_10b",
            lessonId = lessonB.id,
            sourceTranslation = "привет",
            distractorsJson = """["d1","d2","d3","d4","d5"]"""
        )
        val pageB = launchVocabDrill(lessonB, listOf(itemB))

        // Assert Case B
        pageB.assertOptionCount(6)
    }

    // TC-011 (P2): 6 distractors (>5) truncated to 5 distractors (6 options max)
    @Test
    fun tc011_sixDistractors_truncatedToFiveDistractorsAndSixOptionsMax() {
        // Arrange
        val lesson = buildLesson()
        val item = buildVocabItem(
            sourceTranslation = "привет",
            distractorsJson = """["d1","d2","d3","d4","d5","d6"]"""
        )

        // Act
        val page = launchVocabDrill(lesson, listOf(item))

        // Assert
        page.assertOptionCount(6)
            .assertOptionAbsent("d6")
    }

    // TC-012 (P2): Malformed distractorsJson falls back to emptyList() without crash
    @Test
    fun tc012_malformedDistractorsJson_fallsBackToSingleOptionWithoutCrash() {
        // Arrange & Act - "{bad"
        val lesson1 = buildLesson(id = "qa_vd_bad1")
        val item1 = buildVocabItem(
            id = "qa_vd_bad_item_1",
            lessonId = lesson1.id,
            sourceTranslation = "привет",
            distractorsJson = "{bad"
        )
        val page = launchVocabDrill(lesson1, listOf(item1))

        // Assert - "{bad"
        page.assertOptionCount(1)
            .assertOptionTagCount("привет", 1)

        // Arrange & Act - "null"
        val lesson2 = buildLesson(id = "qa_vd_bad2", orderIndex = 101)
        val item2 = buildVocabItem(
            id = "qa_vd_bad_item_2",
            lessonId = lesson2.id,
            sourceTranslation = "спасибо",
            distractorsJson = "null"
        )
        val page2 = launchVocabDrill(lesson2, listOf(item2))

        // Assert - "null"
        page2.assertOptionCount(1)
            .assertOptionTagCount("спасибо", 1)
    }

    // TC-013 (P2): Duplicate distractor matching sourceTranslation rendered without .distinct()
    @Test
    fun tc013_duplicateDistractor_renderedWithoutDistinct() {
        // Arrange
        val lesson = buildLesson()
        val item = buildVocabItem(
            sourceTranslation = "привет",
            distractorsJson = """["привет","пока"]"""
        )

        // Act
        val page = launchVocabDrill(lesson, listOf(item))

        // Assert
        page.assertOptionCount(3)
            .assertOptionTagCount("привет", 2)
            .assertOptionTagCount("пока", 1)
    }

    // TC-014 (P2): Optional metadata (partOfSpeech, exampleSentence) blank vs non-blank with multi-byte Cyrillic
    @Test
    fun tc014_optionalMetadata_blankHiddenAndNonBlankCyrillicRendered() {
        // Arrange - Item A (blank/whitespace) & Item B (non-blank Cyrillic)
        val lesson = buildLesson()
        val itemA = buildVocabItem(
            id = "qa_vd_meta_a",
            targetWord = "књига",
            targetWordLatin = "knjiga",
            sourceTranslation = "книга",
            partOfSpeech = "   ",
            exampleSentence = "",
            distractorsJson = """["стол"]"""
        )
        val itemB = buildVocabItem(
            id = "qa_vd_meta_b",
            targetWord = "ђак",
            targetWordLatin = "đak",
            sourceTranslation = "ученик",
            partOfSpeech = "сущ.",
            exampleSentence = "Ђак чита књигу.",
            distractorsJson = """["учитель"]"""
        )
        val page = launchVocabDrill(lesson, listOf(itemA, itemB))
        composeRule.mainClock.autoAdvance = false

        // Act & Assert both items in shuffled order
        repeat(2) {
            val current = page.currentTargetWord()
            if (current == itemA.targetWordLatin) {
                page.assertTextAbsent("сущ.")
                    .assertTextAbsent("„Ђак чита књигу.“")
                    .tapOption(itemA.sourceTranslation)
            } else {
                page.assertTextDisplayed("сущ.")
                    .assertTextDisplayed("„Ђак чита књигу.“")
                    .tapOption(itemB.sourceTranslation)
            }
            page.advanceFrame().advanceDelay(550L)
        }
        page.assertCompletionViewDisplayed(expectedTotal = 2)
    }

    // TC-015 (P2): Progress coerceIn(0f, 1f) clamps when correctCount > initialTotal
    @Test
    fun tc015_progressClamped_whenCorrectCountExceedsInitialTotal() {
        // Arrange & Act
        composeRule.setContent {
            LanguageLearnerTheme {
                VocabDrillProgressBar(correctCount = 3, initialTotal = 2)
            }
        }
        composeRule.waitForIdle()

        // Assert
        VocabDrillPage(composeRule).assertProgressDisplayed("100.0% (3 / 2)")
    }

    // TC-016 (P1): CurriculumRepository.recordAnswer mastery threshold (89, 90, 91) and 100-row pruning
    @Test
    fun tc016_recordAnswer_masteryThresholdAnd100RowPruning() = runBlocking(Dispatchers.IO) {
        // Arrange - L1 (orderIndex=0) and L2 (orderIndex=1, locked)
        val l1 = buildLesson(id = "qa_vd_l1", orderIndex = 0, isCompleted = false, isUnlocked = true)
        val l2 = buildLesson(id = "qa_vd_l2", orderIndex = 1, isCompleted = false, isUnlocked = false)
        db.lessonDao().insertLessons(listOf(l1, l2))

        val baseTime = System.currentTimeMillis() - 10_000L
        // Seed 88 correct + 11 incorrect = 99 answers
        repeat(88) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = l1.id, isCorrect = true, timestamp = baseTime + idx)
            )
        }
        repeat(11) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = l1.id, isCorrect = false, timestamp = baseTime + 88 + idx)
            )
        }

        // Act - 100th answer is correct -> 89/100 (score = 4.45f)
        val score89 = repository.recordAnswer(l1.id, isCorrect = true)

        // Assert - 89/100 does not complete L1 or unlock L2
        assertEquals(4.45f, score89, 0.001f)
        assertFalse(db.lessonDao().getLessonById(l1.id)!!.isCompleted)
        assertFalse(db.lessonDao().getLessonById(l2.id)!!.isUnlocked)
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount(l1.id))

        // Act - 101st answer is correct (prunes oldest correct? Wait: oldest is correct at baseTime+0!)
        // Replace oldest incorrect so window becomes 90/100: clear and seed 89 correct + 10 incorrect (oldest = incorrect)
        db.lessonAnswerDao().clearAnswersForLesson(l1.id)
        repeat(10) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = l1.id, isCorrect = false, timestamp = baseTime + idx)
            )
        }
        repeat(90) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = l1.id, isCorrect = true, timestamp = baseTime + 10 + idx)
            )
        }
        // Now 100 rows (10 oldest incorrect, 90 newest correct). Adding 1 correct prunes 1st incorrect -> 91?
        // First verify 90/100: seed 11 oldest incorrect + 89 newest correct, then record 1 correct -> 90/100!
        db.lessonAnswerDao().clearAnswersForLesson(l1.id)
        repeat(11) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = l1.id, isCorrect = false, timestamp = baseTime + idx)
            )
        }
        repeat(89) { idx ->
            db.lessonAnswerDao().insertAnswer(
                LessonAnswerEntity(lessonId = l1.id, isCorrect = true, timestamp = baseTime + 11 + idx)
            )
        }
        val score90 = repository.recordAnswer(l1.id, isCorrect = true)

        // Assert - 90/100 completes L1 and unlocks L2
        assertEquals(4.50f, score90, 0.001f)
        assertTrue(db.lessonDao().getLessonById(l1.id)!!.isCompleted)
        assertTrue(db.lessonDao().getLessonById(l2.id)!!.isUnlocked)
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount(l1.id))

        // Act - 102nd answer is correct -> prunes another oldest incorrect -> 91/100
        val score91 = repository.recordAnswer(l1.id, isCorrect = true)

        // Assert - 91/100 keeps L2 unlocked and table pruned to 100 rows
        assertEquals(4.55f, score91, 0.001f)
        assertTrue(db.lessonDao().getLessonById(l1.id)!!.isCompleted)
        assertTrue(db.lessonDao().getLessonById(l2.id)!!.isUnlocked)
        assertEquals(100, db.lessonAnswerDao().getTotalAnswersCount(l1.id))
    }

    // TC-017 (P2): Pairwise Configs 1 & 2 (distractorCount=0)
    @Test
    fun tc017_pairwiseConfigs1And2_zeroDistractors() {
        // Arrange Config 1: (0, Latin=true, API>=26, meta=present)
        val lesson1 = buildLesson(id = "qa_vd_pw1")
        val item1 = buildVocabItem(
            id = "qa_vd_pw1_item",
            lessonId = lesson1.id,
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            partOfSpeech = "межд.",
            exampleSentence = "Здраво, пријатељу!",
            distractorsJson = "[]"
        )
        val page = launchVocabDrill(lesson1, listOf(item1), isLatin = true)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))

        // Assert Config 1
        page.assertTargetWordDisplayed("zdravo")
            .assertTextDisplayed("межд.")
            .assertTextDisplayed("„Здраво, пријатељу!“")
            .assertOptionCount(1)
        composeRule.mainClock.autoAdvance = false
        page.tapOption("привет").advanceFrame()
        assertEquals(40L, vibratorShadow.milliseconds)
        page.advanceDelay(550L)

        // Arrange Config 2: (0, Latin=false, API<26 behavior verified in tc005, meta=blank)
        composeRule.mainClock.autoAdvance = true
        val lesson2 = buildLesson(id = "qa_vd_pw2", orderIndex = 101)
        val item2 = buildVocabItem(
            id = "qa_vd_pw2_item",
            lessonId = lesson2.id,
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            partOfSpeech = "",
            exampleSentence = "",
            distractorsJson = "[]"
        )
        val page2 = launchVocabDrill(lesson2, listOf(item2), isLatin = false)

        // Assert Config 2
        page2.assertTargetWordDisplayed("hvala")
            .assertTextAbsent("межд.")
            .assertOptionCount(1)
    }

    // TC-018 (P2): Pairwise Configs 3 & 4 (distractorCount=3)
    @Test
    fun tc018_pairwiseConfigs3And4_threeDistractors() {
        // Arrange Config 3: (3, Latin=true, meta=blank)
        val lesson3 = buildLesson(id = "qa_vd_pw3")
        val item3 = buildVocabItem(
            id = "qa_vd_pw3_item",
            lessonId = lesson3.id,
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            partOfSpeech = "",
            exampleSentence = "",
            distractorsJson = """["d1","d2","d3"]"""
        )
        val page = launchVocabDrill(lesson3, listOf(item3), isLatin = true)

        // Assert Config 3
        page.assertTargetWordDisplayed("zdravo")
            .assertTextAbsent("сущ.")
            .assertOptionCount(4)

        // Arrange Config 4: (3, Latin=false, API>=26, meta=present)
        val lesson4 = buildLesson(id = "qa_vd_pw4", orderIndex = 101)
        val item4 = buildVocabItem(
            id = "qa_vd_pw4_item",
            lessonId = lesson4.id,
            targetWord = "књига",
            targetWordLatin = "knjiga",
            sourceTranslation = "книга",
            partOfSpeech = "сущ.",
            exampleSentence = "Ово је књига.",
            distractorsJson = """["d1","d2","d3"]"""
        )
        val page4 = launchVocabDrill(lesson4, listOf(item4), isLatin = false)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))

        // Assert Config 4
        page4.assertTargetWordDisplayed("knjiga")
            .assertTextDisplayed("сущ.")
            .assertTextDisplayed("„Ово је књига.“")
            .assertOptionCount(4)
        composeRule.mainClock.autoAdvance = false
        page4.tapOption("книга").advanceFrame()
        assertEquals(40L, vibratorShadow.milliseconds)
    }

    // TC-019 (P2): Pairwise Configs 5 & 6 (distractorCount=6)
    @Test
    fun tc019_pairwiseConfigs5And6_sixDistractorsTruncated() {
        // Arrange Config 5: (6, Latin=true, API>=26, meta=blank)
        val lesson5 = buildLesson(id = "qa_vd_pw5")
        val item5 = buildVocabItem(
            id = "qa_vd_pw5_item",
            lessonId = lesson5.id,
            targetWord = "здраво",
            targetWordLatin = "zdravo",
            sourceTranslation = "привет",
            partOfSpeech = "",
            exampleSentence = "",
            distractorsJson = """["d1","d2","d3","d4","d5","d6"]"""
        )
        val page = launchVocabDrill(lesson5, listOf(item5), isLatin = true)
        val vibratorShadow = Shadows.shadowOf(app.getSystemService(Vibrator::class.java))

        // Assert Config 5
        page.assertTargetWordDisplayed("zdravo")
            .assertOptionCount(6)
            .assertOptionAbsent("d6")
        composeRule.mainClock.autoAdvance = false
        page.tapOption("привет").advanceFrame()
        assertEquals(40L, vibratorShadow.milliseconds)
        page.advanceDelay(550L)

        // Arrange Config 6: (6, Latin=false, meta=present)
        composeRule.mainClock.autoAdvance = true
        val lesson6 = buildLesson(id = "qa_vd_pw6", orderIndex = 101)
        val item6 = buildVocabItem(
            id = "qa_vd_pw6_item",
            lessonId = lesson6.id,
            targetWord = "хвала",
            targetWordLatin = "hvala",
            sourceTranslation = "спасибо",
            partOfSpeech = "частица",
            exampleSentence = "Хвала лепо!",
            distractorsJson = """["d1","d2","d3","d4","d5","d6"]"""
        )
        val page6 = launchVocabDrill(lesson6, listOf(item6), isLatin = false)

        // Assert Config 6
        page6.assertTargetWordDisplayed("hvala")
            .assertTextDisplayed("частица")
            .assertTextDisplayed("„Хвала лепо!“")
            .assertOptionCount(6)
            .assertOptionAbsent("d6")
    }
}
