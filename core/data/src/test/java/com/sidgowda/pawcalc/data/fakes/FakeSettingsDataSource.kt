package com.sidgowda.pawcalc.data.fakes

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsDataSource : SettingsDataSource {

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
            updatedSettings
        }
    }
}
