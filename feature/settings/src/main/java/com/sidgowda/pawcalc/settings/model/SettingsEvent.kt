package com.sidgowda.pawcalc.settings.model

import com.sidgowda.pawcalc.common.setting.DateFormat
import com.sidgowda.pawcalc.common.setting.ThemeFormat
import com.sidgowda.pawcalc.common.setting.WeightFormat

sealed interface SettingsEvent {
    data class DateFormatChange(val dateFormat: com.sidgowda.pawcalc.common.setting.DateFormat) : SettingsEvent
    data class WeightFormatChange(val weightFormat: com.sidgowda.pawcalc.common.setting.WeightFormat) : SettingsEvent
    data class ThemeChange(val theme: com.sidgowda.pawcalc.common.setting.ThemeFormat) : SettingsEvent
}
