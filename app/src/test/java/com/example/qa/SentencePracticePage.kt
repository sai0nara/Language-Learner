package com.example.qa

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.robolectric.shadows.ShadowDialog

/**
 * Page Object encapsulating Compose UI selectors, actions, and assertions
 * for [com.example.ui.screens.SentencePracticeScreen].
 */
class SentencePracticePage(private val composeRule: ComposeContentTestRule) {

    fun tapPoolChip(token: String, index: Int = 0): SentencePracticePage = apply {
        composeRule.onAllNodesWithTag("pool_chip_$token")[index].performClick()
    }

    fun tapPlacedChip(token: String, index: Int = 0): SentencePracticePage = apply {
        composeRule.onAllNodesWithTag("placed_chip_$token")[index].performClick()
    }

    fun tapCheckAnswer(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("check_answer_button").performClick()
    }

    fun tapHintButton(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("sentence_hint_button").performClick()
    }

    fun tapDismissHintDialog(): SentencePracticePage = apply {
        composeRule.onNodeWithText("Понятно").performClick()
    }

    fun dismissDialogViaBackPress(): SentencePracticePage = apply {
        ShadowDialog.getLatestDialog()?.onBackPressed()
        composeRule.waitForIdle()
    }

    fun tapSpeakerButton(): SentencePracticePage = apply {
        composeRule.onNodeWithContentDescription("Прослушать целевую фразу").performClick()
    }

    fun tapBackButton(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("back_from_sentence_practice").performClick()
    }

    fun tapFinishPractice(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("finish_practice_button").performClick()
    }

    fun advanceAutoAdvanceDelay(millis: Long = 1300L): SentencePracticePage = apply {
        composeRule.mainClock.advanceTimeBy(millis)
        composeRule.waitForIdle()
    }

    fun assertSourceSentenceDisplayed(text: String): SentencePracticePage = apply {
        composeRule.onNodeWithTag("source_sentence_text").assertIsDisplayed()
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    fun assertPoolChipCount(token: String, expectedCount: Int): SentencePracticePage = apply {
        composeRule.onAllNodesWithTag("pool_chip_$token").assertCountEquals(expectedCount)
    }

    fun assertPlacedChipCount(token: String, expectedCount: Int): SentencePracticePage = apply {
        composeRule.onAllNodesWithTag("placed_chip_$token").assertCountEquals(expectedCount)
    }

    fun assertNoBlankPoolChips(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("pool_chip_").assertDoesNotExist()
    }

    fun assertCheckButtonEnabled(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("check_answer_button").assertIsEnabled()
    }

    fun assertCheckButtonDisabled(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("check_answer_button").assertIsNotEnabled()
    }

    fun assertFeedbackDisplayed(text: String): SentencePracticePage = apply {
        composeRule.onNodeWithText(text).assertIsDisplayed()
    }

    fun assertFeedbackAbsent(text: String): SentencePracticePage = apply {
        composeRule.onNodeWithText(text).assertDoesNotExist()
    }

    fun assertGradeDisplayed(grade: String): SentencePracticePage = apply {
        composeRule.onNodeWithText(grade).assertIsDisplayed()
    }

    fun assertSummaryDisplayed(): SentencePracticePage = apply {
        composeRule.onNodeWithText("Упражнение завершено!").assertIsDisplayed()
        composeRule.onNodeWithTag("finish_practice_button").assertIsDisplayed().assertIsEnabled()
    }

    fun assertUnlockBannerDisplayed(): SentencePracticePage = apply {
        composeRule.onNodeWithText("Поздравляем! Следующий урок разблокирован!").assertIsDisplayed()
    }

    fun assertUnlockBannerAbsent(): SentencePracticePage = apply {
        composeRule.onNodeWithText("Поздравляем! Следующий урок разблокирован!").assertDoesNotExist()
    }

    fun assertDrillingControlsAbsent(): SentencePracticePage = apply {
        composeRule.onNodeWithTag("assembly_bay").assertDoesNotExist()
        composeRule.onNodeWithTag("tile_pool").assertDoesNotExist()
        composeRule.onNodeWithTag("check_answer_button").assertDoesNotExist()
    }

    fun assertHintDialogDisplayed(hintText: String): SentencePracticePage = apply {
        composeRule.onNodeWithText("Подсказка по грамматике").assertIsDisplayed()
        composeRule.onNodeWithText(hintText).assertIsDisplayed()
    }

    fun assertHintDialogAbsent(): SentencePracticePage = apply {
        composeRule.onNodeWithText("Подсказка по грамматике").assertDoesNotExist()
    }

    fun assertAnyPoolChipExists(): SentencePracticePage = apply {
        val count = composeRule.onAllNodes(hasTestTagPrefix("pool_chip_")).fetchSemanticsNodes().size
        check(count > 0) { "Expected at least one pool_chip_* node in tile_pool, found 0" }
    }

    private fun hasTestTagPrefix(prefix: String) =
        androidx.compose.ui.test.SemanticsMatcher("TestTag starts with $prefix") { node ->
            val tag = node.config.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.TestTag) { "" }
            tag.startsWith(prefix)
        }
}
