package com.example.hsklearner.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.hsklearner.model.*

@Database(
    entities = [
        HSKLevel::class,
        CourseUnit::class,
        Lesson::class,
        Vocabulary::class,
        ExampleSentence::class,
        UserProgress::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hskLevelDao(): HSKLevelDao
    abstract fun unitDao(): UnitDao
    abstract fun lessonDao(): LessonDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun progressDao(): ProgressDao
}
