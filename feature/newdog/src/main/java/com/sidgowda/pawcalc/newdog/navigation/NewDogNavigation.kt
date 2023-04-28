package com.sidgowda.pawcalc.newdog.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.NewDog

fun NavController.navigateToNewDogScreen(navOptions: NavOptions? = null) {
    this.navigate(NEW_DOG_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.newDogScreenDestination(
    onSaveDog: () -> Unit
) {
    composable(route = NEW_DOG_SCREEN_ROUTE) {
        NewDog(
            onSaveDog = onSaveDog
        )
    }
}
