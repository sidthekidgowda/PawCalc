package com.sidgowda.pawcalc

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sidgowda.pawcalc.navigation.Destination
import com.sidgowda.pawcalc.navigation.SETTINGS_SCREEN_ROUTE
import com.sidgowda.pawcalc.onboarding.Onboarding
import com.sidgowda.pawcalc.test.TestTags.App.TAG_ACTION_ICON_BUTTON
import com.sidgowda.pawcalc.test.TestTags.App.TAG_NAV_ICON_BUTTON
import com.sidgowda.pawcalc.ui.component.PawCalcTopAppBar
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@SuppressLint("UnrememberedMutableState")
@Composable
fun PawCalcHost() {
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
                currentDestination = currentDestination,
                onNavIconClick = {
                    navController.popBackStack()
                },
                onActionClick = { navController.navigate(SETTINGS_SCREEN_ROUTE) }
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PawCalcNavGraph(
                navController = navController
            )
        }
    }
}

@Composable
fun HomeTopBar(
    modifier: Modifier = Modifier,
    currentDestination: Destination,
    onNavIconClick: () -> Unit,
    onActionClick: () -> Unit
) {
    val title = stringResource(id = currentDestination.title)
    val screenLabel = stringResource(
        id = R.string.cd_current_screen, if (currentDestination == Destination.DogList) {
            "$title ${stringResource(id = R.string.cd_home_screen)}"
        } else {
            title
        }
    )
    PawCalcTopAppBar(
        modifier = modifier,
        title = {
            Text(
                modifier = Modifier.semantics {
                    contentDescription = screenLabel
                },
                text = title,
                style = PawCalcTheme.typography.h2,
                color = PawCalcTheme.colors.onPrimarySurface()
            )
        },
        navigationIcon = {
            if (currentDestination.navIcon != null) {
                IconButton(
                    modifier = Modifier.testTag(TAG_NAV_ICON_BUTTON),
                    onClick = onNavIconClick
                ) {
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
                IconButton(
                    onClick = onActionClick,
                    modifier = Modifier.testTag(TAG_ACTION_ICON_BUTTON)
                ) {
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
