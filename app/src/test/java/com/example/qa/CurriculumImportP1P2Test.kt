package com.example.qa

import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.importer.CurriculumImporter
import com.example.data.local.AppDatabase
import com.example.data.repository.CurriculumRepository
import com.example.ui.screens.ImportExportScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Automated P1 and P2 test suite for Curriculum Import & Export generated from
 * `qa-artifacts/2026-11-14-curriculum-import/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurriculumImportP1P2Test {

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

    private fun mountImportExportScreen(): ImportExportPage {
        composeRule.setContent {
            LanguageLearnerTheme {
                ImportExportScreen(
                    viewModel = viewModel,
                    onBack = {}
                )
            }
        }
        return ImportExportPage(composeRule).assertControlsDisplayed()
    }

    @Test
    fun `TC-001 import valid JSON with lessons array including vocab grammar and sentences via pasted text`() {
        // Arrange
        runBlocking { repository.ensureInitialDataLoaded() }
        val jsonPayload = CurriculumImporter.getSampleJsonTemplate()
        val page = mountImportExportScreen()

        // Act
        val result = CurriculumImporter.parseJson(jsonPayload)
        runBlocking { repository.importCurriculum(result) }
        page.selectFormat("JSON")
            .enterRawPayload(jsonPayload)
            .submitImport()

        // Assert
        assertEquals(1, result.lessons.size)
        assertEquals(2, result.vocabItems.size)
        assertEquals(1, result.grammarGuides.size)
        assertEquals(1, result.sentences.size)
        page.assertStatusTextDisplayed("Imported 1 lessons, 2 vocab words, and 1 sentences.")
        runBlocking {
            val lesson = db.lessonDao().getLessonById(result.lessons[0].id)
            assertNotNull(lesson)
            assertTrue(lesson!!.isUnlocked)
            assertEquals(2, db.vocabDao().getVocabForLessonSync(lesson.id).size)
            assertNotNull(db.grammarDao().getGrammarGuideForLessonSync(lesson.id))
            assertEquals(1, db.sentenceDao().getSentencesForLessonSync(lesson.id).size)
            val srsDue = db.srsDao().getDueItems(System.currentTimeMillis() + 60_000L).first()
            assertTrue(srsDue.any { it.lessonId == lesson.id })
        }
    }

    @Test
    fun `TC-003 import valid JSON with lessons array and empty vocab`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val lessonOnlyJson = """
            {
              "lessons": [
                {
                  "id": "lesson_no_vocab",
                  "orderIndex": 10,
                  "title": "Grammar Only",
                  "subtitle": "Sub",
                  "grammarFocus": "Cases",
                  "isUnlocked": false
                }
              ]
            }
        """.trimIndent()

        // Act
        val result = CurriculumImporter.parseJson(lessonOnlyJson)
        repository.importCurriculum(result)

        // Assert
        assertEquals(1, result.lessons.size)
        assertTrue(result.vocabItems.isEmpty())
        val saved = db.lessonDao().getLessonById("lesson_no_vocab")
        assertNotNull(saved)
        assertTrue(saved!!.isUnlocked)
        assertTrue(db.vocabDao().getVocabForLessonSync("lesson_no_vocab").isEmpty())
    }

    @Test
    fun `TC-005 import valid semicolon-delimited CSV without header row via pasted text`() {
        // Arrange
        runBlocking { repository.ensureInitialDataLoaded() }
        val csvPayload = "кућа;kuća;дом;Существительное;Моја кућа.;"
        val page = mountImportExportScreen()

        // Act
        val result = CurriculumImporter.parseCsv(csvPayload)
        runBlocking { repository.importCurriculum(result) }
        page.selectFormat("CSV")
            .enterRawPayload(csvPayload)
            .submitImport()

        // Assert
        assertEquals(1, result.vocabItems.size)
        assertEquals("кућа", result.vocabItems[0].targetWord)
        assertEquals("дом", result.vocabItems[0].sourceTranslation)
        page.assertStatusTextDisplayed("Imported CSV deck with 1 words.")
        runBlocking {
            val srsDue = db.srsDao().getDueItems(System.currentTimeMillis() + 60_000L).first()
            assertTrue(srsDue.any { it.prompt == "кућа" && it.answer == "дом" })
        }
    }

    @Test
    fun `TC-006 import valid XML curriculum with lesson vocab and sentence`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val xmlPayload = CurriculumImporter.getSampleXmlTemplate()

        // Act
        val result = CurriculumImporter.parseXml(xmlPayload)
        repository.importCurriculum(result)

        // Assert
        assertEquals(1, result.lessons.size)
        assertEquals(2, result.vocabItems.size)
        assertEquals(1, result.sentences.size)
        val lesson = db.lessonDao().getLessonById(result.lessons[0].id)
        assertNotNull(lesson)
        assertTrue(lesson!!.isUnlocked)
        assertEquals(2, db.vocabDao().getVocabForLessonSync(lesson.id).size)
        assertEquals(1, db.sentenceDao().getSentencesForLessonSync(lesson.id).size)
        val srsDue = db.srsDao().getDueItems(System.currentTimeMillis() + 60_000L).first()
        assertTrue(srsDue.any { it.lessonId == lesson.id })
    }

    @Test
    fun `TC-007 import XML with vocab and sentence outside lesson tag`() {
        // Arrange
        runBlocking { repository.ensureInitialDataLoaded() }
        val orphanXml = """
            <curriculum>
              <vocab target="тест" latin="test" translation="тест" />
              <sentence source="Тест" target="Тест" latin="Test" />
            </curriculum>
        """.trimIndent()
        val page = mountImportExportScreen()

        // Act
        val result = CurriculumImporter.parseXml(orphanXml)
        runBlocking { repository.importCurriculum(result) }
        page.selectFormat("XML")
            .enterRawPayload(orphanXml)
            .submitImport()

        // Assert
        assertTrue(result.lessons.isEmpty())
        assertTrue(result.vocabItems.isEmpty())
        assertTrue(result.sentences.isEmpty())
        page.assertStatusTextDisplayed("Imported XML with 0 lessons, 0 words, 0 sentences.")
    }

    @Test
    fun `TC-011 recover from failed import by submitting valid JSON`() {
        // Arrange
        runBlocking { repository.ensureInitialDataLoaded() }
        val page = mountImportExportScreen()
        page.selectFormat("JSON")
            .enterRawPayload("{ invalid")
            .submitImport()
            .assertStatusTextDisplayed("JSON parse error:")
        val validJson = CurriculumImporter.getSampleJsonTemplate()

        // Act
        val validResult = CurriculumImporter.parseJson(validJson)
        runBlocking { repository.importCurriculum(validResult) }
        page.enterRawPayload(validJson)
            .submitImport()

        // Assert
        page.assertStatusTextDisplayed("Imported 1 lessons, 2 vocab words, and 1 sentences.")
        runBlocking {
            val imported = db.lessonDao().getLessonById(validResult.lessons[0].id)
            assertNotNull(imported)
            assertTrue(imported!!.isUnlocked)
        }
    }

    @Test
    fun `TC-015 load and copy JSON sample template then submit`() {
        // Arrange
        val page = mountImportExportScreen()
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Act
        page.tapSampleTemplate("JSON")

        // Assert template loaded & copied
        page.assertInputContains("lessons")
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
        assertTrue(clipText.contains("lessons"))

        // Act submit loaded template
        page.submitImport()

        // Assert import succeeds
        page.assertStatusTextDisplayed("Imported 1 lessons, 2 vocab words, and 1 sentences.")
    }

    @Test
    fun `TC-016 load and copy CSV sample template`() {
        // Arrange
        val page = mountImportExportScreen()
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Act
        page.tapSampleTemplate("CSV")

        // Assert
        page.assertInputContains("targetWord")
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
        assertTrue(clipText.contains("targetWord"))
    }

    @Test
    fun `TC-017 load and copy XML sample template`() {
        // Arrange
        val page = mountImportExportScreen()
        val clipboard = app.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Act
        page.tapSampleTemplate("XML")

        // Assert
        page.assertInputContains("<curriculum>")
        val clipText = clipboard.primaryClip?.getItemAt(0)?.text?.toString().orEmpty()
        assertTrue(clipText.contains("<curriculum>"))
    }

    @Test
    fun `TC-018 import valid-but-empty JSON and XML without crashing`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val initialLessons = db.lessonDao().getAllLessons().first()

        // Act
        val emptyJsonResult = CurriculumImporter.parseJson("{}")
        val emptyXmlResult = CurriculumImporter.parseXml("<curriculum/>")
        repository.importCurriculum(emptyJsonResult)
        repository.importCurriculum(emptyXmlResult)

        // Assert
        assertTrue(emptyJsonResult.lessons.isEmpty())
        assertTrue(emptyJsonResult.vocabItems.isEmpty())
        assertTrue(emptyXmlResult.lessons.isEmpty())
        assertTrue(emptyXmlResult.vocabItems.isEmpty())
        assertEquals(initialLessons, db.lessonDao().getAllLessons().first())
    }

    @Test
    fun `TC-019 import multi-byte Unicode vocabulary without encoding corruption`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val unicodeCsv = """
            targetWord,targetWordLatin,sourceTranslation,partOfSpeech,exampleSentence,audioUri
            "café ☕","cafe","日本語テスト",Noun,"Café & 日本語!",
        """.trimIndent()

        // Act
        val result = CurriculumImporter.parseCsv(unicodeCsv)
        repository.importCurriculum(result)

        // Assert
        assertEquals(1, result.vocabItems.size)
        val lessonId = result.lessons[0].id
        val savedVocab = db.vocabDao().getVocabForLessonSync(lessonId)
        assertEquals(1, savedVocab.size)
        assertEquals("café ☕", savedVocab[0].targetWord)
        assertEquals("日本語テスト", savedVocab[0].sourceTranslation)
    }
}
