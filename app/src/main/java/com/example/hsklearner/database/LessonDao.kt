package com.example.hsklearner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hsklearner.model.Lesson
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {
    @Query("SELECT * FROM lessons WHERE unitId = :unitId ORDER BY lessonNumber ASC")
    fun getLessonsForUnit(unitId: Int): Flow<List<Lesson>>

    @Query("SELECT * FROM lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: Int): Lesson?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg lessons: Lesson)
}
