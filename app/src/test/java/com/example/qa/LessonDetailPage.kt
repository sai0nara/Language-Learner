package com.example.qa

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo

/**
 * Page Object encapsulating Compose UI selectors and interactions for [LessonDetailScreen].
 */
class LessonDetailPage(private val composeRule: ComposeContentTestRule) {

    fun assertActionsDisplayed(): LessonDetailPage = apply {
        composeRule.onNodeWithTag("action_continue_lesson").assertIsDisplayed()
        composeRule.onNodeWithTag("action_vocab_drill").assertIsDisplayed()
        composeRule.onNodeWithTag("action_grammar_matrix").assertIsDisplayed()
    }

    fun tapGrammarMatrix(): LessonDetailPage = apply {
        composeRule.onNodeWithTag("action_grammar_matrix").performClick()
    }

    fun assertTextDisplayed(text: String): LessonDetailPage = apply {
        composeRule.onNodeWithText(text, substring = true).assertIsDisplayed()
    }
}
