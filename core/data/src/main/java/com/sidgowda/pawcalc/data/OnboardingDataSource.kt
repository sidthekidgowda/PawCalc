package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.model.OnboardingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Create Data source
class OnboardingDataSource @Inject constructor() {

    private val _userOnboardedState = MutableStateFlow(OnboardingState.NotOnboarded)
    val userOnboardedState = _userOnboardedState.asStateFlow()

    fun setUserOnboarded() {
        _userOnboardedState.update { OnboardingState.Onboarded }
    }
}
