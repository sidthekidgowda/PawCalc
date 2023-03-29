package com.sidgowda.pawcalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.onboarding.OnboardingResult
import com.sidgowda.pawcalc.onboarding.OnboardingState
import com.sidgowda.pawcalc.onboarding.navigation.ONBOARDING_ROUTE

const val DOG_LIST_ROUTE = "dog_list_screen"

fun NavController.navigateToDogListScreen(navOptions: NavOptions) {
    this.navigate(DOG_LIST_ROUTE, navOptions)
}

fun NavGraphBuilder.dogListScreenDestination(
    onNavigateToOnboarding: () -> Unit,
    onOnboardingCanceled: () -> Unit
) {
    composable(route = DOG_LIST_ROUTE) { backStackEntry ->
        DogListScreen(
            savedStateHandle = backStackEntry.savedStateHandle,
            onNavigateToOnboarding = onNavigateToOnboarding,
            onOnboardingCanceled = onOnboardingCanceled
        )
    }
}

@Composable
fun DogListScreen(
    savedStateHandle: SavedStateHandle,
    viewModel: DogListViewModel = hiltViewModel(),
    onNavigateToOnboarding: () -> Unit,
    onOnboardingCanceled: () -> Unit
) {
    val onboardingState = viewModel.onboardingState.collectAsState().value
    val onboardingResult by savedStateHandle.getLiveData<OnboardingResult>(ONBOARDING_ROUTE).observeAsState()

    when (onboardingState) {
        OnboardingState.Onboarded -> {
            DogList()
        }
        OnboardingState.NotOnboarded -> {
            when (onboardingResult) {
                null ->  LaunchedEffect(key1 = Unit) {
                    onNavigateToOnboarding()
                }
                OnboardingResult.Completed -> DogList()
                OnboardingResult.Cancelled -> LaunchedEffect(key1 = Unit) {
                    onOnboardingCanceled()
                }
            }
        }
    }
}

@Composable
fun DogList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag(DOG_LIST_ROUTE)
            .fillMaxSize()
            .background(Color.Green)
    ) {
        Text("Dog 1")
        Spacer(Modifier.height(20.dp))
        Text("Dog 2")
    }
}
