package com.example.hsklearner

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class NavigationTest {

    /**
     * AUTO-DEMO: Automated UI Verification
     * Run this test to watch the emulator navigate automatically through the app!
     * Steps:
     * 1. Click "HSK Level 1" on Home Screen.
     * 2. Wait for Lesson List (Unit 1).
     * 3. Click "Quiz" for Lesson 1.
     * 4. Verify Quiz Screen appears.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigateFromHomeToQuiz() {
        // DEMO STEP 1: Home Screen -> Click HSK Level 1
        composeTestRule.onNodeWithText("HSK Level 1").performClick()

        // DEMO STEP 2: Lesson List -> Verify and locate Lesson 1 Quiz
        composeTestRule.onNodeWithText("Unit 1: Introductions & Greetings").assertIsDisplayed()
        
        // Find Quiz button for Lesson 1 and Click
        composeTestRule.onAllNodesWithText("Quiz")[0].performClick()

        // DEMO STEP 3: Quiz Screen -> Verify success
        composeTestRule.onNodeWithText("Quiz").assertIsDisplayed()
        
        // Verify a question is shown (e.g., "Question 1/X")
        composeTestRule.onNodeWithText("Question 1/", substring = true).assertIsDisplayed()
    }
}
