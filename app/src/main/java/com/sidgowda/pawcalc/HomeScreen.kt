package com.sidgowda.pawcalc

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import com.sidgowda.pawcalc.welcome.WelcomeScreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // A surface container using the 'background' color from the theme
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    var isNewUser by remember { mutableStateOf(true) }
    val currentScreen = currentDestination?.route ?: Screens.DogList.route

    Scaffold(
        modifier = modifier,
        topBar = {
            if (listOf(Screens.Welcome.route, Screens.DogList.route).contains(currentScreen)) {
                HomeTopBar { navController.navigate(Screens.Settings.route) }
            } else {
                GoBackTopBar(title = "Settings") {
                    navController.popBackStack()
                }
            }
        }
    ) {
        PawCalcNavGraph(
            isNewUser = isNewUser,
            navController = navController,
            isNotNewUser =  { isNewUser = false}
        )
    }
}

@Composable
fun PawCalcNavGraph(
    isNewUser: Boolean,
    navController: NavHostController,
    isNotNewUser: () -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.DogList.route
    ) {
        composable(route = Screens.Welcome.route) {
            WelcomeScreen(
                onClick = {
                    navController.navigate(Screens.DogList.route)
                    isNotNewUser()
                }
            )
        }
        composable(route = Screens.Settings.route) {
            SettingsScreen()
        }
        composable(route = Screens.DogList.route) {
            NavigateToDogListScreen(isNewUser = isNewUser) {
                navController.navigate(Screens.Welcome.route)
            }
        }
    }
}

@Composable
fun NavigateToDogListScreen(isNewUser: Boolean, navigateToWelcomeScreen: () -> Unit) {
    if (isNewUser) {
        LaunchedEffect(Unit) {
            navigateToWelcomeScreen()
        }
    } else {
        DogListScreen()
    }
}

@Composable
fun HomeTopBar(navigateToScreen: () -> Unit ) {
    TopAppBar(
        title = { Text("PawCalc")},
        actions = {
            IconButton(
                onClick = navigateToScreen
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
fun GoBackTopBar(title: String, navigateBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(Icons.Filled.ArrowBack, null)
            }
        }
    )
}

@Preview
@Composable
fun PreviewHomeTopBar() {
    HomeTopBar {

    }
}

@Preview
@Composable
fun PreviewGoBackTopBar() {
    GoBackTopBar(title = "Settings") {
        
    }
}

@Preview
@Composable
fun PreviewHomeScreen() {
    PawCalcTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column() {
                HomeTopBar {

                }
                WelcomeScreen() {

                }
            }
        }
    }
}
