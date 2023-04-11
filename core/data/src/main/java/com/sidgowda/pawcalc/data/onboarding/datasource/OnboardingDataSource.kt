package com.sidgowda.pawcalc.data.onboarding.datasource

import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import kotlinx.coroutines.flow.Flow

interface OnboardingDataSource {

    fun isUserOnboarded(): Flow<OnboardingState>
    fun setUserOnboarded()
}
