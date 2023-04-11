package com.sidgowda.pawcalc.data

import kotlinx.coroutines.flow.Flow

interface OnboardingRepo {

    fun hasUserOnboarded(): Flow<Boolean>
    fun setUserOnboarded()
}
