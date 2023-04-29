package com.sidgowda.pawcalc.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SettingsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val themeFormat: ThemeFormat,
    val dateFormat: DateFormat,
    val weightFormat: WeightFormat
)
