package com.example.hsklearner.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "units",
    foreignKeys = [
        ForeignKey(
            entity = HSKLevel::class,
            parentColumns = ["id"],
            childColumns = ["hskLevelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["hskLevelId"])]
)
data class CourseUnit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hskLevelId: Int,
    val unitNumber: Int,
    val title: String
)
