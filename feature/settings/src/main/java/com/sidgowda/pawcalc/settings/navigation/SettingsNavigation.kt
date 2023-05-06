package com.sidgowda.pawcalc.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.navigation.SETTINGS_SCREEN_ROUTE
import com.sidgowda.pawcalc.settings.ui.Settings

fun NavController.navigateToSettingsScreen(navOptions: NavOptions? = null) {
    this.navigate(SETTINGS_SCREEN_ROUTE, navOptions)
}

fun NavGraphBuilder.settingsScreenDestination() {
    composable(
        route = SETTINGS_SCREEN_ROUTE
    ) {
        Settings()
    }
}
