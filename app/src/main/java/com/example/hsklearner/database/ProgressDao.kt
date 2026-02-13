package com.example.hsklearner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hsklearner.model.ProgressType
import com.example.hsklearner.model.UserProgress
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM user_progress WHERE lessonId = :lessonId")
    fun getProgressForLesson(lessonId: Int): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress)
    
    @Query("UPDATE user_progress SET isCompleted = :isCompleted, score = :score, lastPracticed = :timestamp WHERE lessonId = :lessonId")
    suspend fun updateProgress(lessonId: Int, isCompleted: Boolean, score: Int, timestamp: Long)

    @Query("SELECT * FROM user_progress")
    fun getAllUserProgress(): Flow<List<UserProgress>>
}
