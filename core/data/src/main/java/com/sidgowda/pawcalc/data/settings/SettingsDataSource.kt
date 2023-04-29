package com.sidgowda.pawcalc.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {

    fun settings(): Flow<Settings>

    suspend fun updateSettings(updatedSettings: Settings)

}
