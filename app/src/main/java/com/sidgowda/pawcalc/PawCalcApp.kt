package com.sidgowda.pawcalc

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
            HomeTopBar(
                title = currentDestination.title,
                navIcon = currentDestination.navIcon,
                actionIcon = currentDestination.actionIcon,
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
    @StringRes title: Int,
    navIcon: ImageVector?,
    actionIcon: ImageVector?,
    onNavIconClick: () -> Unit,
    onActionClick: () -> Unit
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
            if (navIcon != null) {
                IconButton(onClick = onNavIconClick) {
                    Icon(
                        imageVector = navIcon,
                        contentDescription = null,
                        tint = PawCalcTheme.colors.onPrimarySurface()
                    )
                }
            } else {
                null
            }
        },
        actionIcon = {
            if (actionIcon != null) {
                IconButton(onClick = onActionClick) {
                    Icon(
                        imageVector = actionIcon,
                        contentDescription = null,
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
            title = R.string.title_home,
            navIcon = Icons.Default.Close,
            actionIcon = Icons.Default.Settings,
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
            title = R.string.title_settings,
            navIcon = null,
            actionIcon = null,
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
                    title = R.string.title_home,
                    navIcon = null,
                    actionIcon = null,
                    onNavIconClick = {},
                    onActionClick = {}
                )
                Onboarding(onNavigateToNewDog = {}, onPopBackStack = {})
            }
        }
    }
}
