package com.sidgowda.pawcalc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase
) : ViewModel() {

    val uiState: StateFlow<MainActivityState> = getOnboardingState().map {
        MainActivityState.Initialized(
            it
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        MainActivityState.Loading
    )
}

sealed interface MainActivityState {
    object Loading : MainActivityState
    data class Initialized(
        val onboardingState: OnboardingState
    ) : MainActivityState
}
