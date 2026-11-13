package com.example.ui.screens

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.LanguageLearnerTheme
import com.example.ui.viewmodel.LanguageLearnerViewModel
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CurriculumAndImportUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: LanguageLearnerViewModel

    @Before
    fun setUp() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        viewModel = LanguageLearnerViewModel(app)
    }

    @Test
    fun `curriculumScreen displays tabs and navigates to SRS and Importer`() {
        var openedSrs = false
        var openedImporter = false

        composeRule.setContent {
            LanguageLearnerTheme {
                CurriculumScreen(
                    viewModel = viewModel,
                    onOpenLesson = {},
                    onOpenSrsReview = { openedSrs = true },
                    onOpenImportExport = { openedImporter = true }
                )
            }
        }

        composeRule.onNodeWithText("Полиглот").assertIsDisplayed()
        composeRule.onNodeWithTag("tab_lessons").assertIsDisplayed()
        composeRule.onNodeWithTag("tab_srs").performClick()
        composeRule.onNodeWithTag("start_srs_review_button").performClick()
        assertTrue(openedSrs)

        composeRule.onNodeWithTag("tab_import").performClick()
        composeRule.onNodeWithTag("open_importer_button").performClick()
        assertTrue(openedImporter)
    }

    @Test
    fun `importExportScreen displays controls and invokes onBack`() {
        var navigatedBack = false

        composeRule.setContent {
            LanguageLearnerTheme {
                ImportExportScreen(
                    viewModel = viewModel,
                    onBack = { navigatedBack = true }
                )
            }
        }

        composeRule.onNodeWithTag("select_file_button").assertIsDisplayed()
        composeRule.onNodeWithTag("import_text_input").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("submit_import_text_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("back_from_importer").performClick()
        assertTrue(navigatedBack)
    }
}
