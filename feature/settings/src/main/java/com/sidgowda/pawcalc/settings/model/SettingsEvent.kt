package com.sidgowda.pawcalc.settings.model

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat

sealed interface SettingsEvent {
    data class DateFormatChange(val dateFormat: DateFormat) : SettingsEvent
    data class WeightFormatChange(val weightFormat: WeightFormat) : SettingsEvent
    data class ThemeChange(val theme: ThemeFormat) : SettingsEvent
}
