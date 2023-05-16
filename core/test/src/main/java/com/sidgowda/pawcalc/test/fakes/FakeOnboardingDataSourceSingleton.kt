package com.sidgowda.pawcalc.test.fakes

import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FakeOnboardingDataSourceSingleton : OnboardingDataSource {

    var onboarding = MutableStateFlow(OnboardingState.NotOnboarded)

    override val onboardingState: Flow<OnboardingState>
        get() = onboarding.asStateFlow()

    override suspend fun setUserOnboarded() {
        onboarding.update { OnboardingState.Onboarded }
    }
}
