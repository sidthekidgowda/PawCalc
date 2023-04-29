package com.sidgowda.pawcalc.domain.settings

import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.data.settings.repo.SettingsRepo
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepo: SettingsRepo
) {
    suspend operator fun invoke(updatedSettings: Settings) {
        settingsRepo.updateSettings(updatedSettings)
    }
}
