package com.sidgowda.pawcalc.data.settings.datasource

import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.model.toSettings
import com.sidgowda.pawcalc.data.settings.model.toSettingsEntity
import com.sidgowda.pawcalc.db.settings.SettingsDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class CachedSettingsDataSource @Inject constructor(
    private val settingsDao: SettingsDao,
    @Named("ioScope") private val scope: CoroutineScope
) : SettingsDataSource {

    private companion object {
        private val INITIAL_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }

    // SharedFlow will act like a cache and replay most recent update to new collectors
    private val settingsSharedFlow = MutableSharedFlow<Settings>(replay = 1)

    init {
        scope.launch {
            try {
                val savedSettingsList = settingsDao.settings().first()
                if (savedSettingsList.isEmpty()) {
                    Timber.d("No user settings exists. Updating with default settings")
                    updateSettings(INITIAL_SETTINGS)
                } else {
                    Timber.d("User settings exist. Emitting previous settings")
                    settingsSharedFlow.emit(savedSettingsList.first().toSettings())
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading settings, updating with Default settings")
                updateSettings(INITIAL_SETTINGS)
            }
        }
    }

    override fun settings(): Flow<Settings> {
        return settingsSharedFlow.asSharedFlow()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        Timber.d(
            "Updating settings: " +
                    "DateFormat-${updatedSettings.dateFormat} " +
                    "WeightFormat-${updatedSettings.weightFormat} " +
                    "Theme-${updatedSettings.themeFormat}"
        )
        settingsSharedFlow.emit(updatedSettings)
        // overwrite the current settings
        settingsDao.insert(updatedSettings.toSettingsEntity())
    }
}
