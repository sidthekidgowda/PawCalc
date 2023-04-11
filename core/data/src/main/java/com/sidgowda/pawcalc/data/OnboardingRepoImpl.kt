package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.model.OnboardingState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepoImpl @Inject constructor(
    private val onboardingDataSource: OnboardingDataSource
) : OnboardingRepo {

    override fun hasUserOnboarded(): Flow<OnboardingState> {
       return onboardingDataSource.userOnboardedState
    }

    override fun setUserOnboarded() {
        onboardingDataSource.setUserOnboarded()
    }
}
