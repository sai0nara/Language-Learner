package com.example.qa

import android.app.Application
import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.ConjugationCell
import com.example.data.model.ConjugationMatrixData
import com.example.data.model.GrammarGuideEntity
import com.example.data.model.LessonEntity
import com.example.ui.screens.GrammarMatrixScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowTextToSpeech

/**
 * Automated P1 and P2 test suite for 3x3 Grammar Matrices & Guides
 * generated from `qa-artifacts/2026-11-14-grammar-matrices/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36], qualifiers = "w600dp-h1200dp")
class GrammarMatrixTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var app: Application
    private lateinit var db: AppDatabase
    private lateinit var viewModel: LanguageLearnerViewModel
    private val moshi = Moshi.Builder().build()
    private val matrixAdapter = moshi.adapter(ConjugationMatrixData::class.java)

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        db = AppDatabase.getInstance(app as Context)
        runBlocking(Dispatchers.IO) {
            db.clearAllTables()
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

    private fun buildNineCells(): List<ConjugationCell> = listOf(
        ConjugationCell(
            formType = "Affirmative",
            tense = "Past",
            formula = "Ја сам радио",
            exampleSerbian = "Ја сам јуче радио.",
            exampleSerbianLatin = "Ja sam juče radio.",
            exampleRussian = "Я вчера работал.",
            notes = "Перфекат с глаголом бити"
        ),
        ConjugationCell(
            formType = "Negative",
            tense = "Past",
            formula = "Ја нисам радио",
            exampleSerbian = "Ја нисам јуче радио.",
            exampleSerbianLatin = "Ja nisam juče radio.",
            exampleRussian = "Я вчера не работал.",
            notes = "Отрицание перфекта"
        ),
        ConjugationCell(
            formType = "Interrogative",
            tense = "Past",
            formula = "Да ли сам радио?",
            exampleSerbian = "Да ли сам јуче радио?",
            exampleSerbianLatin = "Da li sam juče radio?",
            exampleRussian = "Работал ли я вчера?",
            notes = "Вопрос в перфекте"
        ),
        ConjugationCell(
            formType = "Affirmative",
            tense = "Present",
            formula = "Ја радим",
            exampleSerbian = "Ја данас радим.",
            exampleSerbianLatin = "Ja danas radim.",
            exampleRussian = "Я сегодня работаю.",
            notes = "Презент"
        ),
        ConjugationCell(
            formType = "Negative",
            tense = "Present",
            formula = "Ја не радим",
            exampleSerbian = "Ја данас не радим.",
            exampleSerbianLatin = "Ja danas ne radim.",
            exampleRussian = "Я сегодня не работаю.",
            notes = "Отрицание презента"
        ),
        ConjugationCell(
            formType = "Interrogative",
            tense = "Present",
            formula = "Да ли радим?",
            exampleSerbian = "Да ли данас радим?",
            exampleSerbianLatin = "Da li danas radim?",
            exampleRussian = "Работаю ли я сегодня?",
            notes = "Вопрос в презенте"
        ),
        ConjugationCell(
            formType = "Affirmative",
            tense = "Future",
            formula = "Ја ћу радити",
            exampleSerbian = "Ја ћу сутра радити.",
            exampleSerbianLatin = "Ja ću sutra raditi.",
            exampleRussian = "Я завтра буду работать.",
            notes = "Футур I"
        ),
        ConjugationCell(
            formType = "Negative",
            tense = "Future",
            formula = "Ја нећу радити",
            exampleSerbian = "Ја сутра нећу радити.",
            exampleSerbianLatin = "Ja sutra neću raditi.",
            exampleRussian = "Я завтра не буду работать.",
            notes = "Отрицание футура"
        ),
        ConjugationCell(
            formType = "Interrogative",
            tense = "Future",
            formula = "Да ли ћу радити?",
            exampleSerbian = "Да ли ћу сутра радити?",
            exampleSerbianLatin = "Da li ću sutra raditi?",
            exampleRussian = "Буду ли я работать завтра?",
            notes = "Вопрос в футуре"
        )
    )

    private fun seedLessonAndGuide(
        lessonId: String = "lesson_gm_1",
        lessonTitle: String = "Урок 1: Глаголы",
        lessonSubtitle: String = "Основы спряжения",
        guideTitle: String = "Глагол радити",
        guideSummary: String = "Сводка спряжения",
        explanationMarkdown: String = "Правило образования форм глагола радити.",
        matrixJson: String = matrixAdapter.toJson(
            ConjugationMatrixData(
                verbInfinitive = "Радити",
                verbInfinitiveLatin = "Raditi",
                verbTranslation = "Работать",
                cells = buildNineCells()
            )
        ),
        insertGuide: Boolean = true,
        isLatinScript: Boolean = false
    ): LessonEntity {
        val lesson = LessonEntity(
            id = lessonId,
            orderIndex = 1,
            title = lessonTitle,
            subtitle = lessonSubtitle,
            grammarFocus = "Matrix",
            isUnlocked = true
        )
        runBlocking(Dispatchers.IO) {
            db.lessonDao().insertLessons(listOf(lesson))
            if (insertGuide) {
                db.grammarDao().insertGrammarGuide(
                    GrammarGuideEntity(
                        id = "guide_$lessonId",
                        lessonId = lessonId,
                        title = guideTitle,
                        summary = guideSummary,
                        explanationMarkdown = explanationMarkdown,
                        matrixJson = matrixJson
                    )
                )
            }
        }
        if (isLatinScript != viewModel.isLatinScript.value) {
            viewModel.toggleScript()
        }
        viewModel.selectLesson(lesson)
        return lesson
    }

    private fun launchScreen(lesson: LessonEntity, expectGuide: Boolean = true): GrammarMatrixPage {
        composeRule.setContent {
            LanguageLearnerTheme {
                GrammarMatrixScreen(
                    lesson = lesson,
                    viewModel = viewModel,
                    onBack = {}
                )
            }
        }
        if (expectGuide) {
            composeRule.waitUntil(timeoutMillis = 5_000) {
                viewModel.activeGrammar.value?.lessonId == lesson.id
            }
        }
        composeRule.waitForIdle()
        return GrammarMatrixPage(composeRule)
    }

    private fun assertLastSpokenText(expected: String) {
        val tts = ShadowTextToSpeech.getLastTextToSpeechInstance()
        val shadowTts = Shadows.shadowOf(tts)
        assertEquals(expected, shadowTts.lastSpokenText)
    }

    @Test
    fun `TC-001 Load grammar guide, select Affirmative Past cell in Cyrillic, and trigger audio playback`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = false)
        val page = launchScreen(lesson)

        // Act
        page.assertTextDisplayed("Глагол радити")
            .assertExplanationDisplayed("Правило образования форм глагола радити.")
            .assertFallbackMatrixAbsent()
            .tapCellByFormula("Ја сам радио")
            .tapAudioButton()

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Формула: Ја сам радио")
            .assertTextDisplayed("Ја сам јуче радио.")
            .assertTextDisplayed("Я вчера работал.")
            .assertTextDisplayed("Примечание: Перфекат с глаголом бити")
        assertLastSpokenText("Ја сам јуче радио.")
    }

    @Test
    fun `TC-002 Switch from selected cell to Negative Present cell and re-trigger audio without UI lock`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = false)
        val page = launchScreen(lesson)
        page.tapCellByFormula("Ја сам радио")
            .tapAudioButton()

        // Act
        page.tapCellByFormula("Ја не радим")
            .tapAudioButton()

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Формула: Ја не радим")
            .assertTextDisplayed("Ја данас не радим.")
            .assertTextDisplayed("Я сегодня не работаю.")
        assertLastSpokenText("Ја данас не радим.")
    }

    @Test
    fun `TC-003 Render and play Latin example when isLatinScript is true and exampleSerbianLatin is populated`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = true)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Ја радим")
            .tapAudioButton()

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Ja danas radim.")
            .assertTextDisplayed("Я сегодня работаю.")
        assertLastSpokenText("Ja danas radim.")
    }

    @Test
    fun `TC-004 Fallback to Cyrillic exampleSerbian when isLatinScript is true and exampleSerbianLatin is blank`() {
        // Arrange
        val cellsWithBlankLatin = buildNineCells().map { cell ->
            if (cell.formType == "Affirmative" && cell.tense == "Present") {
                cell.copy(exampleSerbianLatin = "")
            } else {
                cell
            }
        }
        val lesson = seedLessonAndGuide(
            matrixJson = matrixAdapter.toJson(
                ConjugationMatrixData("Радити", "Raditi", "Работать", cells = cellsWithBlankLatin)
            ),
            isLatinScript = true
        )
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Ја радим")
            .tapAudioButton()

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Ја данас радим.")
        assertLastSpokenText("Ја данас радим.")
    }

    @Test
    fun `TC-005 Fallback to lesson title subtitle and FallbackMatrixTable when grammarGuide is null`() {
        // Arrange
        val lesson = seedLessonAndGuide(
            lessonTitle = "Урок без гида",
            lessonSubtitle = "Подзаголовок урока",
            insertGuide = false
        )

        // Act
        val page = launchScreen(lesson, expectGuide = false)

        // Assert
        page.assertTextDisplayed("Урок без гида")
            .assertTextDisplayed("Подзаголовок урока")
            .assertExplanationAbsent()
            .assertFallbackMatrixDisplayed()
    }

    @Test
    fun `TC-006 Catch Moshi parse exception on malformed matrixJson and render FallbackMatrixTable`() {
        // Arrange
        val lesson = seedLessonAndGuide(matrixJson = "{malformed_json")

        // Act
        val page = launchScreen(lesson)

        // Assert
        page.assertTextDisplayed("Глагол радити")
            .assertExplanationDisplayed("Правило образования форм глагола радити.")
            .assertFallbackMatrixDisplayed()
    }

    @Test
    fun `TC-007a Render FallbackMatrixTable when matrixJson is empty object`() {
        // Arrange
        val lessonEmptyObj = seedLessonAndGuide(lessonId = "lesson_empty_obj", matrixJson = "{}")

        // Act
        val page = launchScreen(lessonEmptyObj)

        // Assert
        page.assertFallbackMatrixDisplayed()
            .assertDetailCardAbsent()
    }

    @Test
    fun `TC-007b Render FallbackMatrixTable when matrixJson has empty cells list`() {
        // Arrange
        val emptyCellsJson = matrixAdapter.toJson(
            ConjugationMatrixData("Радити", "Raditi", "Работать", cells = emptyList())
        )
        val lessonEmptyList = seedLessonAndGuide(lessonId = "lesson_empty_cells", matrixJson = emptyCellsJson)

        // Act
        val page = launchScreen(lessonEmptyList)

        // Assert
        page.assertFallbackMatrixDisplayed()
            .assertDetailCardAbsent()
    }

    @Test
    fun `TC-008 Omit explanation card when explanationMarkdown is blank and matrixJson is valid`() {
        // Arrange
        val lesson = seedLessonAndGuide(explanationMarkdown = "   ")

        // Act
        val page = launchScreen(lesson)

        // Assert
        page.assertTextDisplayed("Глагол радити")
            .assertExplanationAbsent()
            .assertFallbackMatrixAbsent()
            .assertTextDisplayed("Ја сам радио")
    }

    @Test
    fun `TC-009 Omit explanation card and render FallbackMatrixTable when explanationMarkdown is blank and matrixJson is invalid`() {
        // Arrange
        val lesson = seedLessonAndGuide(explanationMarkdown = "", matrixJson = "{}")

        // Act
        val page = launchScreen(lesson)

        // Assert
        page.assertTextDisplayed("Глагол радити")
            .assertExplanationAbsent()
            .assertFallbackMatrixDisplayed()
    }

    @Test
    fun `TC-010 Missing form tense cell displays dash and tapping it is a no-op`() {
        // Arrange: omit all Future cells so both primary and tense-fallback return null for Future row
        val withoutFuture = buildNineCells().filter { it.tense != "Future" }
        val lesson = seedLessonAndGuide(
            matrixJson = matrixAdapter.toJson(
                ConjugationMatrixData("Радити", "Raditi", "Работать", cells = withoutFuture)
            )
        )
        val page = launchScreen(lesson)

        // Act
        page.tapMissingDashCell(index = 0)

        // Assert
        page.assertDetailCardAbsent()
    }

    @Test
    fun `TC-011 Initial state NoCellSelected hides selected_matrix_cell_card and audio button`() {
        // Arrange
        val lesson = seedLessonAndGuide()

        // Act
        val page = launchScreen(lesson)

        // Assert
        page.assertDetailCardAbsent()
            .assertAudioButtonAbsent()
    }

    @Test
    fun `TC-012 Omit notes line in detail card when cell notes is blank`() {
        // Arrange
        val cellsWithBlankNotes = buildNineCells().map { cell ->
            if (cell.formType == "Affirmative" && cell.tense == "Past") cell.copy(notes = "") else cell
        }
        val lesson = seedLessonAndGuide(
            matrixJson = matrixAdapter.toJson(
                ConjugationMatrixData("Радити", "Raditi", "Работать", cells = cellsWithBlankNotes)
            )
        )
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Ја сам радио")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Формула: Ја сам радио")
            .assertTextDisplayed("Ја сам јуче радио.")
            .assertTextDisplayed("Я вчера работал.")
            .assertNotesAbsent()
    }

    @Test
    fun `TC-013 Pairwise config 3 Affirmative Future Cyrillic`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = false)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Ја ћу радити")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Ја ћу сутра радити.")
            .assertTextDisplayed("Я завтра буду работать.")
    }

    @Test
    fun `TC-014 Pairwise config 4 Negative Past Latin`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = true)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Ја нисам радио")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Ja nisam juče radio.")
            .assertTextDisplayed("Я вчера не работал.")
    }

    @Test
    fun `TC-015 Pairwise config 6 Negative Future Latin`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = true)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Ја нећу радити")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Ja sutra neću raditi.")
            .assertTextDisplayed("Я завтра не буду работать.")
    }

    @Test
    fun `TC-016 Pairwise config 7 Interrogative Past Cyrillic`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = false)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Да ли сам радио?")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Да ли сам јуче радио?")
            .assertTextDisplayed("Работал ли я вчера?")
    }

    @Test
    fun `TC-017 Pairwise config 8 Interrogative Present Latin`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = true)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Да ли радим?")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Da li danas radim?")
            .assertTextDisplayed("Работаю ли я сегодня?")
    }

    @Test
    fun `TC-018 Pairwise config 9 Interrogative Future Cyrillic`() {
        // Arrange
        val lesson = seedLessonAndGuide(isLatinScript = false)
        val page = launchScreen(lesson)

        // Act
        page.tapCellByFormula("Да ли ћу радити?")

        // Assert
        page.assertDetailCardDisplayed()
            .assertTextDisplayed("Да ли ћу сутра радити?")
            .assertTextDisplayed("Буду ли я работать завтра?")
    }
}
