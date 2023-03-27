package com.sidgowda.pawcalc

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.sidgowda.pawcalc.newdog.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.navigation.navigateToNewDogScreen
import com.sidgowda.pawcalc.newdog.navigation.newDogScreenDestination
import com.sidgowda.pawcalc.welcome.navigation.WELCOME_SCREEN_ROUTE
import com.sidgowda.pawcalc.welcome.navigation.navigateToWelcomeScreen
import com.sidgowda.pawcalc.welcome.navigation.welcomeScreenDestination

@Composable
fun PawCalcNavGraph(
    modifier: Modifier = Modifier,
    isNewUser: Boolean,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = DOG_LIST_ROUTE
    ) {
        welcomeScreenDestination(
            onNavigateToNewDog = {
                navController.navigateToNewDogScreen(
                    navOptions {
                        popUpTo(WELCOME_SCREEN_ROUTE) {
                            inclusive = true
                        }
                    }
                )
            }
        )
        settingsScreenDestination()
        newDogScreenDestination(
            onSaveDog = {
                navController.navigateToDogListScreen(
                    navOptions {
                        popUpTo(NEW_DOG_SCREEN_ROUTE) {
                            inclusive = true
                        }
                    }
                )
            }
        )
        dogListDestination(
            isNewUser = isNewUser,
            navigateToWelcomeScreen = {
                navController.navigateToWelcomeScreen(
                    navOptions {
                        popUpTo(DOG_LIST_ROUTE) {
                            inclusive = true
                        }
                    }
                )
            }
        )
    }
}

fun NavGraphBuilder.dogListDestination(isNewUser: Boolean, navigateToWelcomeScreen: () -> Unit) {
    composable(route = DOG_LIST_ROUTE) {
        if (isNewUser) {
            LaunchedEffect(Unit) {
                navigateToWelcomeScreen()
            }
        } else {
            DogListScreen()
        }
    }
}
