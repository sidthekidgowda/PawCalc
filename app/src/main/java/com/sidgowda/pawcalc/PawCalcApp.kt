package com.sidgowda.pawcalc

import android.annotation.SuppressLint
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sidgowda.pawcalc.ui.component.PawCalcTopAppBar
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import com.sidgowda.pawcalc.welcome.WelcomeScreen

@SuppressLint("UnrememberedMutableState")
@Composable
fun PawCalcApp(
    isNewUser: Boolean = false
) {
    var isNewUser by remember { mutableStateOf(isNewUser) }
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination by derivedStateOf {
        currentBackStack?.destination?.route?.let {
            Destination.fromString(it)
        } ?: run {
            if (isNewUser) {
                Destination.Welcome
            } else {
                Destination.DogList
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                title = currentDestination.title,
                canNavigateBack = navController.backQueue.size > 1,
                navigateBack = { navController.navigateUp() },
                navigateToSettings = { navController.navigate(SETTINGS_ROUTE) }
            )
        }
    ) { innerPadding ->
        PawCalcNavGraph(
            isNewUser = isNewUser,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun HomeTopBar(
    @StringRes title: Int,
    canNavigateBack: Boolean,
    actionIcon: ImageVector = Icons.Default.Settings,
    navigateBack: () -> Unit,
    navigateToSettings: () -> Unit
) {
    PawCalcTopAppBar(
        title = {
            Text(
                text = stringResource(id = title),
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
            title = R.string.title_home,
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
            title = R.string.title_settings,
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
                HomeTopBar(
                    title = R.string.title_home,
                    canNavigateBack = false,
                    navigateBack = {},
                    navigateToSettings = {}
                )
                WelcomeScreen(onNavigateToNewDog = {})
            }
        }
    }
}
