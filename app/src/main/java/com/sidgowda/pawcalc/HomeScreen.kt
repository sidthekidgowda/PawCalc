package com.sidgowda.pawcalc

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import com.sidgowda.pawcalc.welcome.WelcomeScreen

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { HomeTopBar() }
    ) {
        WelcomeScreen(onClick = {})
    }
}

@Composable
fun HomeTopBar() {
    TopAppBar(
        title = { Text("PawCalc")},
        actions = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Settings, contentDescription = null)
            }
        }
    )
}

@Preview
@Composable
fun PreviewHomeScreen() {
    PawCalcTheme {
        HomeScreen()
    }
}
