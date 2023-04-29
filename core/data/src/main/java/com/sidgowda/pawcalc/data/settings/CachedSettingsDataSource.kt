package com.sidgowda.pawcalc.data.settings

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class CachedSettingsDataSource @Inject constructor() : SettingsDataSource {

    private val settings = MutableStateFlow(
        Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    )

    override fun settings(): Flow<Settings> {
        return settings.asStateFlow()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        settings.update {
            it.copy(
                weightFormat = updatedSettings.weightFormat,
                dateFormat = updatedSettings.dateFormat,
                themeFormat = updatedSettings.themeFormat
            )
        }
    }
}
