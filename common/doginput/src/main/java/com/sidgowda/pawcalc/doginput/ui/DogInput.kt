package com.sidgowda.pawcalc.doginput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputMode
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.model.DogInputUnit
import com.sidgowda.pawcalc.ui.component.EmptyCameraLogo
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme


@Composable
fun DogInput(
    modifier: Modifier = Modifier,
    dogInputState: DogInputState,
    dogInputMode: DogInputMode,
    unit: DogInputUnit = DogInputUnit.IMPERIAL,
    handleEvent: (event: DogInputEvent) -> Unit,
    onSaveDog: () -> Unit
) {
   DogInputScreen(
       modifier = modifier,
       dogInputState = dogInputState,
       dogInputMode = dogInputMode,
       dogInputUnit = unit,
       onPictureChanged = { pictureUrl ->
           handleEvent(DogInputEvent.PicChanged(pictureUrl))
       },
       onNameChanged = { name ->
           handleEvent(DogInputEvent.NameChanged(name))
       },
       onWeightChanged = { weight ->
           handleEvent(DogInputEvent.WeightChanged(weight))
       },
       onBirthDateChanged = { date ->
           handleEvent(DogInputEvent.BirthDateChanged(date))
       },
       onSaveDog = onSaveDog
   )
}

@Composable
internal fun DogInputScreen(
    modifier: Modifier = Modifier,
    dogInputState: DogInputState,
    dogInputMode: DogInputMode,
    dogInputUnit: DogInputUnit = DogInputUnit.IMPERIAL,
    onPictureChanged: (picUrl: String) -> Unit,
    onNameChanged: (name: String) -> Unit,
    onWeightChanged: (weight: String) -> Unit,
    onBirthDateChanged: (date: String) -> Unit,
    onSaveDog: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(PawCalcTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        val weightFocusRequester = FocusRequester()
        val birthDateFocusRequester = FocusRequester()
        Column(
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                    horizontal = 48.dp
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(2.dp))
            CameraInput()
            NameInput(
                name = dogInputState.name,
                onNameChanged = onNameChanged,
                weightFocusRequester = weightFocusRequester
            )
            WeightInput(
                weight = dogInputState.weight,
                onWeightChanged = onWeightChanged,
                weightFocusRequester = weightFocusRequester,
                birthDateFocusRequester = birthDateFocusRequester
            )
            BirthDateInput(
                date = dogInputState.birthDate,
                birthDateFocusRequester = birthDateFocusRequester
            )
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
                            text = stringResource(id = R.string.save_input),
                            style = PawCalcTheme.typography.h3,
                            color = PawCalcTheme.colors.onPrimary
                        )
                    }
                )
            }
        }
    }
}

@Composable
internal fun CameraInput(modifier: Modifier = Modifier) {
    EmptyCameraLogo()
}

@Composable
internal fun NameInput(
    modifier: Modifier = Modifier,
    name: String,
    onNameChanged: (name: String) -> Unit,
    weightFocusRequester: FocusRequester
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.name_text_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(52.dp),
            value = name,
            onValueChange = {
                onNameChanged(it)
            },
            textStyle = PawCalcTheme.typography.h5,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = PawCalcTheme.colors.surface,
                textColor = PawCalcTheme.colors.onSurface
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    weightFocusRequester.requestFocus()
                }
            )
        )
    }
}

@Composable
internal fun WeightInput(
    modifier: Modifier = Modifier,
    weight: String,
    onWeightChanged: (weight: String) -> Unit,
    weightFocusRequester: FocusRequester,
    birthDateFocusRequester: FocusRequester
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.weight_text_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(.6f)
                .height(52.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = weight,
                onValueChange = {
                    onWeightChanged(it)
                },
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .focusRequester(weightFocusRequester),
                textStyle = PawCalcTheme.typography.h5,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = PawCalcTheme.colors.surface,
                    textColor = PawCalcTheme.colors.onSurface
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Decimal
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        birthDateFocusRequester.requestFocus()
                    }
                )
            )
            GreyBox(
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Text(
                    "lb",
                    style = PawCalcTheme.typography.h5,
                    color = PawCalcTheme.colors.onBackground
                )
            }
        }
    }
}

@Composable
internal fun BirthDateInput(
    modifier: Modifier = Modifier,
    date: String,
    birthDateFocusRequester: FocusRequester
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.birth_date_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(.6f)
                .height(52.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            TextField(
                value = "",
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .focusRequester(birthDateFocusRequester),
                textStyle = PawCalcTheme.typography.h5,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = PawCalcTheme.colors.surface,
                    textColor = PawCalcTheme.colors.onSurface
                )
            )
            GreyBox(
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = ""
                )
            }
        }
    }
}

@Composable
internal fun GreyBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(52.dp)
            .width(42.dp)
            .background(
                color = Grey200,
                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

//-----Preview--------------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewNewDogScreen() {
    PawCalcTheme {
        DogInputScreen(
            modifier = Modifier.fillMaxSize(),
            dogInputState = DogInputState(),
            dogInputMode = DogInputMode.NEW_DOG,
            onSaveDog = {},
            onWeightChanged = {},
            onNameChanged = {},
            onPictureChanged = {},
            onBirthDateChanged = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewEditDogScreen() {
    PawCalcTheme {
        DogInputScreen(
            modifier = Modifier.fillMaxSize(),
            dogInputState = DogInputState(),
            dogInputMode = DogInputMode.EDIT_DOG,
            onSaveDog = {},
            onWeightChanged = {},
            onNameChanged = {},
            onPictureChanged = {},
            onBirthDateChanged = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewNameInput() {
    PawCalcTheme {
        Column(Modifier.fillMaxWidth()) {
            NameInput(
                name = "Mowgli",
                onNameChanged = {},
                weightFocusRequester = FocusRequester()
            )
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewWeightInput() {
    PawCalcTheme {
        Column(Modifier.fillMaxWidth()) {
            WeightInput(
                weight = "87.0",
                onWeightChanged = {},
                weightFocusRequester = FocusRequester(),
                birthDateFocusRequester = FocusRequester()
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
                date = "07/30/2019",
                birthDateFocusRequester = FocusRequester()
            )
        }
    }
}
