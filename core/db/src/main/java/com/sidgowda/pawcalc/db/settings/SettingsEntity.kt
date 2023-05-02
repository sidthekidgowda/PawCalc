package com.sidgowda.pawcalc.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int,
    val themeFormat: com.sidgowda.pawcalc.common.setting.ThemeFormat,
    val dateFormat: com.sidgowda.pawcalc.common.setting.DateFormat,
    val weightFormat: com.sidgowda.pawcalc.common.setting.WeightFormat
)
