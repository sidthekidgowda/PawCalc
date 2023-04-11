package com.sidgowda.pawcalc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sidgowda.pawcalc.doglist.navigation.DOG_LIST_SCREEN_ROUTE
import com.sidgowda.pawcalc.doglist.navigation.dogListScreenDestination
import com.sidgowda.pawcalc.editdog.editDogScreenDestination
import com.sidgowda.pawcalc.newdog.navigation.navigateToNewDogScreen
import com.sidgowda.pawcalc.newdog.navigation.newDogScreenDestination
import com.sidgowda.pawcalc.onboarding.OnboardingResult
import com.sidgowda.pawcalc.onboarding.navigation.ONBOARDING_ROUTE
import com.sidgowda.pawcalc.onboarding.navigation.navigateToOnboarding
import com.sidgowda.pawcalc.onboarding.navigation.onboardingDestination

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
            onDogDetails = {

            }
        )
        onboardingDestination(
            setOnboardingResult = {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    ONBOARDING_ROUTE,
                    OnboardingResult.Cancelled
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

