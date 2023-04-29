package com.sidgowda.pawcalc.data.settings.repo

import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.SettingsDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class SettingsRepoImpl @Inject constructor(
    private val settingsDataSource: SettingsDataSource,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : SettingsRepo {

    override fun settings(): Flow<Settings> {
        return cachedSettingsDataSource.settings()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        // change to io dispatcher
        withContext(ioDispatcher) {
            cachedSettingsDataSource.updateSettings(updatedSettings)
            diskSettingsDataSource.updateSettings(updatedSettings)
        }
    }
}
