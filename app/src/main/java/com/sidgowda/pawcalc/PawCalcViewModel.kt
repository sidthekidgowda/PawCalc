package com.sidgowda.pawcalc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class PawCalcViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase,
    @Named("io") ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    val uiState: StateFlow<PawCalcActivityState> =
        getOnboardingState().map {
            PawCalcActivityState.Initialized(
                it
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
        val onboardingState: OnboardingState
    ) : PawCalcActivityState
}
