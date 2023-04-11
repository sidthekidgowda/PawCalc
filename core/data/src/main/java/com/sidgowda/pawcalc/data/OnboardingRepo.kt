package com.sidgowda.pawcalc.data

import com.sidgowda.pawcalc.data.model.OnboardingState
import kotlinx.coroutines.flow.Flow

interface OnboardingRepo {

    fun hasUserOnboarded(): Flow<OnboardingState>
    fun setUserOnboarded()
}
