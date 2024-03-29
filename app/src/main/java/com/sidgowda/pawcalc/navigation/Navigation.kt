package com.sidgowda.pawcalc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingProgress
import com.sidgowda.pawcalc.dogdetails.dogDetailsScreenDestination
import com.sidgowda.pawcalc.dogdetails.navigateToDogDetails
import com.sidgowda.pawcalc.doglist.navigation.dogListScreenDestination
import com.sidgowda.pawcalc.editdog.editDogScreenDestination
import com.sidgowda.pawcalc.editdog.navigateToEditDogScreen
import com.sidgowda.pawcalc.navigation.DOG_LIST_SCREEN_ROUTE
import com.sidgowda.pawcalc.navigation.ONBOARDING_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.navigation.navigateToNewDogScreen
import com.sidgowda.pawcalc.newdog.navigation.newDogScreenDestination
import com.sidgowda.pawcalc.onboarding.navigation.navigateToOnboarding
import com.sidgowda.pawcalc.onboarding.navigation.onboardingDestination
import com.sidgowda.pawcalc.settings.navigation.settingsScreenDestination
import timber.log.Timber

@Composable
fun PawCalcNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = DOG_LIST_SCREEN_ROUTE
    ) {
        dogListScreenDestination(
            onNavigateToOnboarding = {
                navController.navigateToOnboarding()
            },
            onNewDog = {
                navController.navigateToNewDogScreen()
            },
            onDogDetails = { dogId ->
                Timber.d("Navigating to Dog with id: $dogId")
                navController.navigateToDogDetails(dogId)
            }
        )
        dogDetailsScreenDestination(
            onEditDog = { dogId ->
                Timber.d("Navigating to Edit Dog with id: $dogId")
                navController.navigateToEditDogScreen(dogId)
            }
        )
        onboardingDestination(
            setOnboardingResult = {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    ONBOARDING_SCREEN_ROUTE,
                    OnboardingProgress.Cancelled
                )
            },
            onNavigateToNewDog = {
                navController.navigateToNewDogScreen()
            },
            onPopBackStack = {
                navController.popBackStack()
            }
        )
        settingsScreenDestination()
        newDogScreenDestination(
            onSaveDog = {
                navController.popBackStack()
            }
        )
        editDogScreenDestination(
            onSaveDog = {
                navController.popBackStack()
            }
        )
    }
}

