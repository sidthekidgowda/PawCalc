package com.sidgowda.pawcalc.onboarding

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
import com.sidgowda.pawcalc.ui.component.PawCalcLogo
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun Onboarding(
    onNavigateToNewDog: () -> Unit,
    onPopBackStack: () -> Unit
) {
    OnboardingScreen(
        onBoarded = {
            onPopBackStack()
            onNavigateToNewDog()
            OnboardingSingleton.onOnboarded()
        }
    )
}

@Composable
internal fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onBoarded: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        PawCalcLogo(
            modifier = Modifier.size(140.dp),
            contentDescription = stringResource(id = R.string.cd_paw_calc_logo)
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            textAlign = TextAlign.Center,
            text = stringResource(id = R.string.onboarding_content),
            style = PawCalcTheme.typography.h1,
            color = PawCalcTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(50.dp))
        PawCalcButton(
            onClick = onBoarded,
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

//-------Preview------------------------------------------------------------------------------------
@LightDarkPreview
@Composable
fun PreviewOnboardingScreen() {
    PawCalcTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PawCalcTheme.colors.background)
        ) {
            Onboarding(onNavigateToNewDog = {}, onPopBackStack = {})
        }
    }
}
