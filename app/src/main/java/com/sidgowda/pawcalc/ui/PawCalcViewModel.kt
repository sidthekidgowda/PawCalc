package com.sidgowda.pawcalc.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.ThemeFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.domain.onboarding.GetOnboardingStateUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PawCalcViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    @Named("io") ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private companion object {
        private val DEFAULT_SETTINGS = Settings(
            weightFormat = WeightFormat.POUNDS,
            dateFormat = DateFormat.AMERICAN,
            themeFormat = ThemeFormat.SYSTEM
        )
    }

    val uiState: StateFlow<PawCalcActivityState> =
        combine(
            getOnboardingState().catch { emit(OnboardingState.NotOnboarded) },
            getSettingsUseCase().catch { emit(DEFAULT_SETTINGS) }
        ) { onboarding, settings ->
            PawCalcActivityState.Initialized(
                onboarding,
                settings
            )
        }
        .onEach {
            val settings = it.settings
            Timber.d(
                "Current Settings: " +
                        "DateFormat-${settings.dateFormat} " +
                        "WeightFormat-${settings.weightFormat} " +
                        "Theme-${settings.themeFormat}"
            )
        }
        .flowOn(ioDispatcher)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PawCalcActivityState.Loading
        )
}

sealed interface PawCalcActivityState {
    object Loading : PawCalcActivityState
    data class Initialized(
        val onboardingState: OnboardingState,
        val settings: Settings
    ) : PawCalcActivityState
}
