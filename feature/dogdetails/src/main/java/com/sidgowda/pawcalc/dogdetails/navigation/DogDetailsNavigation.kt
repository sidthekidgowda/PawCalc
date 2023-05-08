package com.sidgowda.pawcalc.dogdetails

import androidx.navigation.*
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.dogdetails.ui.DogDetails
import com.sidgowda.pawcalc.navigation.DOG_DETAILS_SCREEN_ROUTE

internal const val DOG_ID_KEY = "dogId"

fun NavController.navigateToDogDetails(dogId: Int, navOptions: NavOptions? = null) {
    this.navigate("$DOG_DETAILS_SCREEN_ROUTE/$dogId", navOptions)
}

fun NavGraphBuilder.dogDetailsScreenDestination(
    onEditDog: (Int) -> Unit
) {
    composable(
        route = "DOG_DETAILS_SCREEN_ROUTE/{$DOG_ID_KEY}",
        arguments = listOf(navArgument(DOG_ID_KEY) { type = NavType.IntType})
    ) {
        DogDetails(
            onEditDog = onEditDog
        )
    }
}
