package com.sidgowda.pawcalc.welcome.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.welcome.WelcomeScreenRoute

const val WELCOME_SCREEN_ROUTE = "welcome_screen_route"

fun NavController.navigateToWelcomeScreen(navOptions: NavOptions? = null) {
    this.navigate(WELCOME_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.welcomeScreenDestination(onNavigateToAddDog: () -> Unit) {
    composable(route = WELCOME_SCREEN_ROUTE) {
        WelcomeScreenRoute(onNavigateToNewDogScreen = onNavigateToAddDog)
    }
}
