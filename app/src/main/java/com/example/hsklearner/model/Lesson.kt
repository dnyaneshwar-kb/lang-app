package com.example.hsklearner.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = CourseUnit::class,
            parentColumns = ["id"],
            childColumns = ["unitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["unitId"])]
)
data class Lesson(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val unitId: Int,
    val lessonNumber: Int,
    val title: String
)
