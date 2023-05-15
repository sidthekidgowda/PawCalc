package com.sidgowda.pawcalc

import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FakeOnboardingDataSource : OnboardingDataSource {

    val onboardingStateFlow = MutableStateFlow(OnboardingState.NotOnboarded)

    override val onboardingState: Flow<OnboardingState>
        get() = onboardingStateFlow.asStateFlow()

    override suspend fun setUserOnboarded() {
        onboardingStateFlow.update { (OnboardingState.Onboarded) }
    }

    fun reset() {
        onboardingStateFlow.update { OnboardingState.NotOnboarded }
    }

    fun startAsOnboarded() {
        onboardingStateFlow.update { OnboardingState.Onboarded }
    }
}
