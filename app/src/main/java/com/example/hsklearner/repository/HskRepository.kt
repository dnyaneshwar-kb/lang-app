package com.example.hsklearner.repository

import com.example.hsklearner.database.*
import javax.inject.Inject

class HskRepository @Inject constructor(
    private val hskLevelDao: HSKLevelDao,
    private val unitDao: UnitDao,
    private val lessonDao: LessonDao,
    private val vocabularyDao: VocabularyDao,
    private val progressDao: ProgressDao
) {
    fun getUnitsForLevel(levelId: Int) = unitDao.getUnitsForLevel(levelId)
    suspend fun getLevelById(levelId: Int) = hskLevelDao.getLevelById(levelId)
    
    fun getLessonsForUnit(unitId: Int) = lessonDao.getLessonsForUnit(unitId)
    suspend fun getUnitById(unitId: Int) = unitDao.getUnitById(unitId)

    fun getVocabularyForLesson(lessonId: Int) = vocabularyDao.getVocabularyForLesson(lessonId)

    fun getAllLevels() = hskLevelDao.getAllLevels()
    suspend fun updateLessonProgress(progress: UserProgress) {
        progressDao.insertProgress(progress)
    }

    fun getLessonProgress(lessonId: Int) = progressDao.getProgressForLesson(lessonId)

    fun getAllProgress() = progressDao.getAllUserProgress()
}
