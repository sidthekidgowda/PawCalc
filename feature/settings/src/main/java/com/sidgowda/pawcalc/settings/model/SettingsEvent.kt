package com.sidgowda.pawcalc.settings.model

import com.sidgowda.pawcalc.data.settings.model.DateFormat
import com.sidgowda.pawcalc.data.settings.model.ThemeFormat
import com.sidgowda.pawcalc.data.settings.model.WeightFormat

sealed interface SettingsEvent {
    data class DateFormatChange(val dateFormat: DateFormat) : SettingsEvent
    data class WeightFormatChange(val weightFormat: WeightFormat) : SettingsEvent
    data class ThemeChange(val theme: ThemeFormat) : SettingsEvent
}
