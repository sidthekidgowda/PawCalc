package com.sidgowda.pawcalc.settings.model

import com.sidgowda.pawcalc.data.settings.model.DateFormat
import com.sidgowda.pawcalc.data.settings.model.ThemeFormat
import com.sidgowda.pawcalc.data.settings.model.WeightFormat

data class SettingsState(
    val weightFormat: WeightFormat = WeightFormat.POUNDS,
    val dateFormat: DateFormat = DateFormat.AMERICAN,
    val theme: ThemeFormat = ThemeFormat.SYSTEM
)
