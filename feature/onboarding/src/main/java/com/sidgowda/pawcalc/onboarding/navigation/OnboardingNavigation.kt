package com.sidgowda.pawcalc.onboarding.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.onboarding.Onboarding

const val ONBOARDING_ROUTE = "onboarding_route"

fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) {
    this.navigate(ONBOARDING_ROUTE, navOptions)
}

fun NavGraphBuilder.onboardingDestination(
    setOnboardingResult: () -> Unit,
    onNavigateToNewDog: () -> Unit,
    onPopBackStack: () -> Unit
) {
    composable(route = ONBOARDING_ROUTE) {
        LaunchedEffect(key1 = Unit) {
            setOnboardingResult()
        }
        Onboarding(
            onNavigateToNewDog = onNavigateToNewDog,
            onPopBackStack = onPopBackStack
        )
    }
}
