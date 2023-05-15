package com.sidgowda.pawcalc.test.fakes

import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakeOnboardingDataSource(onboardedState: OnboardingState) : OnboardingDataSource {

    val onboardingStateFlow = MutableStateFlow(onboardedState)

    override val onboardingState: Flow<OnboardingState>
        get() = onboardingStateFlow.asStateFlow()

    override suspend fun setUserOnboarded() {
        onboardingStateFlow.update { OnboardingState.Onboarded }
    }

    fun reset() {
        onboardingStateFlow.update { OnboardingState.NotOnboarded }
    }
}
