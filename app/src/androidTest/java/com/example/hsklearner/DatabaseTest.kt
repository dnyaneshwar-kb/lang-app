package com.example.hsklearner

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.hsklearner.database.AppDatabase
import com.example.hsklearner.database.HSKLevelDao
import com.example.hsklearner.model.CourseUnit
import com.example.hsklearner.model.HSKLevel
import com.example.hsklearner.model.Lesson
import com.example.hsklearner.model.Vocabulary
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var levelDao: HSKLevelDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        levelDao = db.hskLevelDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testFullDatabaseHierarchy() = runBlocking {
        // 1. Insert Level
        val level = HSKLevel(1, 1, "HSK Level 1", 150)
        levelDao.insertAll(level)

        val retrievedLevel = levelDao.getLevelById(1)
        assertEquals("HSK Level 1", retrievedLevel?.description)

        // 2. Insert Unit (Foreign Key to Level)
        val unit = CourseUnit(id = 1, hskLevelId = 1, unitNumber = 1, title = "Greetings")
        db.unitDao().insertAll(unit)

        val units = db.unitDao().getUnitsForLevel(1).first()
        assertEquals(1, units.size)
        assertEquals("Greetings", units[0].title)

        // 3. Insert Lesson (Foreign Key to Unit)
        val lesson = Lesson(id = 10, unitId = 1, lessonNumber = 1, title = "Hello")
        db.lessonDao().insertAll(lesson)

        val lessons = db.lessonDao().getLessonsForUnit(1).first()
        assertEquals(1, lessons.size)
        assertEquals("Hello", lessons[0].title)

        // 4. Insert Vocabulary (Foreign Key to Lesson)
        val vocab = Vocabulary(
            id = 100,
            lessonId = 10,
            hanzi = "你好",
            pinyin = "nǐ hǎo",
            definition = "Hello"
        )
        db.vocabularyDao().insertAll(vocab)

        val vocabularyList = db.vocabularyDao().getVocabularyForLesson(10).first()
        assertEquals(1, vocabularyList.size)
        assertEquals("你好", vocabularyList[0].hanzi)
    }
}
