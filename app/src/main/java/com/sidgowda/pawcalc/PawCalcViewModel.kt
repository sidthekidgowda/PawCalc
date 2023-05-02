package com.sidgowda.pawcalc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.settings.model.Settings
import com.sidgowda.pawcalc.common.setting.DateFormat
import com.sidgowda.pawcalc.common.setting.ThemeFormat
import com.sidgowda.pawcalc.common.setting.WeightFormat
import com.sidgowda.pawcalc.domain.dogs.GetOnboardingStateUseCase
import com.sidgowda.pawcalc.domain.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PawCalcViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase,
    getSettingsUseCase: GetSettingsUseCase,
    @Named("io") ioDispatcher: CoroutineDispatcher
) : ViewModel() {


    val uiState: StateFlow<PawCalcActivityState> =
        combine(getOnboardingState(), getSettingsUseCase()) { onboarding, settings ->
            PawCalcActivityState.Initialized(
                onboarding,
                settings
            )
        }
        .catch {
            // any errors shouldn't crash app. Treat as uninitialized
            emit(
                PawCalcActivityState.Initialized(
                    onboardingState = OnboardingState.NotOnboarded,
                    settings = Settings(
                        weightFormat = com.sidgowda.pawcalc.common.setting.WeightFormat.POUNDS,
                        dateFormat = com.sidgowda.pawcalc.common.setting.DateFormat.AMERICAN,
                        themeFormat = com.sidgowda.pawcalc.common.setting.ThemeFormat.SYSTEM
                    )
                )
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
