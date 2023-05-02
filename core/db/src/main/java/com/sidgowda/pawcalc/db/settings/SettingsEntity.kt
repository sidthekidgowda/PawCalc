package com.sidgowda.pawcalc.db.settings

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sidgowda.pawcalc.common.setting.DateFormat
import com.sidgowda.pawcalc.common.setting.ThemeFormat
import com.sidgowda.pawcalc.common.setting.WeightFormat

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int,
    val themeFormat: ThemeFormat,
    val dateFormat: DateFormat,
    val weightFormat: WeightFormat
)
