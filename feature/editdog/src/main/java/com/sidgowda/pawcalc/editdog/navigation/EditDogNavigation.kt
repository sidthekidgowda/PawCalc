package com.sidgowda.pawcalc.editdog

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.editdog.ui.EditDog

fun NavController.navigateToEditDogScreen(dogId: Int, navOptions: NavOptions? = null) {
    this.navigate("edit/$dogId", navOptions)
}

fun NavGraphBuilder.editDogScreenDestination(
    onSaveDog: () -> Unit
) {
    composable(
        route = "edit/{dogId}",
        arguments = listOf(navArgument("dogId") { type = NavType.IntType })
    ) {
        EditDog(
            dogId = it.arguments?.getInt("dogId") ?: 0,
            onSaveDog = onSaveDog
        )
    }
}
