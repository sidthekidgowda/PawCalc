package com.sidgowda.pawcalc.data.settings.model

import com.sidgowda.pawcalc.common.setting.DateFormat
import com.sidgowda.pawcalc.common.setting.ThemeFormat
import com.sidgowda.pawcalc.common.setting.WeightFormat

data class Settings(
    val weightFormat: com.sidgowda.pawcalc.common.setting.WeightFormat,
    val dateFormat: com.sidgowda.pawcalc.common.setting.DateFormat,
    val themeFormat: com.sidgowda.pawcalc.common.setting.ThemeFormat
)
