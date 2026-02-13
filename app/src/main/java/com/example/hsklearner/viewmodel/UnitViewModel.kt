package com.example.hsklearner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hsklearner.repository.HskRepository
import com.example.hsklearner.model.CourseUnit
import com.example.hsklearner.model.HSKLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnitViewModel @Inject constructor(
    private val repository: HskRepository
) : ViewModel() {

    private val _units = MutableStateFlow<List<CourseUnit>>(emptyList())
    val units = _units.asStateFlow()

    private val _level = MutableStateFlow<HSKLevel?>(null)
    val level = _level.asStateFlow()

    fun loadUnits(levelId: Int) {
        viewModelScope.launch {
            repository.getUnitsForLevel(levelId).collect {
                _units.value = it
            }
        }
        viewModelScope.launch {
            _level.value = repository.getLevelById(levelId)
        }
    }
}
