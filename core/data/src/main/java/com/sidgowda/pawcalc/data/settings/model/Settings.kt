package com.sidgowda.pawcalc.data.settings.model

import com.sidgowda.pawcalc.db.settings.DateFormat
import com.sidgowda.pawcalc.db.settings.ThemeFormat
import com.sidgowda.pawcalc.db.settings.WeightFormat

data class Settings(
    val weightFormat: WeightFormat,
    val dateFormat: DateFormat,
    val themeFormat: ThemeFormat
)
