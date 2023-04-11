package com.sidgowda.pawcalc.data.onboarding.datasource

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow

interface OnboardingDataSource {

    val onboardingState: Flow<OnboardingState>
    suspend fun setUserOnboarded()
}
