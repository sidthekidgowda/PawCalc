package com.sidgowda.pawcalc.data.onboarding.repo

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow

interface OnboardingRepo {

    fun hasUserOnboarded(): Flow<OnboardingState>
    fun setUserOnboarded()
}
