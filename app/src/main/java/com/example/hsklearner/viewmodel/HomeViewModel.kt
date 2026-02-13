package com.example.hsklearner.viewmodel

import androidx.lifecycle.ViewModel
import com.example.hsklearner.repository.HskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HskRepository
) : ViewModel() {
    val levels = repository.getAllLevels()
}
