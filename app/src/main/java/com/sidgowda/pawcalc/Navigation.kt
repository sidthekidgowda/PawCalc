package com.sidgowda.pawcalc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.sidgowda.pawcalc.newdog.navigation.navigateToNewDogScreen
import com.sidgowda.pawcalc.newdog.navigation.newDogScreenDestination
import com.sidgowda.pawcalc.welcome.navigation.navigateToWelcomeScreen
import com.sidgowda.pawcalc.welcome.navigation.welcomeScreenDestination

@Composable
fun PawCalcNavGraph(
    modifier: Modifier = Modifier,
    isNewUser: Boolean,
    navController: NavHostController,
    updateNewUser: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screens.DogList.route
    ) {
        welcomeScreenDestination(
            onNavigateToAddDog = {
                navController.navigateToNewDogScreen(
                    navOptions {
                        popUpTo(Screens.Welcome.route) {
                            inclusive = false
                        }
                    }
                )
                updateNewUser()
            }
        )
        settingsScreenDestination()
        newDogScreenDestination(
            onNavigateToDogDetails = {
                navController.navigate(Screens.DogList.route)
            }
        )
        dogListDestination(
            isNewUser = isNewUser,
            navigateToWelcomeScreen = {
                navController.navigateToWelcomeScreen(
                    navOptions {
                        popUpTo(Screens.DogList.route) { inclusive = true }
                    }
                )
            }
        )
    }
}

fun NavGraphBuilder.dogListDestination(isNewUser: Boolean, navigateToWelcomeScreen: () -> Unit) {
    composable(route = Screens.DogList.route) {
        if (isNewUser) {
            LaunchedEffect(key1 = Unit) {
                navigateToWelcomeScreen()
            }
        } else {
            DogListScreen()
        }
    }
}
