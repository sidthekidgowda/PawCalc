package com.sidgowda.pawcalc.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.db.settings.DateFormat
import com.sidgowda.pawcalc.db.settings.ThemeFormat
import com.sidgowda.pawcalc.db.settings.WeightFormat
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.domain.settings.UpdateSettingsUseCase
import com.sidgowda.pawcalc.settings.model.SettingsEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {
    /**
     * Settings should be loaded along with onboarding when Splash Screen is shown.
     * All updates should be fast and synchronous.
     * Updated settings should be saved to disk.
     */
    private val _settings = MutableStateFlow<Settings?>(null)
    val settings = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                // we only need to use the first emitted settings.
                val settings = getSettingsUseCase().first()
                _settings.update { settings }
            } catch (e: Exception) {
                // could be exceptions from Room
            }
        }
    }

    fun handleEvent(settingsEvent: SettingsEvent) {
        when (settingsEvent) {
            is SettingsEvent.WeightFormatChange -> updateWeightFormat(settingsEvent.weightFormat)
            is SettingsEvent.DateFormatChange -> updateDateFormat(settingsEvent.dateFormat)
            is SettingsEvent.ThemeChange -> updateTheme(settingsEvent.theme)
        }
    }

    private fun updateWeightFormat(weightFormat: WeightFormat) {
       val updatedSettings = _settings.updateAndGet {
            it?.copy(weightFormat = weightFormat)
        }
        saveUpdatedSettings(updatedSettings!!)
    }

    private fun updateDateFormat(dateFormat: DateFormat) {
        val updatedSettings = _settings.updateAndGet {
            it?.copy(dateFormat = dateFormat)
        }
        saveUpdatedSettings(updatedSettings!!)
    }

    private fun updateTheme(theme: ThemeFormat) {
        val updatedSettings = _settings.updateAndGet {
            it?.copy(themeFormat = theme)
        }
        saveUpdatedSettings(updatedSettings!!)
    }

    private fun saveUpdatedSettings(updatedSettings: Settings) {
        viewModelScope.launch {
            updateSettingsUseCase(updatedSettings)
        }
    }
}
