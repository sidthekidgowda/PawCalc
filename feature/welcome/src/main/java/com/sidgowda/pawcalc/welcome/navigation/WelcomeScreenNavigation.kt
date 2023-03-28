package com.sidgowda.pawcalc.welcome.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.welcome.WelcomeScreen

const val WELCOME_SCREEN_ROUTE = "welcome_screen_route"

fun NavController.navigateToWelcomeScreen(navOptions: NavOptions? = null) {
    this.navigate(WELCOME_SCREEN_ROUTE, navOptions)
}


fun NavGraphBuilder.welcomeScreenDestination(
    setOnboardingResult: () -> Unit,
    onNavigateToNewDog: () -> Unit,
    onPopBackStack: () -> Unit
) {
    composable(route = WELCOME_SCREEN_ROUTE) {
        LaunchedEffect(key1 = Unit) {
            setOnboardingResult()
        }
        WelcomeScreen(
            onNavigateToNewDog = onNavigateToNewDog,
            onPopBackStack = onPopBackStack
        )
    }
}
