package com.example.hsklearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hsklearner.model.UserProgress
import com.example.hsklearner.model.Vocabulary
import com.example.hsklearner.repository.HskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val passed: Boolean = false,
    val selectedOptionIndex: Int? = null,
    val isAnswerCorrect: Boolean? = null
)

data class QuizQuestion(
    val questionText: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val type: QuizType
)

enum class QuizType {
    HANZI_TO_DEF, DEF_TO_HANZI
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: HskRepository
) : ViewModel() {

    private val _state = MutableStateFlow(QuizState())
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private var currentLessonId: Int = 0

    fun loadQuiz(lessonId: Int) {
        currentLessonId = lessonId
        viewModelScope.launch {
            repository.getVocabularyForLesson(lessonId).collect { vocabList ->
                if (vocabList.isNotEmpty()) {
                    generateQuestions(vocabList)
                }
            }
        }
    }

    private fun generateQuestions(vocabList: List<Vocabulary>) {
        val questions = vocabList.map { targetWord ->
            // Randomly decide question type
            val type = if (Math.random() > 0.5) QuizType.HANZI_TO_DEF else QuizType.DEF_TO_HANZI
            
            // Pick 3 distractors
            val distractors = vocabList.filter { it.id != targetWord.id }.shuffled().take(3)
            // If not enough words, duplicates possible (for HSK1 lesson size ~10, should be ok)
            // Ideally we pull from broader pool, but lesson-scope is requested.

            val options = (distractors + targetWord).shuffled()
            val correctIndex = options.indexOf(targetWord)

            val questionText = if (type == QuizType.HANZI_TO_DEF) targetWord.hanzi else targetWord.definition
            val optionTexts = options.map { 
                if (type == QuizType.HANZI_TO_DEF) it.definition else it.hanzi 
            }

            QuizQuestion(questionText, optionTexts, correctIndex, type)
        }.shuffled()

        _state.value = QuizState(questions = questions)
    }

    fun submitAnswer(optionIndex: Int) {
        if (_state.value.selectedOptionIndex != null) return // Already answered

        val currentQuestion = _state.value.questions[_state.value.currentQuestionIndex]
        val isCorrect = optionIndex == currentQuestion.correctOptionIndex
        val newScore = if (isCorrect) _state.value.score + 1 else _state.value.score

        _state.value = _state.value.copy(
            selectedOptionIndex = optionIndex,
            isAnswerCorrect = isCorrect,
            score = newScore
        )
    }

    fun nextQuestion() {
        val nextIndex = _state.value.currentQuestionIndex + 1
        if (nextIndex < _state.value.questions.size) {
            _state.value = _state.value.copy(
                currentQuestionIndex = nextIndex,
                selectedOptionIndex = null,
                isAnswerCorrect = null
            )
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        val score = _state.value.score
        val total = _state.value.questions.size
        val percentage = if (total > 0) (score.toFloat() / total) * 100 else 0f
        val passed = percentage >= 70f

        _state.value = _state.value.copy(
            isFinished = true,
            passed = passed
        )

        // Save progress
        viewModelScope.launch {
            val progress = UserProgress(
                lessonId = currentLessonId,
                isCompleted = passed,
                score = percentage.toInt(),
                lastPracticed = System.currentTimeMillis()
            )
            repository.updateLessonProgress(progress)
        }
    }
}
