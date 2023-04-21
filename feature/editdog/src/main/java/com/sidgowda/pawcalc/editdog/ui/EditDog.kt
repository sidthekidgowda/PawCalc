package com.sidgowda.pawcalc.editdog.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchDogForId(dogId)
    }

    val dogInputState: DogInputState by viewModel.dogInputState.collectAsStateWithLifecycle()
    when {
        dogInputState.isLoading -> CircularProgressIndicator()
        dogInputState.isError ->
            AlertDialog(
                onDismissRequest = onSaveDog,
                title = {
                    // Error
                    // could not load dog

                },
                text = {
                    //could not load dog
                    // please try again later
                },
                confirmButton = {
                    // ok
                }
            )
        else ->
            DogInput(
                modifier = modifier,
                dogInputState = dogInputState,
                onSaveDog = onSaveDog,
                handleEvent = viewModel::handleEvent
            )
    }
}
