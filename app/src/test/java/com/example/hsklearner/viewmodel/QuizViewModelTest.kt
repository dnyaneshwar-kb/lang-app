package com.example.hsklearner.viewmodel

import com.example.hsklearner.model.QuizType
import com.example.hsklearner.model.UserProgress
import com.example.hsklearner.model.Vocabulary
import com.example.hsklearner.repository.HskRepository
import com.example.hsklearner.util.SpeechRecognitionHelper
import com.example.hsklearner.util.TextToSpeechHelper
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private lateinit var viewModel: QuizViewModel
    private lateinit var repository: HskRepository
    private lateinit var speechHelper: SpeechRecognitionHelper
    private lateinit var ttsHelper: TextToSpeechHelper

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        speechHelper = mockk(relaxed = true)
        ttsHelper = mockk(relaxed = true)

        viewModel = QuizViewModel(repository, speechHelper, ttsHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadQuiz generates questions successfully`() = runTest {
        // Given
        val vocabList = listOf(
            Vocabulary(1, 1, "h1", "p1", "d1"),
            Vocabulary(2, 1, "h2", "p2", "d2"),
            Vocabulary(3, 1, "h3", "p3", "d3"),
            Vocabulary(4, 1, "h4", "p4", "d4")
        )
        coEvery { repository.getVocabularyForLesson(any()) } returns flowOf(vocabList)

        // When
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        // Then
        val state = viewModel.state.value
        assertEquals(4, state.questions.size)
        assertFalse(state.isFinished)
    }

    @Test
    fun `submitAnswer correct updates score and advances`() = runTest {
        // Given
        val vocabList = listOf(Vocabulary(1, 1, "h1", "p1", "d1"))
        coEvery { repository.getVocabularyForLesson(any()) } returns flowOf(vocabList)
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        // When
        // The generated question will have correctOptionIndex.
        val correctIndex = viewModel.state.value.questions[0].correctOptionIndex
        viewModel.submitAnswer(correctIndex)
        
        // Then
        assertEquals(1, viewModel.state.value.score)
        assertEquals(correctIndex, viewModel.state.value.selectedOptionIndex)
    }

    @Test
    fun `submitAnswer incorrect does not update score`() = runTest {
        // Given
        // We need at least 2 options to pick a wrong one.
        val vocabList = listOf(
             Vocabulary(1, 1, "h1", "p1", "d1"),
             Vocabulary(2, 1, "h2", "p2", "d2")
        )
        coEvery { repository.getVocabularyForLesson(any()) } returns flowOf(vocabList)
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        val question = viewModel.state.value.questions[0]
        val wrongIndex = (question.correctOptionIndex + 1) % question.options.size
        
        // When
        viewModel.submitAnswer(wrongIndex)

        // Then
        assertEquals(0, viewModel.state.value.score)
        assertEquals(wrongIndex, viewModel.state.value.selectedOptionIndex)
    }

    @Test
    fun `finishQuiz saves progress`() = runTest {
        // Given
        val vocabList = listOf(Vocabulary(1, 1, "h1", "p1", "d1"))
        coEvery { repository.getVocabularyForLesson(any()) } returns flowOf(vocabList)
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        // When
        val correctIndex = viewModel.state.value.questions[0].correctOptionIndex
        viewModel.submitAnswer(correctIndex)
        viewModel.nextQuestion() // Should finish since 1 question
        advanceUntilIdle()

        // Then
        assertTrue(viewModel.state.value.isFinished)
        assertTrue(viewModel.state.value.passed) // 1/1 = 100%
        
        coVerify { repository.updateLessonProgress(match { it.lessonId == 1 && it.isCompleted && it.score == 100 }) }
    }
}
