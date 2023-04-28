package com.sidgowda.pawcalc.data.onboarding.repo

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow

interface OnboardingRepo {

    val onboardingState: Flow<OnboardingState>
    suspend fun setUserOnboarded()
}
