package com.example.qa

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.robolectric.shadows.ShadowLooper

/**
 * Page Object encapsulating Compose UI selectors, actions, and assertions
 * for [com.example.ui.screens.VocabDrillScreen].
 */
class VocabDrillPage(private val composeRule: ComposeContentTestRule) {

    fun tapOption(optionText: String, index: Int = 0): VocabDrillPage = apply {
        composeRule.onAllNodesWithTag("option_card_$optionText")[index].performClick()
    }

    fun tapPronounceButton(): VocabDrillPage = apply {
        composeRule.onNodeWithTag("vocab_pronounce_button").performClick()
    }

    fun tapRestartButton(): VocabDrillPage = apply {
        composeRule.onNodeWithTag("restart_drill_button").performClick()
    }

    fun tapBackButton(): VocabDrillPage = apply {
        composeRule.onNodeWithTag("back_from_vocab_drill").performClick()
    }

    fun advanceDelay(millis: Long): VocabDrillPage = apply {
        composeRule.mainClock.advanceTimeBy(millis)
        ShadowLooper.idleMainLooper()
        composeRule.waitForIdle()
    }

    fun advanceFrame(): VocabDrillPage = apply {
        composeRule.mainClock.advanceTimeByFrame()
        ShadowLooper.idleMainLooper()
        composeRule.waitForIdle()
    }

    fun currentTargetWord(): String {
        val node = composeRule.onNodeWithTag("target_word_prompt").fetchSemanticsNode()
        return node.config[SemanticsProperties.Text].joinToString("") { it.text }
    }

    fun assertTargetWordDisplayed(expected: String): VocabDrillPage = apply {
        composeRule.onNodeWithTag("target_word_prompt")
            .assertIsDisplayed()
            .assertTextEquals(expected)
    }

    fun assertTargetWordAbsent(): VocabDrillPage = apply {
        composeRule.onNodeWithTag("target_word_prompt").assertDoesNotExist()
    }

    fun assertProgressDisplayed(expectedText: String): VocabDrillPage = apply {
        composeRule.onNodeWithText(expectedText).assertIsDisplayed()
    }

    fun assertOptionCount(expectedCount: Int): VocabDrillPage = apply {
        composeRule.onAllNodes(hasTestTagPrefix("option_card_")).assertCountEquals(expectedCount)
    }

    fun assertOptionTagCount(optionText: String, expectedCount: Int): VocabDrillPage = apply {
        composeRule.onAllNodesWithTag("option_card_$optionText").assertCountEquals(expectedCount)
    }

    fun assertOptionAbsent(optionText: String): VocabDrillPage = apply {
        composeRule.onNodeWithTag("option_card_$optionText").assertDoesNotExist()
    }

    fun assertTextDisplayed(text: String): VocabDrillPage = apply {
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    fun assertTextAbsent(text: String): VocabDrillPage = apply {
        composeRule.onNodeWithText(text).assertDoesNotExist()
    }

    fun assertCompletionViewDisplayed(expectedTotal: Int): VocabDrillPage = apply {
        composeRule.onNodeWithText("Отличная работа!").assertIsDisplayed()
        composeRule.onNodeWithText(
            "Все $expectedTotal новых слов урока успешно усвоены и добавлены в очередь интервального повторения."
        ).assertIsDisplayed()
        composeRule.onNodeWithTag("restart_drill_button").assertIsDisplayed().assertIsEnabled()
        composeRule.onNodeWithTag("target_word_prompt").assertDoesNotExist()
    }

    private fun hasTestTagPrefix(prefix: String) =
        SemanticsMatcher("TestTag starts with $prefix") { node ->
            val tag = node.config.getOrElse(SemanticsProperties.TestTag) { "" }
            tag.startsWith(prefix)
        }
}
