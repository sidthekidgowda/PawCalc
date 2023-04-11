package com.sidgowda.pawcalc.doglist.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingResult
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.navigation.ONBOARDING_SCREEN_ROUTE

@Composable
fun DogList(
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    onNavigateToOnboarding: () -> Unit,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    val viewModel: DogListViewModel = hiltViewModel()
    val onboardingState = viewModel.isUserOnboarded().collectAsStateWithLifecycle()
    val onboardingResult = savedStateHandle.getLiveData<OnboardingResult>(ONBOARDING_SCREEN_ROUTE).observeAsState().value

    if (onboardingState.value == OnboardingState.Onboarded) {
        DogListScreen(
            modifier = modifier.fillMaxSize(),
            viewModel = viewModel,
            onNewDog = onNewDog,
            onDogDetails = onDogDetails
        )
    } else {
        when (onboardingResult) {
            null ->{
                LaunchedEffect(key1 = Unit) {
                    onNavigateToOnboarding()
                }
            }
            OnboardingResult.Completed -> {
                DogListScreen(
                    modifier = modifier.fillMaxSize(),
                    viewModel = viewModel,
                    onNewDog = onNewDog,
                    onDogDetails = onDogDetails
                )
            }
            OnboardingResult.Cancelled -> {
                val activity = LocalContext.current.findActivity()
                LaunchedEffect(key1 = Unit) {
                    // finish the activity
                    activity.finish()
                }
            }
        }
    }
}

private fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("no activity")
}

@Composable
internal fun DogListScreen(
    modifier: Modifier = Modifier,
    viewModel: DogListViewModel,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {

}
