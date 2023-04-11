package com.sidgowda.pawcalc.doglist.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.doglist.ui.DogList

const val DOG_LIST_SCREEN_ROUTE = "dog_list_screen_route"

fun NavController.navigateToDogListScreen(navOptions: NavOptions? = null) {
    this.navigate(DOG_LIST_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.dogListScreenDestination(
    onNavigateToOnboarding: () -> Unit,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    composable(route = DOG_LIST_SCREEN_ROUTE) { backStackEntry ->
        DogList(
            savedStateHandle = backStackEntry.savedStateHandle,
            onNavigateToOnboarding = onNavigateToOnboarding,
            onNewDog = onNewDog,
            onDogDetails = onDogDetails
        )

    }
}
