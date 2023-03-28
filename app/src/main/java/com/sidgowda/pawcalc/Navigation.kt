package com.sidgowda.pawcalc

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.sidgowda.pawcalc.newdog.navigation.navigateToNewDogScreen
import com.sidgowda.pawcalc.newdog.navigation.newDogScreenDestination
import com.sidgowda.pawcalc.welcome.OnboardingResult
import com.sidgowda.pawcalc.welcome.navigation.WELCOME_SCREEN_ROUTE
import com.sidgowda.pawcalc.welcome.navigation.navigateToWelcomeScreen
import com.sidgowda.pawcalc.welcome.navigation.welcomeScreenDestination

@Composable
fun PawCalcNavGraph(
    navController: NavHostController,
    onActivityFinish: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = DOG_LIST_ROUTE
    ) {
        dogListScreenDestination(
            onNavigateToWelcomeDog = {
                navController.navigateToWelcomeScreen()
            },
            onOnboardingCanceled = onActivityFinish
        )
        welcomeScreenDestination(
            setOnboardingResult = {
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    WELCOME_SCREEN_ROUTE,
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
    }
}

