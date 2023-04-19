package com.sidgowda.pawcalc.doglist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.doglist.ui.DogList
import com.sidgowda.pawcalc.navigation.DOG_LIST_SCREEN_ROUTE

fun NavController.navigateToDogListScreen(navOptions: NavOptions? = null) {
    this.navigate(DOG_LIST_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.dogListScreenDestination(
    onNavigateToOnboarding: () -> Unit,
    onNewDog: () -> Unit,
    onEditDog: (Long) -> Unit,
    onDogDetails: () -> Unit
) {
    composable(route = DOG_LIST_SCREEN_ROUTE) { backStackEntry ->
        DogList(
            savedStateHandle = backStackEntry.savedStateHandle,
            onNavigateToOnboarding = onNavigateToOnboarding,
            onNewDog = onNewDog,
            onEditDog = onEditDog,
            onDogDetails = onDogDetails
        )
    }
}
