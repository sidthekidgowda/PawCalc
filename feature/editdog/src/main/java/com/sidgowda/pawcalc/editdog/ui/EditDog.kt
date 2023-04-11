package com.sidgowda.pawcalc.editdog.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidgowda.pawcalc.doginput.DogInput
import com.sidgowda.pawcalc.doginput.model.DogInputMode
import com.sidgowda.pawcalc.doginput.model.DogInputState

@Composable
internal fun EditDog(
    onSaveDog: () -> Unit
) {
    EditDogScreen(
        modifier = Modifier.fillMaxSize(),
        viewModel = hiltViewModel(),
        onSaveDog = onSaveDog
    )
}

@Composable
internal fun EditDogScreen(
    modifier: Modifier = Modifier,
    viewModel: EditDogViewModel,
    onSaveDog: () -> Unit
) {
    val dogInputState: DogInputState by viewModel.dogInputState.collectAsState()

    DogInput(
        modifier = modifier,
        dogInputState = dogInputState,
        dogInputMode = DogInputMode.EDIT_DOG,
        onSaveDog = onSaveDog,
        handleEvent = viewModel::handleEvent
    )
}
