package com.sidgowda.pawcalc.newdog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.doginput.DogInput
import com.sidgowda.pawcalc.doginput.model.DogInputMode
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
internal fun NewDog(
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
    val dogInputState: DogInputState by viewModel.inputState.collectAsStateWithLifecycle()

    DogInput(
        modifier = modifier.testTag(NEW_DOG_SCREEN_ROUTE),
        dogInputState = dogInputState,
        dogInputMode = DogInputMode.NEW_DOG,
        onSaveDog = onSaveDog,
        handleEvent = viewModel::handleEvent
    )
}

//--------Preview-----------------------------------------------------------------------------------

@com.sidgowda.pawcalc.ui.theme.LightDarkPreview
@Composable
fun PreviewNewDogScreen() {
    PawCalcTheme {
        NewDog {

        }
    }
}
