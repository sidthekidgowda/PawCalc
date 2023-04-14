package com.sidgowda.pawcalc.doglist.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingProgress
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.model.DogListState
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
    val context = LocalContext.current
    val onboardingProgress: OnboardingProgress =
        savedStateHandle.getLiveData<OnboardingProgress>(ONBOARDING_SCREEN_ROUTE)
            .observeAsState().value ?: OnboardingProgress.NotStarted
    var isOnboarded by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.onboardingState.collect { onboardingState ->
            when (onboardingState) {
                OnboardingState.Onboarded -> {
                    isOnboarded = true
                }
                OnboardingState.NotOnboarded -> {
                    when (onboardingProgress) {
                        OnboardingProgress.NotStarted -> {
                            onNavigateToOnboarding()
                        }
                        OnboardingProgress.Cancelled -> {
                            val activity = context.findActivity()
                            activity.finish()
                        }
                    }
                }
            }
        }
    }
    if (isOnboarded) {
        DogListScreen(
            modifier = modifier.fillMaxSize(),
            viewModel = viewModel,
            onNewDog = onNewDog,
            onDogDetails = onDogDetails
        )
    }
}

@Composable
internal fun DogListScreen(
    modifier: Modifier = Modifier,
    viewModel: DogListViewModel,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        // fetch dogs to get most recent updates
        viewModel.fetchDogs()
    }
    val dogListState: DogListState by viewModel.dogListState.collectAsStateWithLifecycle()
    when {
        dogListState.isLoading -> {
            // show progress
        }
        dogListState.isError -> {
            // show error dialog
        }
        else -> {
            if (dogListState.dogs.isEmpty()) {
               //show empty state
            } else {
                //show list with add button
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

