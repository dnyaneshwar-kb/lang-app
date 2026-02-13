package com.example.hsklearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hsklearner.repository.HskRepository
import com.example.hsklearner.model.Vocabulary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val repository: HskRepository
) : ViewModel() {

    private val _vocabularyList = MutableStateFlow<List<Vocabulary>>(emptyList())
    val vocabularyList = _vocabularyList.asStateFlow()

    fun loadVocabulary(lessonId: Int) {
        viewModelScope.launch {
            repository.getVocabularyForLesson(lessonId).collect {
                _vocabularyList.value = it
            }
        }
    }
}
