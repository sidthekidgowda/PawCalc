package com.sidgowda.pawcalc.newdog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.newdog.NewDogRoute

const val NEW_DOG_SCREEN_ROUTE = "new_dog_screen_route"

fun NavController.navigateToNewDogScreen(navOptions: NavOptions? = null) {
    this.navigate(NEW_DOG_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.newDogScreenDestination(onNavigateToDogDetails: () -> Unit) {
    composable(route = NEW_DOG_SCREEN_ROUTE) {
        NewDogRoute()
    }
}