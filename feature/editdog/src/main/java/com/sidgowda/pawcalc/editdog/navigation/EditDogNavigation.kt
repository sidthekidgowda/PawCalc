package com.sidgowda.pawcalc.editdog

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.editdog.ui.EditDog
import com.sidgowda.pawcalc.navigation.EDIT_DOG_SCREEN_ROUTE

private const val DOG_ID_KEY = "dogId"
fun NavController.navigateToEditDogScreen(dogId: Long, navOptions: NavOptions? = null) {
    this.navigate("$EDIT_DOG_SCREEN_ROUTE/$dogId", navOptions)
}

fun NavGraphBuilder.editDogScreenDestination(
    onSaveDog: () -> Unit
) {
    composable(
        route = "$EDIT_DOG_SCREEN_ROUTE/{$DOG_ID_KEY}",
        arguments = listOf(navArgument(DOG_ID_KEY) { type = NavType.LongType })
    ) {
        EditDog(
            dogId = it.arguments?.getLong(DOG_ID_KEY) ?: 0L,
            onSaveDog = onSaveDog
        )
    }
}
