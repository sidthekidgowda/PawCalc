package com.sidgowda.pawcalc.newdog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun NewDog(
    onSaveDog: () -> Unit
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
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        DogInputField(
            value = viewModel.name,
            label = "Name",
            onValueChange = { newValue -> viewModel.updateName(newValue) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        DogInputField(
            value = viewModel.weight,
            label = "Weight",
            placeholder = "lbs",
            onValueChange = { newValue -> viewModel.updateWeight(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        DogInputField(
            value = viewModel.date,
            label = "Date",
            placeholder = "mm/dd/yyyy",
            onValueChange = { newValue -> viewModel.updateDate(newValue) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
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

@Composable
fun DogInputField(
    value: String = "",
    placeholder: String = "",
    label: String = "",
    onValueChange: (String) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        modifier = modifier,
        onValueChange = onValueChange
    )
}
