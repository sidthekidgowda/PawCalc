package com.sidgowda.pawcalc.onboarding

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class OnboardingState {
    NotOnboarded,
    Onboarded
}

enum class OnboardingResult {
    Completed,
    Cancelled
}

object OnboardingSingleton {
    private val _onboardingState = MutableStateFlow(OnboardingState.NotOnboarded)
    val onboardingState: StateFlow<OnboardingState> = _onboardingState.asStateFlow()

    fun onOnboarded() {
        _onboardingState.value = OnboardingState.Onboarded
    }
}
