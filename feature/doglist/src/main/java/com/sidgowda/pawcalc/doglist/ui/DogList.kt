package com.sidgowda.pawcalc.doglist.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.onboarding.OnboardingResult
import com.sidgowda.pawcalc.onboarding.navigation.ONBOARDING_ROUTE

@Composable
fun DogList(
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    onNavigateToOnboarding: () -> Unit,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    val viewModel: DogListViewModel = hiltViewModel()
    val onboardingState = viewModel.onboardingState.collectAsStateWithLifecycle()
    val onboardingResult = savedStateHandle.getLiveData<OnboardingResult>(ONBOARDING_ROUTE).observeAsState()

    if (onboardingState.value) {
        DogListScreen(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            onNewDog = {  },
            onDogDetails = onDogDetails
        )
    } else {
        when (onboardingResult) {
            null ->{
                LaunchedEffect(key1 = Unit) {
                    onNavigateToOnboarding()
                }
            }
//            OnboardingResult.Completed -> {
//                DogListScreen(
//                    modifier = Modifier.fillMaxSize(),
//                    viewModel = viewModel,
//                    onNewDog = {  },
//                    onDogDetails = onDogDetails
//                )
//            }
//            OnboardingResult.Cancelled -> {
//                LaunchedEffect(key1 = Unit) {
//                    onOnboardingCanceled()
//                }
//            }
        }
    }
}

@Composable
internal fun DogListScreen(
    modifier: Modifier = Modifier,
    viewModel: DogListViewModel,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {

}
