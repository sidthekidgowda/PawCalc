package com.sidgowda.pawcalc.settings.model

sealed interface SettingsEvent {
    data class DateFormatChange(val dateFormat: DateFormat) : SettingsEvent
    data class WeightFormatChange(val weightFormat: WeightFormat) : SettingsEvent
    data class ThemeChange(val theme: ThemeFormat) : SettingsEvent
}
