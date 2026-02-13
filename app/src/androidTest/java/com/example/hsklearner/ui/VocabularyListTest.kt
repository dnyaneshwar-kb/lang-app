package com.example.hsklearner.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.hsklearner.model.Vocabulary
import org.junit.Rule
import org.junit.Test

class VocabularyListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testVocabularyListDisplaysAndScrolls() {
        // Prepare dummy data
        val dummyVocab = List(20) { index ->
            Vocabulary(
                id = index,
                lessonId = 1,
                hanzi = "Word $index",
                pinyin = "pinyin $index",
                definition = "Definition $index"
            )
        }

        // Set content
        composeTestRule.setContent {
            VocabularyListContent(
                vocabularyList = dummyVocab,
                onBackClick = {}
            )
        }

        // Check if first item is displayed
        composeTestRule.onNodeWithText("Word 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("pinyin 0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Definition 0").assertIsDisplayed()

        // Check if last item is NOT displayed initially (assuming 20 items usually overflow)
        // Note: Screen size dependent, but "Word 19" likely off-screen.
        // We'll scroll to it.
        composeTestRule.onNodeWithText("Word 19").performScrollTo().assertIsDisplayed()
    }
}
