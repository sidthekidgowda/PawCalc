package com.sidgowda.pawcalc.doginput.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.doginput.model.DogInputMode
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun DogInputScreen(
    modifier: Modifier = Modifier,
    dogInputMode: DogInputMode,
    onSaveDog: () -> Unit
) {

}

@Composable
fun NameInput(
    modifier: Modifier = Modifier,
    name: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "",
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(52.dp),
            value = name,
            onValueChange = {},
            textStyle = PawCalcTheme.typography.h5,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = PawCalcTheme.colors.surface,
                textColor = PawCalcTheme.colors.onSurface
            )
        )
    }
}

@Composable
fun WeightInput(
    modifier: Modifier = Modifier,
    weight: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "",
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
                modifier = Modifier.fillMaxWidth(.9f),
                textStyle = PawCalcTheme.typography.h5,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = PawCalcTheme.colors.surface,
                    textColor = PawCalcTheme.colors.onSurface
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
fun BirthDateInput(
    modifier: Modifier = Modifier,
    date: String
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "",
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
                modifier = Modifier.fillMaxWidth(.9f),
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
fun GreyBox(
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
            dogInputMode = DogInputMode.NEW_DOG,
            onSaveDog = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewEditDogScreen() {
    PawCalcTheme {
        DogInputScreen(
            modifier = Modifier.fillMaxSize(),
            dogInputMode = DogInputMode.EDIT_DOG,
            onSaveDog = {}
        )
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
