package com.example.hsklearner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lessonId: Int,
    val isCompleted: Boolean,
    val score: Int, // Percentage 0-100
    val lastPracticed: Long
)
