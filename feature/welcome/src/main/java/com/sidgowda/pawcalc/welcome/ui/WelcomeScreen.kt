package com.sidgowda.pawcalc.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun WelcomeScreenRoute(onNavigateToNewDogScreen: ()-> Unit) {
    WelcomeScreen(onNavigateToAddDog = onNavigateToNewDogScreen)
}

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToAddDog: ()-> Unit
) {
    Column(
        modifier = modifier
            .padding(24.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.welcome_content),
            style = PawCalcTheme.typography.h1,
            color = PawCalcTheme.colors.onBackground
        )
        // todo make custom component, Button with Fix width and takes an onClick
        Spacer(modifier = Modifier.weight(1.0f))
        PawCalcButton(
            onClick = onNavigateToAddDog,
            content = {
                Text(
                    stringResource(id = R.string.add_dog),
                    style = PawCalcTheme.typography.h3,
                    color = PawCalcTheme.colors.onPrimary
                )
            }
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewWelcomeScreen() {
    PawCalcTheme {
        Column(modifier = Modifier
            .fillMaxSize()
            .background(PawCalcTheme.colors.background)) {
            WelcomeScreen(onNavigateToAddDog = {})
        }
    }
}
