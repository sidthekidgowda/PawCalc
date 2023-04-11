package com.sidgowda.pawcalc.data.onboarding.datasource

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Create Data source
class OnboardingDataSourceImpl @Inject constructor() : OnboardingDataSource {

    private val _userOnboardedState = MutableStateFlow(OnboardingState.NotOnboarded)
    val userOnboardedState = _userOnboardedState.asStateFlow()
    override fun isUserOnboarded(): Flow<OnboardingState> {
        return userOnboardedState
    }

    override fun setUserOnboarded() {
        _userOnboardedState.update { OnboardingState.Onboarded }
    }
}
