package com.sidgowda.pawcalc.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepoImpl @Inject constructor(
    private val onboardingDataSource: OnboardingDataSource
) : OnboardingRepo {

    override fun hasUserOnboarded(): Flow<Boolean> {
       return onboardingDataSource.userOnboardedState
    }

    override fun setUserOnboarded() {
        onboardingDataSource.setUserOnboarded()
    }
}
