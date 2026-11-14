package com.example.qa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextReplacement

/**
 * Page Object encapsulating Compose UI selectors and interactions for [ImportExportScreen].
 */
class ImportExportPage(private val composeRule: ComposeContentTestRule) {

    fun assertControlsDisplayed(): ImportExportPage = apply {
        composeRule.onNodeWithTag("select_file_button").assertIsDisplayed()
        composeRule.onNodeWithTag("import_text_input").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("submit_import_text_button").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithTag("reset_defaults_button").performScrollTo().assertIsDisplayed()
    }

    fun selectFormat(format: String): ImportExportPage = apply {
        composeRule.onAllNodesWithText(format)[0]
            .performScrollTo()
            .performClick()
    }

    fun enterRawPayload(payload: String): ImportExportPage = apply {
        composeRule.onNodeWithTag("import_text_input")
            .performScrollTo()
            .performTextReplacement(payload)
    }

    fun submitImport(): ImportExportPage = apply {
        composeRule.onNodeWithTag("submit_import_text_button")
            .performScrollTo()
            .performClick()
    }

    fun tapResetToDefaults(): ImportExportPage = apply {
        composeRule.onNodeWithTag("reset_defaults_button")
            .performScrollTo()
            .performClick()
    }

    fun tapSampleTemplate(format: String): ImportExportPage = apply {
        composeRule.onAllNodesWithText(format)[1]
            .performScrollTo()
            .performClick()
    }

    fun assertInputContains(substring: String): ImportExportPage = apply {
        composeRule.onNodeWithTag("import_text_input")
            .performScrollTo()
            .assertTextContains(substring, substring = true)
    }

    fun assertSubmitDisabled(): ImportExportPage = apply {
        composeRule.onNodeWithTag("submit_import_text_button")
            .performScrollTo()
            .assertIsNotEnabled()
    }

    fun assertStatusTextDisplayed(substring: String): ImportExportPage = apply {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(substring, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onNodeWithText(substring, substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
