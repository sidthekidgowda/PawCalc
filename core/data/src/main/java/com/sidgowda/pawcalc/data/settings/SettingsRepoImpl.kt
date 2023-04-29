package com.sidgowda.pawcalc.data.settings

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class SettingsRepoImpl @Inject constructor(
    @Named("cached") private val cachedSettingsDataSource: SettingsDataSource,
    @Named("disk") private val diskSettingsDataSource: SettingsDataSource,
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
