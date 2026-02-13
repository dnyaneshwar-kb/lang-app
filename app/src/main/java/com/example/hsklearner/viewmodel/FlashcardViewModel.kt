package com.example.hsklearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hsklearner.model.Vocabulary
import com.example.hsklearner.repository.HskRepository
import com.example.hsklearner.util.TextToSpeechHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlashcardState(
    val currentWord: Vocabulary? = null,
    val isFlipped: Boolean = false,
    val currentIndex: Int = 0,
    val totalCount: Int = 0,
    val isFinished: Boolean = false
)

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: HskRepository,
    private val ttsHelper: TextToSpeechHelper
) : ViewModel() {

    private val _state = MutableStateFlow(FlashcardState())
    val state: StateFlow<FlashcardState> = _state.asStateFlow()

    private var vocabularyList: List<Vocabulary> = emptyList()

    fun loadFormat(lessonId: Int) {
        viewModelScope.launch {
            repository.getVocabularyForLesson(lessonId).collect { list ->
                if (list.isNotEmpty()) {
                    vocabularyList = list.shuffled()
                    _state.value = FlashcardState(
                        currentWord = vocabularyList.first(),
                        currentIndex = 0,
                        totalCount = vocabularyList.size
                    )
                }
            }
        }
    }

    fun flipCard() {
        _state.value = _state.value.copy(isFlipped = !_state.value.isFlipped)
    }

    fun nextCard(known: Boolean) {
        val nextIndex = _state.value.currentIndex + 1
        if (nextIndex < vocabularyList.size) {
            _state.value = FlashcardState(
                currentWord = vocabularyList[nextIndex],
                currentIndex = nextIndex,
                totalCount = vocabularyList.size,
                isFlipped = false
            )
        } else {
            _state.value = _state.value.copy(isFinished = true)
        }
        // TODO: Save progress (known/unknown)
    }

    fun playAudio() {
        _state.value.currentWord?.hanzi?.let {
            ttsHelper.speak(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        // We use @Singleton logic for TTS helper usually, but if we want to clean up strictly:
        // ttsHelper.shutdown() 
        // Warning: If injected as Singleton, don't shutdown here if other screens use it.
        // If scoped to ViewModel, then shutdown.
        // My implementation is Singleton, so I won't shutdown here.
    }
}
