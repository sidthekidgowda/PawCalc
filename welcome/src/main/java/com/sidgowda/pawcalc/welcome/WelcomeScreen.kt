package com.sidgowda.pawcalc.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

const val WELCOME_SCREEN_ROUTE = "welcome_screen"
fun NavGraphBuilder.welcomeGraph(onNavigateToAddDog: () -> Unit) {
    composable(route = WELCOME_SCREEN_ROUTE) {
        WelcomeScreen(onNavigateToAddDog = onNavigateToAddDog)
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier, onNavigateToAddDog: ()-> Unit) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            "Welcome to PawCalc. Find out how old your dog is in accurate human years.",
            fontSize = 36.sp,
            fontFamily = FontFamily.Monospace
        )
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onNavigateToAddDog
        ) {
            Text(
                "Add Dog",
                fontSize = 24.sp
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen(onNavigateToAddDog = {})
}
