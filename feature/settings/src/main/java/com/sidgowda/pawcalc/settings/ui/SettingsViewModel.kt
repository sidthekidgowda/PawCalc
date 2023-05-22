package com.sidgowda.pawcalc.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import com.sidgowda.pawcalc.domain.settings.UpdateSettingsUseCase
import com.sidgowda.pawcalc.settings.model.SettingsEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase
) : ViewModel() {

    /**
     * Settings should be loaded along with onboarding when Splash Screen is shown.
     * All updates should be fast and synchronous.
     * Updated settings should be saved to disk.
     *
     * Default settings will be overridden in init block
     */
    private val _settings = MutableStateFlow(
        Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    )
    val settings = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            // we only need to use the first emitted settings
            val currentSettings = _settings.updateAndGet {
                getSettingsUseCase().first()
            }
            Timber.d(
                "Current settings: " +
                        "DateFormat-${currentSettings.dateFormat} " +
                        "WeightFormat-${currentSettings.weightFormat} " +
                        "Theme-${currentSettings.themeFormat}"
            )
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
        Timber.d("Updating weight from ${_settings.value.weightFormat} to $weightFormat")
        val updatedSettings = _settings.updateAndGet {
            it.copy(weightFormat = weightFormat)
        }
        saveUpdatedSettings(updatedSettings)
    }

    private fun updateDateFormat(dateFormat: DateFormat) {
        Timber.d("Updating date from ${_settings.value.dateFormat} to $dateFormat")
        val updatedSettings = _settings.updateAndGet {
            it.copy(dateFormat = dateFormat)
        }
        saveUpdatedSettings(updatedSettings)
    }

    private fun updateTheme(theme: ThemeFormat) {
        Timber.d("Updating theme from ${_settings.value.themeFormat} to $theme")
        val updatedSettings = _settings.updateAndGet {
            it.copy(themeFormat = theme)
        }
        saveUpdatedSettings(updatedSettings)
    }

    private fun saveUpdatedSettings(updatedSettings: Settings) {
        viewModelScope.launch {
            updateSettingsUseCase(updatedSettings)
        }
    }
}
