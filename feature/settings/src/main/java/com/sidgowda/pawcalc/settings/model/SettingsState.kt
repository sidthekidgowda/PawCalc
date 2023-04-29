package com.sidgowda.pawcalc.settings.model

data class SettingsState(
    val weightFormat: WeightFormat = WeightFormat.POUNDS,
    val dateFormat: DateFormat = DateFormat.AMERICAN,
    val theme: Theme = Theme.SYSTEM
)
