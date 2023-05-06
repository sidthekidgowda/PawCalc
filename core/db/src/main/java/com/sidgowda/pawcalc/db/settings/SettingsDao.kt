package com.sidgowda.pawcalc.db.settings

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings")
    fun settings(): Flow<List<SettingsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: SettingsEntity)
}
