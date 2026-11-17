package com.example.qa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo

/**
 * Page Object encapsulating Compose UI selectors and interactions for [CurriculumScreen].
 */
class CurriculumPage(private val composeRule: ComposeContentTestRule) {

    fun assertHeaderDisplayed(): CurriculumPage = apply {
        val expected = "Srpski jezik (B1)"
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(expected).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText(expected).assertIsDisplayed()
    }

    fun toggleTheme(): CurriculumPage = apply {
        composeRule.onNodeWithTag("toggle_theme_button").performClick()
    }

    fun assertLessonCardDisplayed(lessonId: String): CurriculumPage = apply {
        composeRule.onNodeWithTag("lesson_card_$lessonId")
            .performScrollTo()
            .assertIsDisplayed()
    }

    fun tapLessonCard(lessonId: String): CurriculumPage = apply {
        composeRule.onNodeWithTag("lesson_card_$lessonId")
            .performScrollTo()
            .performClick()
    }

    fun assertTextDisplayed(text: String): CurriculumPage = apply {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text, substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeRule.onNodeWithText(text, substring = true)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
