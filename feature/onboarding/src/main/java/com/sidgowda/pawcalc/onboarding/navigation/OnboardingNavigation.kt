package com.sidgowda.pawcalc.onboarding.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.navigation.ONBOARDING_SCREEN_ROUTE
import com.sidgowda.pawcalc.onboarding.Onboarding

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) {
    this.navigate(ONBOARDING_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.onboardingDestination(
    setOnboardingResult: () -> Unit,
    onNavigateToNewDog: () -> Unit,
    onPopBackStack: () -> Unit
) {
    composable(route = ONBOARDING_SCREEN_ROUTE) {
        LaunchedEffect(key1 = Unit) {
            setOnboardingResult()
        }
        Onboarding(
            onNavigateToNewDog = onNavigateToNewDog,
            onPopBackStack = onPopBackStack
        )
    }
}
