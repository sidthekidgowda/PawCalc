package com.sidgowda.pawcalc.data.settings.datasource

import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.model.toSettings
import com.sidgowda.pawcalc.data.settings.model.toSettingsEntity
import com.sidgowda.pawcalc.db.settings.DateFormat
import com.sidgowda.pawcalc.db.settings.SettingsDao
import com.sidgowda.pawcalc.db.settings.ThemeFormat
import com.sidgowda.pawcalc.db.settings.WeightFormat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
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

    private val settingsSharedFlow = MutableSharedFlow<Settings>(replay = 1)

    init {
        scope.launch {
            try {
                val savedSettingsList = settingsDao.settings().first()
                //todo handle io error
                updateSettings(
                    if (savedSettingsList.isEmpty()) {
                        INITIAL_SETTINGS
                    } else {
                        savedSettingsList.first().toSettings()
                    }
                )
            } catch (e: Exception) {
                //todo add log for error
                updateSettings(INITIAL_SETTINGS)
            }
        }
    }

    override fun settings(): Flow<Settings> {
        return settingsSharedFlow.asSharedFlow()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        settingsSharedFlow.emit(updatedSettings)
        settingsDao.update(updatedSettings.toSettingsEntity())
    }
}
