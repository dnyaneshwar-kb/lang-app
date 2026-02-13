package com.example.hsklearner.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sentences",
    foreignKeys = [
        ForeignKey(
            entity = Vocabulary::class,
            parentColumns = ["id"],
            childColumns = ["vocabularyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["vocabularyId"])]
)
data class ExampleSentence(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val vocabularyId: Int,
    val chinese: String,
    val pinyin: String,
    val english: String
)
