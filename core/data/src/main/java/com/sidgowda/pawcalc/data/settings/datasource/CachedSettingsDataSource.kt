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
            val savedSettingsList = settingsDao.settings().first()
            //todo handle io error
            updateSettings(
                if (savedSettingsList.isEmpty()) {
                    INITIAL_SETTINGS
                } else {
                    savedSettingsList.first().toSettings()
                }
            )
        }
    }

    override fun settings(): Flow<Settings> {
        return settingsSharedFlow.asSharedFlow()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        scope.launch {
            settingsSharedFlow.emit(updatedSettings)
            settingsDao.update(updatedSettings.toSettingsEntity())
        }
    }
}
