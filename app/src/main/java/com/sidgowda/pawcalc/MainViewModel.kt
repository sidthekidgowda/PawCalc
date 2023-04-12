package com.sidgowda.pawcalc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase
) : ViewModel() {

    val onboardingState: StateFlow<OnboardingState> = getOnboardingState().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        OnboardingState.NotOnboarded
    )
}
