package com.example.hsklearner.database

import android.content.Context
import com.example.hsklearner.model.CourseUnit
import com.example.hsklearner.model.HSKLevel
import com.example.hsklearner.model.Lesson
import com.example.hsklearner.model.Vocabulary
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val hskLevelDao: HSKLevelDao,
    private val unitDao: UnitDao,
    private val lessonDao: LessonDao,
    private val vocabularyDao: VocabularyDao
) {

    fun initialize() {
        CoroutineScope(Dispatchers.IO).launch {
            if (hskLevelDao.getLevelById(1) == null) {
                populateLevelData(1, "HSK Level 1", 150, "hsk1_vocab.json")
            }
            if (hskLevelDao.getLevelById(2) == null) {
                populateLevelData(2, "HSK Level 2", 300, "hsk2_vocab.json")
            }
        }
    }

    private suspend fun populateLevelData(levelId: Int, levelName: String, wordCount: Int, jsonFileName: String) {
        // 1. Insert HSK Level
        val hskLevel = HSKLevel(levelId, levelId, levelName, wordCount)
        hskLevelDao.insertAll(hskLevel)

        try {
            val jsonString = readAssetJson(jsonFileName)
            val jsonArray = JSONArray(jsonString)

            val insertedUnits = mutableSetOf<Int>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val unitNum = obj.getInt("unit")
                val unitTitle = obj.getString("unitTitle")
                val lessonNum = obj.getInt("lesson")
                val lessonTitle = obj.getString("lessonTitle")
                val vocabArray = obj.getJSONArray("vocabulary")

                // Generate deterministic IDs based on Level
                // Unit ID = (Level * 1000) + UnitNum
                val deterministicUnitId = (levelId * 1000) + unitNum
                
                if (!insertedUnits.contains(deterministicUnitId)) {
                    val unit = CourseUnit(
                        id = deterministicUnitId,
                        hskLevelId = levelId,
                        unitNumber = unitNum,
                        title = unitTitle
                    )
                    unitDao.insertAll(unit)
                    insertedUnits.add(deterministicUnitId)
                }

                // Lesson ID = (UnitID * 100) + LessonNum
                val deterministicLessonId = (deterministicUnitId * 100) + lessonNum
                
                 val lesson = Lesson(
                    id = deterministicLessonId,
                    unitId = deterministicUnitId,
                    lessonNumber = lessonNum,
                    title = lessonTitle
                )
                lessonDao.insertAll(lesson)

                // Insert Vocab
                val vocabList = mutableListOf<Vocabulary>()
                for (j in 0 until vocabArray.length()) {
                    val vObj = vocabArray.getJSONObject(j)
                    val v = Vocabulary(
                        lessonId = deterministicLessonId,
                        hanzi = vObj.getString("hanzi"),
                        pinyin = vObj.getString("pinyin"),
                        definition = vObj.getString("definition"),
                        audioPath = if (vObj.has("audio")) vObj.getString("audio") else null
                    )
                    vocabList.add(v)
                }
                vocabularyDao.insertAll(*vocabList.toTypedArray())
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun readAssetJson(fileName: String): String {
        val stringBuilder = StringBuilder()
        try {
            val inputStream = context.assets.open(fileName)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return stringBuilder.toString()
    }
}
