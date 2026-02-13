package com.example.hsklearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hsklearner.repository.HskRepository
import com.example.hsklearner.model.Lesson
import com.example.hsklearner.model.CourseUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: HskRepository
) : ViewModel() {

    data class LessonWithStatus(
        val lesson: Lesson,
        val isLocked: Boolean,
        val isCompleted: Boolean,
        val score: Int
    )

    private val _unit = MutableStateFlow<CourseUnit?>(null)
    val unit = _unit.asStateFlow()

    private val _lessonStatuses = MutableStateFlow<List<LessonWithStatus>>(emptyList())
    val lessonStatuses = _lessonStatuses.asStateFlow()

    fun loadLessons(unitId: Int) {
        viewModelScope.launch {
            _unit.value = repository.getUnitById(unitId)
        }

        viewModelScope.launch {
            combine(
                repository.getLessonsForUnit(unitId),
                repository.getAllProgress()
            ) { lessons, progressList ->
                val sortedLessons = lessons.sortedBy { it.lessonNumber }
                val statusList = mutableListOf<LessonWithStatus>()

                var previousCompleted = true // First lesson is always unlocked

                for (lesson in sortedLessons) {
                    val progress = progressList.find { it.lessonId == lesson.id }
                    val isCompleted = progress?.isCompleted == true
                    val score = progress?.score ?: 0
                    
                    // Locked if previous was not completed
                    val isLocked = !previousCompleted
                    
                    statusList.add(LessonWithStatus(lesson, isLocked, isCompleted, score))

                    // Update for next iteration
                    previousCompleted = isCompleted
                }
                statusList
            }.collect {
                _lessonStatuses.value = it
            }
        }
    }
}
