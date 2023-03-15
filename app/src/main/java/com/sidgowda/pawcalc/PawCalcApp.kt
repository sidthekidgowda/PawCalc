package com.sidgowda.pawcalc

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.sidgowda.pawcalc.newdog.navigation.navigateToNewDogScreen
import com.sidgowda.pawcalc.newdog.navigation.newDogScreenDestination
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import com.sidgowda.pawcalc.welcome.WelcomeScreen
import com.sidgowda.pawcalc.welcome.navigation.navigateToWelcomeScreen
import com.sidgowda.pawcalc.welcome.navigation.welcomeScreenDestination

@Composable
fun PawCalcApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    var isNewUser by remember { mutableStateOf(true) }
    val currentScreen = currentDestination?.route ?: Screens.DogList.route
    val backButtonScreens = listOf(Screens.Settings.route)
    val menuActions = listOf(Screens.DogList.route, Screens.Welcome.route)

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopBar(
                title = topBarTitle(currentScreen),
                canNavigateBack = backButtonScreens.contains(currentScreen),
                hasMenuActions = menuActions.contains(currentScreen),
                navigateBack = { navController.navigateUp() },
                navigateToSettings = { navController.navigate(Screens.Settings.route) }
            )
        }
    ) { innerPadding ->
        PawCalcNavGraph(
            isNewUser = isNewUser,
            navController = navController,
            updateNewUser =  { isNewUser = false },
            modifier = modifier.padding(innerPadding)
        )
    }
}

fun topBarTitle(route: String): String =
    when (route) {
        Screens.Welcome.route -> "PawCalc"
        Screens.DogList.route -> "PawCalc"
        Screens.Settings.route -> "Settings"
        else -> "PawCalc"
    }


@Composable
fun PawCalcNavGraph(
    isNewUser: Boolean,
    navController: NavHostController,
    updateNewUser: () -> Unit,
    modifier: Modifier = Modifier
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

@Composable
fun HomeTopBar(
    title: String,
    canNavigateBack: Boolean,
    hasMenuActions: Boolean,
    icons: List<ImageVector> = listOf(Icons.Default.Settings),
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit ) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = if (canNavigateBack) {
            {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Filled.ArrowBack, null)
                }
            }
        } else null,
        actions = {
            if (hasMenuActions) {
                icons.forEach { image ->
                    IconButton(
                        onClick = navigateToSettings
                    ) {
                        Icon(
                            imageVector = image,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewHomeTopBar() {
    HomeTopBar(
        title = "PawCalc",
        canNavigateBack = false,
        hasMenuActions = true,
        navigateBack = {},
        navigateToSettings = {}
    )
}
@Preview
@Composable
fun PreviewSettingsTopBar() {
    HomeTopBar(
        title = "Settings",
        canNavigateBack = true,
        hasMenuActions = false,
        navigateBack = {},
        navigateToSettings = {}
    )
}

@Preview
@Composable
fun PreviewHomeScreen() {
    PawCalcTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                PreviewHomeTopBar()
                WelcomeScreen() {

                }
            }
        }
    }
}
