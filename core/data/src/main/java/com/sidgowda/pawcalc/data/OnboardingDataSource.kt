package com.sidgowda.pawcalc.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class OnboardingDataSource @Inject constructor() {

    private val _userOnboardedState = MutableStateFlow(false)
    val userOnboardedState = _userOnboardedState.asStateFlow()

    fun setUserOnboarded() {
        _userOnboardedState.update {
            it
        }
    }
}
