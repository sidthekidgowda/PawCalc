package com.sidgowda.pawcalc

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sidgowda.pawcalc.navigation.Destination
import com.sidgowda.pawcalc.onboarding.Onboarding
import com.sidgowda.pawcalc.ui.component.PawCalcTopAppBar
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@SuppressLint("UnrememberedMutableState")
@Composable
fun PawCalcApp(
    onActivityFinish: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination by derivedStateOf {
        currentBackStack?.destination?.route?.let {
            Destination.fromString(it)
        } ?: run {
            Destination.DogList
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            // todo - fix top bar for onboarding
            HomeTopBar(
                currentDestination = currentDestination,
                onNavIconClick = {
                    navController.popBackStack(route = DOG_LIST_ROUTE, inclusive = false)
                },
                onActionClick = { navController.navigate(SETTINGS_ROUTE) }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PawCalcNavGraph(
                navController = navController,
                onActivityFinish = onActivityFinish
            )
        }
    }
}

@Composable
fun HomeTopBar(
    currentDestination: Destination,
    onNavIconClick: () -> Unit,
    onActionClick: () -> Unit
) {
    PawCalcTopAppBar(
        title = {
            Text(
                text = stringResource(id = currentDestination.title),
                style = PawCalcTheme.typography.h2,
                color = PawCalcTheme.colors.onPrimarySurface()
            )
        },
        navigationIcon = {
            if (currentDestination.navIcon != null) {
                IconButton(onClick = onNavIconClick) {
                    Icon(
                        modifier = Modifier.size(30.dp),
                        imageVector = currentDestination.navIcon,
                        contentDescription = stringResource(
                            id = currentDestination.navIconContentDescription
                        ),
                        tint = PawCalcTheme.colors.onPrimarySurface()
                    )
                }
            } else {
                null
            }
        },
        actionIcon = {
            if (currentDestination.actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = currentDestination.actionIcon,
                        contentDescription = stringResource(
                            id = currentDestination.actionIconContentDescription
                        ),
                        tint = PawCalcTheme.colors.onPrimarySurface()
                    )
                }
            } else {
                null
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
            currentDestination = Destination.Onboarding,
            onNavIconClick = {},
            onActionClick = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewSettingsTopBar() {
    PawCalcTheme {
        HomeTopBar(
            currentDestination = Destination.Settings,
            onNavIconClick = {},
            onActionClick = {}
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
                    currentDestination = Destination.DogList,
                    onNavIconClick = {},
                    onActionClick = {}
                )
                Onboarding(onNavigateToNewDog = {}, onPopBackStack = {})
            }
        }
    }
}
