package com.example.hsklearner

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hsklearner.database.AppDatabase
import com.example.hsklearner.database.ProgressDao
import com.example.hsklearner.model.UserProgress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ProgressIntegrationTest {
    private lateinit var db: AppDatabase
    private lateinit var progressDao: ProgressDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        progressDao = db.progressDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun verifyProgressPersistence() = runBlocking {
        val lessonId = 1
        val progress = UserProgress(
            lessonId = lessonId,
            isCompleted = true,
            score = 80,
            lastPracticed = System.currentTimeMillis()
        )

        // 1. Insert Progress
        progressDao.insertProgress(progress)

        // 2. Retrieve
        val retrieved = progressDao.getProgressForLesson(lessonId).first()
        
        assertNotNull(retrieved)
        assertTrue(retrieved!!.isCompleted)
        assertEquals(80, retrieved.score)

        // 3. Update Progress (Simulate retaking quiz with better score)
        val newProgress = progress.copy(score = 90, lastPracticed = System.currentTimeMillis())
        progressDao.insertProgress(newProgress)

        val updated = progressDao.getProgressForLesson(lessonId).first()
        assertEquals(90, updated!!.score)
    }
}
