package com.example.hsklearner.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hsk_levels")
data class HSKLevel(
    @PrimaryKey val id: Int, // 1, 2, 3, 4, 5, 6
    val level: Int,
    val description: String,
    val totalWords: Int
)
