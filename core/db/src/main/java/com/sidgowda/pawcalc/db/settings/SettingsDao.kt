package com.sidgowda.pawcalc.db.settings

import androidx.room.*

@Dao
interface SettingsDao {

    @Query("SELECT * FROM settings")
    fun settings(): Settings

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: Settings)

    @Update
    fun update(settings: Settings)

    @Delete
    fun delete(settings: Settings)
}
