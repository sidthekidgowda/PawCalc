package com.sidgowda.pawcalc.newdog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sidgowda.pawcalc.newdog.navigation.NEW_DOG_SCREEN_ROUTE
import com.sidgowda.pawcalc.newdog.ui.NewDogViewModel
import com.sidgowda.pawcalc.ui.component.EmptyCameraLogo
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
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
        modifier = modifier
            .testTag(NEW_DOG_SCREEN_ROUTE)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(PawCalcTheme.colors.background)
            .padding(vertical = 16.dp, horizontal = 48.dp),
        verticalArrangement = Arrangement.spacedBy(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmptyCameraLogo()
        NameInput(name = viewModel.name)
        WeightInput(weight = viewModel.weight)
        BirthDateInput(date = viewModel.date)
        Column {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(12.dp))
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

@Composable
fun NameInput(
    modifier: Modifier = Modifier,
    name: String
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
