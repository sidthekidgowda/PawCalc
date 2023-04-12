package com.sidgowda.pawcalc.doglist.ui

import androidx.lifecycle.ViewModel
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.domain.GetOnboardingStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    getOnboardingState: GetOnboardingStateUseCase
) : ViewModel() {

    val onboardingState: Flow<OnboardingState> = getOnboardingState()
}
