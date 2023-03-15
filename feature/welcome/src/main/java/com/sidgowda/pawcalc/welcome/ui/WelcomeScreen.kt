package com.sidgowda.pawcalc.welcome

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreenRoute(onNavigateToNewDogScreen: ()-> Unit) {
    WelcomeScreen(onNavigateToAddDog = onNavigateToNewDogScreen)
}

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddDog: ()-> Unit
) {
    Scaffold(
        topBar = {}
    ) { innerPadding ->
        Column(
            modifier = modifier.padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                stringResource(id = R.string.welcome_content),
                fontSize = 36.sp,
                fontFamily = FontFamily.Monospace
            )
            // todo make custom component, Button with Fix width and takes an onClick
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigateToAddDog
            ) {
                Text(
                    stringResource(id = R.string.add_dog),
                    fontSize = 24.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
@Composable
fun PreviewWelcomeScreen() {
    WelcomeScreen(onNavigateToAddDog = {})
}
