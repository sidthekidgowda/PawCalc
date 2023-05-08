package com.sidgowda.pawcalc.editdog.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.doginput.DogInput
import com.sidgowda.pawcalc.doginput.model.DogInputState

@Composable
internal fun EditDog(
    dogId: Int,
    onSaveDog: () -> Unit
) {
    EditDogScreen(
        modifier = Modifier.fillMaxSize(),
        viewModel = hiltViewModel(),
        dogId = dogId,
        onSaveDog = onSaveDog
    )
}

@Composable
internal fun EditDogScreen(
    modifier: Modifier = Modifier,
    viewModel: EditDogViewModel,
    dogId: Int,
    onSaveDog: () -> Unit
) {
    val dogInputState: DogInputState by viewModel.dogInputState.collectAsStateWithLifecycle()
    DogInput(
        modifier = modifier,
        dogInputState = dogInputState,
        onSaveDog = onSaveDog,
        handleEvent = viewModel::handleEvent
    )
}

//---------Preview----------------------------------------------------------------------------------
// todo add preview
