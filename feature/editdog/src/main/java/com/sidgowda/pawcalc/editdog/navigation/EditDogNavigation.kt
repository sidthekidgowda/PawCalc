package com.sidgowda.pawcalc.editdog

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.editdog.ui.EditDog
import com.sidgowda.pawcalc.navigation.EDIT_DOG_SCREEN_ROUTE

fun NavController.navigateToEditDogScreen(navOptions: NavOptions? = null) {
    this.navigate(EDIT_DOG_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.editDogScreenDestination(
    onSaveDog: () -> Unit
) {
    composable(route = EDIT_DOG_SCREEN_ROUTE) {
        EditDog(
            onSaveDog = onSaveDog
        )
    }
}
