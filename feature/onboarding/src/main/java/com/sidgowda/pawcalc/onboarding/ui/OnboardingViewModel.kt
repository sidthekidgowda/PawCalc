package com.sidgowda.pawcalc.onboarding.ui

import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.domain.SetUserOnboardedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
   private val userOnboarded: SetUserOnboardedUseCase
) : ViewModel() {

    fun setUserOnboarded() {
        userOnboarded()
    }
}
