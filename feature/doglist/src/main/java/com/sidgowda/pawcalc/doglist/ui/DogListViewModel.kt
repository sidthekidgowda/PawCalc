package com.sidgowda.pawcalc.doglist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.data.onboarding.repo.OnboardingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor(
    private val onboardingRepo: OnboardingRepo
) : ViewModel() {
    fun isUserOnboarded(): StateFlow<OnboardingState> {
        return onboardingRepo.hasUserOnboarded().stateIn(
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope,
            initialValue = OnboardingState.NotOnboarded
        )
    }

}
