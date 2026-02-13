package com.example.hsklearner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.hsklearner.model.ExampleSentence
import com.example.hsklearner.model.Vocabulary
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary WHERE lessonId = :lessonId")
    fun getVocabularyForLesson(lessonId: Int): Flow<List<Vocabulary>>

    @Query("SELECT * FROM vocabulary WHERE id = :vocabId")
    suspend fun getVocabularyById(vocabId: Int): Vocabulary?

    @Query("SELECT * FROM sentences WHERE vocabularyId = :vocabId")
    fun getSentencesForVocabulary(vocabId: Int): Flow<List<ExampleSentence>>
    
    @Query("SELECT * FROM vocabulary WHERE hanzi LIKE :query || '%' OR pinyin LIKE :query || '%' OR definition LIKE '%' || :query || '%'")
    fun searchVocabulary(query: String): Flow<List<Vocabulary>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg vocabulary: Vocabulary)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentences(vararg sentences: ExampleSentence)
}
