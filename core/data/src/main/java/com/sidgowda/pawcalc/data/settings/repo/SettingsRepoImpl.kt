package com.sidgowda.pawcalc.data.settings.repo

import com.sidgowda.pawcalc.data.settings.datasource.SettingsDataSource
import com.sidgowda.pawcalc.data.settings.model.Settings
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
        return settingsDataSource.settings()
    }

    override suspend fun updateSettings(updatedSettings: Settings) {
        // change to io dispatcher
        withContext(ioDispatcher) {
            settingsDataSource.updateSettings(updatedSettings)
        }
    }
}
