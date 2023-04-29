package com.sidgowda.pawcalc.settings.ui

import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.data.settings.DateFormat
import com.sidgowda.pawcalc.data.settings.ThemeFormat
import com.sidgowda.pawcalc.data.settings.WeightFormat
import com.sidgowda.pawcalc.settings.model.SettingsEvent
import com.sidgowda.pawcalc.settings.model.SettingsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
) : ViewModel() {

    // settings should be loaded with onboarding when Splash Screen is shown
    // all updates should be synchronous
    // save changes on background thread to room


    private val _settings = MutableStateFlow(SettingsState())
    val settings = _settings.asStateFlow()


    fun handleEvent(settingsEvent: SettingsEvent) {
        when (settingsEvent) {
            is SettingsEvent.WeightFormatChange -> updateWeightFormat(settingsEvent.weightFormat)
            is SettingsEvent.DateFormatChange -> updateDateFormat(settingsEvent.dateFormat)
            is SettingsEvent.ThemeChange -> updateTheme(settingsEvent.theme)
        }
    }

    private fun updateWeightFormat(weightFormat: WeightFormat) {
        _settings.update {
            it.copy(weightFormat = weightFormat)
        }
        // update to disk
    }

    private fun updateDateFormat(dateFormat: DateFormat) {
        _settings.update {
            it.copy(dateFormat = dateFormat)
        }
        // update to disk
    }

    private fun updateTheme(theme: ThemeFormat) {
        _settings.update {
            it.copy(theme = theme)
        }
        // update to disk
    }

}
