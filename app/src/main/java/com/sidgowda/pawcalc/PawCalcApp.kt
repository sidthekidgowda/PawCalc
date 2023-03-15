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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sidgowda.pawcalc.ui.component.PawCalcTopAppBar
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import com.sidgowda.pawcalc.welcome.WelcomeScreen

@Composable
fun PawCalcApp(
    modifier: Modifier = Modifier,
    isNewUser: Boolean = false
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    var isNewUser by remember { mutableStateOf(isNewUser) }
    val currentScreen = currentDestination?.route ?: Screens.DogList.route
    val backButtonScreens = listOf(Screens.Settings.route)

    Scaffold(
        modifier = modifier,
        topBar = {
            HomeTopBar(
                title = topBarTitle(currentScreen),
                canNavigateBack = backButtonScreens.contains(currentScreen),
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
fun HomeTopBar(
    title: String,
    canNavigateBack: Boolean,
    actionIcon: ImageVector = Icons.Default.Settings,
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit
) {
    PawCalcTopAppBar(
        title = {
            Text(
                text = title,
                style = PawCalcTheme.typography.h2,
                color = PawCalcTheme.colors.onPrimarySurface()
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        null,
                        tint = PawCalcTheme.colors.primarySurface()
                    )
                }
            } else {
                null
            }
        },
        action = {
            IconButton(
                onClick = navigateToSettings
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = PawCalcTheme.colors.onPrimarySurface()
                )
            }
        }
    )
}

//--------------Preview-----------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewHomeTopBar() {
    PawCalcTheme {
        HomeTopBar(
            title = "PawCalc",
            canNavigateBack = false,
            navigateBack = {},
            navigateToSettings = {}
        )
    }
}
@LightDarkPreview
@Composable
fun PreviewSettingsTopBar() {
    PawCalcTheme {
        HomeTopBar(
            title = "Settings",
            canNavigateBack = true,
            navigateBack = {},
            navigateToSettings = {}
        )
    }
}

@LightDarkPreview
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
