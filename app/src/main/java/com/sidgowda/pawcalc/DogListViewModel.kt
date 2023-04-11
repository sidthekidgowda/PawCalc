package com.sidgowda.pawcalc

import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.onboarding.OnboardingSingleton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor() : ViewModel() {

    val onboardingState = OnboardingSingleton.onboardingState
}
