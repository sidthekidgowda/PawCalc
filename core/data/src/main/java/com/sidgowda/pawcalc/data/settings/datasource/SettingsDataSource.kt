package com.sidgowda.pawcalc.data.settings.datasource

import com.sidgowda.pawcalc.data.settings.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsDataSource {

    fun settings(): Flow<Settings>

    suspend fun updateSettings(updatedSettings: Settings)
}
