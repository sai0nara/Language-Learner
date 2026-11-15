package com.example.qa

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.robolectric.shadows.ShadowLooper

/**
 * Page Object encapsulating Compose UI selectors, actions, and assertions
 * for [com.example.ui.screens.SpacedRepetitionScreen] and the SRS tab/launcher
 * on [com.example.ui.screens.CurriculumScreen].
 */
class SpacedRepetitionPage(private val composeRule: ComposeContentTestRule) {

    fun waitForTag(tag: String, timeoutMillis: Long = 5_000): SpacedRepetitionPage = apply {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            ShadowLooper.idleMainLooper()
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    fun waitForText(text: String, substring: Boolean = false, timeoutMillis: Long = 5_000): SpacedRepetitionPage = apply {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            ShadowLooper.idleMainLooper()
            composeRule.onAllNodesWithText(text, substring = substring)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    fun waitForCondition(timeoutMillis: Long = 5_000, condition: () -> Boolean): SpacedRepetitionPage = apply {
        composeRule.waitUntil(timeoutMillis = timeoutMillis) {
            ShadowLooper.idleMainLooper()
            condition()
        }
    }

    fun tapSrsTab(): SpacedRepetitionPage = apply {
        waitForTag("tab_srs")
        composeRule.onNodeWithTag("tab_srs").performClick()
        composeRule.waitForIdle()
    }

    fun tapStartReview(): SpacedRepetitionPage = apply {
        waitForTag("start_srs_review_button")
        composeRule.onNodeWithTag("start_srs_review_button").performClick()
        composeRule.waitForIdle()
    }

    fun tapShowAnswer(): SpacedRepetitionPage = apply {
        waitForTag("show_answer_button")
        composeRule.onNodeWithTag("show_answer_button").performClick()
        composeRule.waitForIdle()
    }

    fun tapSpeakerButton(): SpacedRepetitionPage = apply {
        composeRule.onNodeWithContentDescription("Озвучить карточку").performClick()
        composeRule.waitForIdle()
    }

    fun tapGrade(tag: String): SpacedRepetitionPage = apply {
        waitForTag(tag)
        composeRule.onNodeWithTag(tag).performClick()
        composeRule.waitForIdle()
    }

    fun tapGradeAgain(): SpacedRepetitionPage = tapGrade("grade_again")

    fun tapGradeHard(): SpacedRepetitionPage = tapGrade("grade_hard")

    fun tapGradeGood(): SpacedRepetitionPage = tapGrade("grade_good")

    fun tapGradeEasy(): SpacedRepetitionPage = tapGrade("grade_easy")

    fun tapBackFromSrs(): SpacedRepetitionPage = apply {
        waitForTag("back_from_srs")
        composeRule.onNodeWithTag("back_from_srs").performClick()
        composeRule.waitForIdle()
    }

    fun tapDoneBackButton(): SpacedRepetitionPage = apply {
        waitForTag("srs_done_back_button")
        composeRule.onNodeWithTag("srs_done_back_button").performClick()
        composeRule.waitForIdle()
    }

    fun assertSrsBadgeCount(count: Int): SpacedRepetitionPage = apply {
        waitForText(count.toString())
        composeRule.onNodeWithText(count.toString()).assertIsDisplayed()
    }

    fun assertStartReviewEnabled(): SpacedRepetitionPage = apply {
        waitForTag("start_srs_review_button")
        composeRule.onNodeWithTag("start_srs_review_button").assertIsEnabled()
    }

    fun assertStartReviewDisabled(): SpacedRepetitionPage = apply {
        waitForTag("start_srs_review_button")
        composeRule.onNodeWithTag("start_srs_review_button").assertIsNotEnabled()
    }

    fun assertPromptText(expected: String): SpacedRepetitionPage = apply {
        waitForTag("srs_prompt_text")
        composeRule.onNodeWithTag("srs_prompt_text").assertTextEquals(expected)
    }

    fun assertAnswerText(expected: String): SpacedRepetitionPage = apply {
        waitForTag("srs_answer_text")
        composeRule.onNodeWithTag("srs_answer_text").assertTextEquals(expected)
    }

    fun assertAnswerNotDisplayed(): SpacedRepetitionPage = apply {
        composeRule.onAllNodesWithTag("srs_answer_text").assertCountEquals(0)
    }

    fun assertShowAnswerDisplayed(): SpacedRepetitionPage = apply {
        waitForTag("show_answer_button")
        composeRule.onNodeWithTag("show_answer_button").assertIsDisplayed()
    }

    fun assertShowAnswerNotDisplayed(): SpacedRepetitionPage = apply {
        composeRule.onAllNodesWithTag("show_answer_button").assertCountEquals(0)
    }

    fun assertGradeButtonsNotDisplayed(): SpacedRepetitionPage = apply {
        composeRule.onAllNodesWithTag("grade_again").assertCountEquals(0)
        composeRule.onAllNodesWithTag("grade_hard").assertCountEquals(0)
        composeRule.onAllNodesWithTag("grade_good").assertCountEquals(0)
        composeRule.onAllNodesWithTag("grade_easy").assertCountEquals(0)
    }

    fun assertEmptyQueueDisplayed(): SpacedRepetitionPage = apply {
        waitForText("Очередь чиста!")
        composeRule.onNodeWithText("Очередь чиста!").assertIsDisplayed()
        composeRule.onNodeWithTag("srs_done_back_button").assertIsDisplayed()
    }

    fun assertTextDisplayed(text: String, substring: Boolean = false): SpacedRepetitionPage = apply {
        waitForText(text, substring = substring)
        composeRule.onNodeWithText(text, substring = substring).assertIsDisplayed()
    }

    fun assertTextNotDisplayed(text: String): SpacedRepetitionPage = apply {
        composeRule.onAllNodesWithText(text).assertCountEquals(0)
    }
}
