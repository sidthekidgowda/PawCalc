package com.sidgowda.pawcalc.data.onboarding.repo

import com.sidgowda.pawcalc.data.onboarding.datasource.OnboardingDataSourceImpl
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepoImpl @Inject constructor(
    private val onboardingDataSource: OnboardingDataSourceImpl
) : OnboardingRepo {

    override fun hasUserOnboarded(): Flow<OnboardingState> {
       return onboardingDataSource.userOnboardedState
    }

    override fun setUserOnboarded() {
        onboardingDataSource.setUserOnboarded()
    }
}
