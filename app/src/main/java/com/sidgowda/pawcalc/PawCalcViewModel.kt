package com.sidgowda.pawcalc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.domain.dogs.GetOnboardingStateUseCase
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
        .catch {
            // any errors shouldn't crash app. Treat as uninitialized
            emit(PawCalcActivityState.Initialized(onboardingState = OnboardingState.NotOnboarded))
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
