package com.sidgowda.pawcalc.doglist.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class DogListViewModel @Inject constructor() : ViewModel() {
    private val _onboardingState = MutableStateFlow(false)
    val onboardingState = _onboardingState.asStateFlow()
}
