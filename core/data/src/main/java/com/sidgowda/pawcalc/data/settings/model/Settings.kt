package com.sidgowda.pawcalc.data.settings.model

import com.sidgowda.pawcalc.common.setting.DateFormat
import com.sidgowda.pawcalc.common.setting.ThemeFormat
import com.sidgowda.pawcalc.common.setting.WeightFormat

data class Settings(
    val weightFormat: WeightFormat,
    val dateFormat: DateFormat,
    val themeFormat: ThemeFormat
)
