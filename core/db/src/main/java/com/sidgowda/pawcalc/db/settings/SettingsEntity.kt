package com.sidgowda.pawcalc.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int,
    val themeFormat: ThemeFormat,
    val dateFormat: DateFormat,
    val weightFormat: WeightFormat
)
