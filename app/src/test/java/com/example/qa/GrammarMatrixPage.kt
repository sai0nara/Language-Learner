package com.example.qa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo

/**
 * Page Object encapsulating Compose UI selectors and actions for [com.example.ui.screens.GrammarMatrixScreen].
 */
class GrammarMatrixPage(private val composeRule: ComposeContentTestRule) {

    fun assertTextDisplayed(text: String): GrammarMatrixPage = apply {
        composeRule.onNodeWithText(text, substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    fun assertExplanationDisplayed(text: String): GrammarMatrixPage = apply {
        composeRule.onNodeWithText("Грамматическое правило").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText(text, substring = true).performScrollTo().assertIsDisplayed()
    }

    fun assertExplanationAbsent(): GrammarMatrixPage = apply {
        composeRule.onNodeWithText("Грамматическое правило").assertDoesNotExist()
    }

    fun assertFallbackMatrixDisplayed(): GrammarMatrixPage = apply {
        composeRule.onNodeWithText("Матрица спряжения сербского глагола:", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    fun assertFallbackMatrixAbsent(): GrammarMatrixPage = apply {
        composeRule.onNodeWithText("Матрица спряжения сербского глагола:", substring = true)
            .assertDoesNotExist()
    }

    fun tapCellByFormula(formula: String): GrammarMatrixPage = apply {
        composeRule.onNodeWithText(formula)
            .performScrollTo()
            .performClick()
    }

    fun tapMissingDashCell(index: Int = 0): GrammarMatrixPage = apply {
        composeRule.onAllNodesWithText("—")[index]
            .performScrollTo()
            .performClick()
    }

    fun assertDetailCardDisplayed(): GrammarMatrixPage = apply {
        composeRule.onNodeWithTag("selected_matrix_cell_card")
            .performScrollTo()
            .assertIsDisplayed()
    }

    fun assertDetailCardAbsent(): GrammarMatrixPage = apply {
        composeRule.onNodeWithTag("selected_matrix_cell_card").assertDoesNotExist()
    }

    fun assertNotesAbsent(): GrammarMatrixPage = apply {
        composeRule.onNodeWithText("Примечание:", substring = true).assertDoesNotExist()
    }

    fun tapAudioButton(): GrammarMatrixPage = apply {
        composeRule.onNodeWithContentDescription("Озвучить пример")
            .performScrollTo()
            .performClick()
    }

    fun assertAudioButtonAbsent(): GrammarMatrixPage = apply {
        composeRule.onNodeWithContentDescription("Озвучить пример").assertDoesNotExist()
    }
}
