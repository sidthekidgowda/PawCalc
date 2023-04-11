package com.sidgowda.pawcalc.onboarding.ui

import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) : ViewModel() {

    fun setUserOnboarded() {
        onboardingRepo.setUserOnboarded()
    }
}
