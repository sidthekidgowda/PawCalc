package com.sidgowda.pawcalc.data.settings.repo

import com.sidgowda.pawcalc.data.settings.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepo {

    fun settings(): Flow<Settings>

    suspend fun updateSettings(updatedSettings: Settings)

}
