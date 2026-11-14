package com.example.qa

import android.app.Application
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.importer.CurriculumImporter
import com.example.data.local.AppDatabase
import com.example.data.model.LessonEntity
import com.example.data.repository.CurriculumRepository
import com.example.ui.screens.ImportExportScreen
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
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
 * Automated P0 test suite for Curriculum Import & Export generated from
 * `qa-artifacts/2026-11-14-curriculum-import/test-suite.md` following AAA.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurriculumImportP0Test {

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
    fun `TC-002 import valid JSON with standalone vocab array via stream and persist SRS`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val standaloneVocabJson = """
            {
              "vocab": [
                {
                  "targetWord": "књига",
                  "targetWordLatin": "knjiga",
                  "sourceTranslation": "книга",
                  "partOfSpeech": "Существительное"
                }
              ]
            }
        """.trimIndent()

        // Act
        val result = CurriculumImporter.parseJson(standaloneVocabJson)
        repository.importCurriculum(result)

        // Assert
        assertEquals(1, result.lessons.size)
        assertEquals(1, result.vocabItems.size)
        assertTrue(result.message.isNotBlank())
        val lessons = db.lessonDao().getAllLessons().first()
        val importedLesson = lessons.first { it.title == "Imported Vocabulary" }
        assertTrue(importedLesson.isUnlocked)
        val vocab = db.vocabDao().getVocabForLessonSync(importedLesson.id)
        assertEquals(listOf("књига"), vocab.map { it.targetWord })
        val srsDue = db.srsDao().getDueItems(System.currentTimeMillis() + 60_000L).first()
        assertTrue(srsDue.any { it.prompt == "књига" && it.answer == "книга" })
    }

    @Test
    fun `TC-004 import valid comma-delimited CSV with header and quoted embedded commas`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val csvPayload = """
            targetWord,targetWordLatin,sourceTranslation,partOfSpeech,exampleSentence,audioUri
            "добар, дан","dobar, dan","добрый день",Фраза,"Добар, дан!",
        """.trimIndent()

        // Act
        val result = CurriculumImporter.parseCsv(csvPayload)
        repository.importCurriculum(result)

        // Assert
        assertEquals(1, result.lessons.size)
        assertEquals(1, result.vocabItems.size)
        assertEquals("добар, дан", result.vocabItems[0].targetWord)
        assertEquals("добрый день", result.vocabItems[0].sourceTranslation)
        val importedLesson = db.lessonDao().getAllLessons().first().first { it.id == result.lessons[0].id }
        assertTrue(importedLesson.isUnlocked)
        val srsDue = db.srsDao().getDueItems(System.currentTimeMillis() + 60_000L).first()
        assertTrue(srsDue.any { it.prompt == "добар, дан" && it.answer == "добрый день" })
    }

    @Test
    fun `TC-008 reject malformed JSON syntax without corrupting existing DB`() {
        // Arrange
        val initialLessons = runBlocking {
            repository.ensureInitialDataLoaded()
            db.lessonDao().getAllLessons().first()
        }
        val malformedJson = "{ \"lessons\": ["
        val page = mountImportExportScreen()

        // Act
        val result = CurriculumImporter.parseJson(malformedJson)
        page.selectFormat("JSON")
            .enterRawPayload(malformedJson)
            .submitImport()

        // Assert
        assertTrue(result.lessons.isEmpty())
        assertTrue(result.vocabItems.isEmpty())
        assertTrue(result.message.startsWith("JSON parse error:"))
        page.assertStatusTextDisplayed("JSON parse error:")
        assertEquals(initialLessons, runBlocking { db.lessonDao().getAllLessons().first() })
    }

    @Test
    fun `TC-009 reject malformed XML syntax after prior successful import`() {
        // Arrange
        val lessonsAfterValidImport = runBlocking {
            repository.ensureInitialDataLoaded()
            val validImport = CurriculumImporter.parseJson(CurriculumImporter.getSampleJsonTemplate())
            repository.importCurriculum(validImport)
            db.lessonDao().getAllLessons().first()
        }
        val malformedXml = "<curriculum><lesson></curriculum>"
        val page = mountImportExportScreen()

        // Act
        val result = CurriculumImporter.parseXml(malformedXml)
        page.selectFormat("XML")
            .enterRawPayload(malformedXml)
            .submitImport()

        // Assert
        assertTrue(result.lessons.isEmpty())
        assertTrue(result.vocabItems.isEmpty())
        assertTrue(result.message.startsWith("XML parse error:"))
        page.assertStatusTextDisplayed("XML parse error:")
        assertEquals(lessonsAfterValidImport, runBlocking { db.lessonDao().getAllLessons().first() })
    }

    @Test
    fun `TC-010 reject blank or whitespace-only CSV`() {
        // Arrange
        val initialLessons = runBlocking {
            repository.ensureInitialDataLoaded()
            db.lessonDao().getAllLessons().first()
        }
        val blankCsv = "   \n\t  "
        val page = mountImportExportScreen()

        // Act
        val result = CurriculumImporter.parseCsv(blankCsv)
        page.selectFormat("CSV")
            .enterRawPayload(blankCsv)

        // Assert
        assertTrue(result.lessons.isEmpty())
        assertEquals("CSV file is empty", result.message)
        page.assertSubmitDisabled()
        assertEquals(initialLessons, runBlocking { db.lessonDao().getAllLessons().first() })
    }

    @Test
    fun `TC-012 reset to default curriculum from existing state`() = runTest {
        // Arrange
        db.lessonDao().insertLesson(
            LessonEntity("custom_only", 1, "Custom Only", "", "Focus", isUnlocked = true)
        )
        assertEquals(1, db.lessonDao().getAllLessons().first().size)

        // Act
        repository.clearAndResetToDefaults()

        // Assert
        val lessons = db.lessonDao().getAllLessons().first()
        assertFalse(lessons.any { it.id == "custom_only" })
        assertTrue(lessons.isNotEmpty())
        assertTrue(lessons.first().isUnlocked)
    }

    @Test
    fun `TC-013 reset to default curriculum after successful import`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val imported = CurriculumImporter.parseJson(CurriculumImporter.getSampleJsonTemplate())
        repository.importCurriculum(imported)
        assertTrue(db.lessonDao().getAllLessons().first().any { it.id == "lesson_custom_1" })

        // Act
        repository.clearAndResetToDefaults()

        // Assert
        val lessons = db.lessonDao().getAllLessons().first()
        assertFalse(lessons.any { it.id == "lesson_custom_1" })
        assertTrue(lessons.isNotEmpty())
        assertTrue(lessons.first().isUnlocked)
    }

    @Test
    fun `TC-014 reset to default curriculum after failed import`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val failedResult = CurriculumImporter.parseJson("{ invalid")
        assertTrue(failedResult.lessons.isEmpty())

        // Act
        repository.clearAndResetToDefaults()

        // Assert
        val lessons = db.lessonDao().getAllLessons().first()
        assertTrue(lessons.isNotEmpty())
        assertTrue(lessons.first().isUnlocked)
    }

    @Test
    fun `TC-020 import SQL injection string literally and handle double-submit without DB corruption`() = runTest {
        // Arrange
        repository.ensureInitialDataLoaded()
        val sqliPayload = """
            {
              "vocab": [
                {
                  "targetWord": "'; DROP TABLE lessons; --",
                  "targetWordLatin": "sqli",
                  "sourceTranslation": "literal text",
                  "partOfSpeech": "Noun"
                }
              ]
            }
        """.trimIndent()

        // Act
        val result = CurriculumImporter.parseJson(sqliPayload)
        repository.importCurriculum(result)
        repository.importCurriculum(result)

        // Assert
        val lessons = db.lessonDao().getAllLessons().first()
        assertTrue(lessons.isNotEmpty())
        val importedLesson = lessons.first { it.id == result.lessons[0].id }
        val vocab = db.vocabDao().getVocabForLessonSync(importedLesson.id)
        assertEquals(1, vocab.size)
        assertEquals("'; DROP TABLE lessons; --", vocab[0].targetWord)
    }
}
