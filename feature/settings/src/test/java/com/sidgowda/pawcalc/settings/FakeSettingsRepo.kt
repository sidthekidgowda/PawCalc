package com.sidgowda.pawcalc.settings

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeSettingsRepo : SettingsRepo {

    private val settings = MutableStateFlow(
        Settings(
            themeFormat = ThemeFormat.SYSTEM,
            dateFormat = DateFormat.AMERICAN,
            weightFormat = WeightFormat.POUNDS
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
