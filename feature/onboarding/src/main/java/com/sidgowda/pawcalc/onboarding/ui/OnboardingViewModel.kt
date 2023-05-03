package com.sidgowda.pawcalc.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sidgowda.pawcalc.domain.onboarding.SetUserOnboardedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
   private val userOnboarded: SetUserOnboardedUseCase
) : ViewModel() {

    fun setUserOnboarded() {
        viewModelScope.launch {
            userOnboarded()
        }
    }
}
