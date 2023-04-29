package com.sidgowda.pawcalc.data.settings

import kotlinx.coroutines.flow.Flow

interface SettingsRepo {

    fun settings(): Flow<Settings>

    suspend fun updateSettings(updatedSettings: Settings)

}
