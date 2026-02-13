package com.example.hsklearner.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hsklearner.model.CourseUnit
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {
    @Query("SELECT * FROM units WHERE hskLevelId = :levelId ORDER BY unitNumber ASC")
    fun getUnitsForLevel(levelId: Int): Flow<List<CourseUnit>>

    @Query("SELECT * FROM units WHERE id = :unitId")
    suspend fun getUnitById(unitId: Int): CourseUnit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg units: CourseUnit)
}
