package com.sidgowda.pawcalc.newdog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel

@Composable
fun NewDogRoute(newDogViewModel: NewDogViewModel = hiltViewModel()) {
    NewDogScreen(
        newDogViewModel,
        onNavigateToDogDetails = {}
    )
}

@Composable
fun NewDogScreen(
    newDogViewModel: NewDogViewModel,
    modifier: Modifier = Modifier,
    onNavigateToDogDetails: () -> Unit) {

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("New Dog", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(16.dp))
        DogInputField(
            value = newDogViewModel.name,
            placeholder = "Name",
            onValueChange = { newValue -> newDogViewModel.updateName(newValue) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        DogInputField(
            value = newDogViewModel.weight,
            placeholder = "Weight",
            onValueChange = { newValue -> newDogViewModel.updateWeight(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        DogInputField(
            value = newDogViewModel.date,
            placeholder = "mm/dd/yyyy",
            onValueChange = { newValue -> newDogViewModel.updateDate(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Save")
        }
    }
}

@Composable
fun DogInputField(
    value: String = "",
    placeholder: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        placeholder = { Text(placeholder) },
        modifier = modifier,
        onValueChange = onValueChange
    )
}
