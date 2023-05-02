package com.sidgowda.pawcalc.settings.model

sealed interface SettingsEvent {
    data class DateFormatChange(val dateFormat: com.sidgowda.pawcalc.common.settings.DateFormat) : SettingsEvent
    data class WeightFormatChange(val weightFormat: com.sidgowda.pawcalc.common.settings.WeightFormat) : SettingsEvent
    data class ThemeChange(val theme: com.sidgowda.pawcalc.common.settings.ThemeFormat) : SettingsEvent
}
