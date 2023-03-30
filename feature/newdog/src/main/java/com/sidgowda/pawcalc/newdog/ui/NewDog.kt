package com.sidgowda.pawcalc.newdog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.ui.BirthDateInput
import com.sidgowda.pawcalc.doginput.ui.NameInput
import com.sidgowda.pawcalc.doginput.ui.WeightInput
import com.sidgowda.pawcalc.newdog.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel
import com.sidgowda.pawcalc.ui.component.EmptyCameraLogo
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun NewDog(
    onSaveDog: () -> Unit,
) {
    NewDogScreen(
        modifier = Modifier.fillMaxSize(),
        viewModel = hiltViewModel(),
        onSaveDog = onSaveDog
    )
}

@Composable
internal fun NewDogScreen(
    modifier: Modifier = Modifier,
    viewModel: NewDogViewModel,
    onSaveDog: () -> Unit
) {
    // todo - change to collectAsStateWithLifecycle()
    val dogInputState: DogInputState by viewModel.inputState.collectAsState()

    Column(
        modifier = modifier
            .testTag(NEW_DOG_SCREEN_ROUTE)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(PawCalcTheme.colors.background)
            .padding(
                vertical = 16.dp,
                horizontal = 48.dp
            ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(2.dp))
        EmptyCameraLogo()
        NameInput(name = dogInputState.name)
        WeightInput(weight = dogInputState.weight)
        BirthDateInput(date = dogInputState.birthDate)
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            PawCalcButton(
                onClick = onSaveDog,
                content = {
                    Text(
                        text = "Save",
                        style = PawCalcTheme.typography.h3,
                        color = PawCalcTheme.colors.onPrimary
                    )
                }
            )
        }
    }
}

//--------Preview-----------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewNewDogScreen() {
    PawCalcTheme {
        NewDog {

        }
    }
}

@LightDarkPreview
@Composable
fun PreviewNameInput() {
    PawCalcTheme {
        Column(Modifier.fillMaxWidth()) {
            NameInput(name = "Mowgli")
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewWeightInput() {
    PawCalcTheme {
        Column(Modifier.fillMaxWidth()) {
            WeightInput(
                weight = "87.0"
            )
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewBirthDateInput() {
    PawCalcTheme {
        Column(modifier = Modifier.fillMaxWidth()) {
            BirthDateInput(
                date = "07/30/2019"
            )
        }
    }
}
