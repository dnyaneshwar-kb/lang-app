package com.example.hsklearner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hsklearner.model.HSKLevel
import kotlinx.coroutines.flow.Flow

@Dao
interface HSKLevelDao {
    @Query("SELECT * FROM hsk_levels ORDER BY id ASC")
    fun getAllLevels(): Flow<List<HSKLevel>>

    @Query("SELECT * FROM hsk_levels WHERE id = :levelId")
    suspend fun getLevelById(levelId: Int): HSKLevel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg levels: HSKLevel)
}
