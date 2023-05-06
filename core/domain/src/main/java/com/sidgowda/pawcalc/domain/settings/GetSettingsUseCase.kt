package com.sidgowda.pawcalc.domain.settings

import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepo: SettingsRepo
) {
    operator fun invoke(): Flow<Settings> {
        return settingsRepo.settings()
    }
}
