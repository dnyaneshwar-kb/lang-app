package com.example.hsklearner.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabulary",
    foreignKeys = [
        ForeignKey(
            entity = Lesson::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["lessonId"])]
)
data class Vocabulary(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lessonId: Int,
    val hanzi: String,
    val pinyin: String,
    val definition: String,
    val audioPath: String? = null
)
