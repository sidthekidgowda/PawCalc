package com.sidgowda.pawcalc.settings.model

import com.sidgowda.pawcalc.data.settings.DateFormat
import com.sidgowda.pawcalc.data.settings.ThemeFormat
import com.sidgowda.pawcalc.data.settings.WeightFormat

data class SettingsState(
    val weightFormat: WeightFormat = WeightFormat.POUNDS,
    val dateFormat: DateFormat = DateFormat.AMERICAN,
    val theme: ThemeFormat = ThemeFormat.SYSTEM
)
