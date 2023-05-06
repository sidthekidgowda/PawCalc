package com.sidgowda.pawcalc.data.settings.model

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat

data class Settings(
    val weightFormat: WeightFormat,
    val dateFormat: DateFormat,
    val themeFormat: ThemeFormat
)
