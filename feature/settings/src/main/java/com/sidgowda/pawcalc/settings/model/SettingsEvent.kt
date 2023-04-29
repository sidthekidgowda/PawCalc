package com.sidgowda.pawcalc.settings.model

import com.sidgowda.pawcalc.db.settings.DateFormat
import com.sidgowda.pawcalc.db.settings.ThemeFormat
import com.sidgowda.pawcalc.db.settings.WeightFormat

sealed interface SettingsEvent {
    data class DateFormatChange(val dateFormat: DateFormat) : SettingsEvent
    data class WeightFormatChange(val weightFormat: WeightFormat) : SettingsEvent
    data class ThemeChange(val theme: ThemeFormat) : SettingsEvent
}
