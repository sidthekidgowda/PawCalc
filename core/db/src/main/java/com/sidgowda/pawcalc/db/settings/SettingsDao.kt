package com.sidgowda.pawcalc.db.settings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings")
    fun settings(): Flow<Settings>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(settings: Settings)

    @Update
    suspend fun update(settings: Settings)

    @Delete
    suspend fun delete(settings: Settings)
}
