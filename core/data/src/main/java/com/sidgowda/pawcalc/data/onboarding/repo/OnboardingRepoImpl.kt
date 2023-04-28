package com.sidgowda.pawcalc.data.onboarding.repo

import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSource
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepoImpl @Inject constructor(
    private val onboardingDataSource: OnboardingDataSource
) : OnboardingRepo {

    override val onboardingState: Flow<OnboardingState>
        get() = onboardingDataSource.onboardingState

    override suspend fun setUserOnboarded() {
        onboardingDataSource.setUserOnboarded()
    }
}
