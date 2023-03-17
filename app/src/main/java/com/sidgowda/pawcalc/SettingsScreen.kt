package com.sidgowda.pawcalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

const val SETTINGS_ROUTE = "settings_screen"
fun NavGraphBuilder.settingsScreenDestination() {
    composable(route = SETTINGS_ROUTE) {
        SettingsScreen()
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        modifier
            .fillMaxSize()
            .background(Color.Black)) {
        Text("Settings", color = Color.Red)
    }
}

@Preview
@Composable
fun PreviewSettingsScreen() {
    PawCalcTheme {
        SettingsScreen()
    }
}
