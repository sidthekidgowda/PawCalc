package com.sidgowda.pawcalc.dogdetails.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.formattedToString
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsEvent
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsState
import com.sidgowda.pawcalc.dogdetails.model.NavigateEvent
import com.sidgowda.pawcalc.ui.component.EmptyDogPictureWithCamera
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
internal fun DogDetails(
    onEditDog: (Int) -> Unit
) {
    val viewModel = hiltViewModel<DogDetailsViewModel>()

    val dogDetailsState by viewModel.dogDetailsState.collectAsStateWithLifecycle()
    dogDetailsState.navigateEvent?.let { navigateEvent ->
        LaunchedEffect(key1 = navigateEvent) {
            when (navigateEvent) {
                is NavigateEvent.EditDog -> {
                    onEditDog(navigateEvent.id)
                }
            }
            viewModel.handleEvent(DogDetailsEvent.OnNavigated)
        }
    }
    DogDetailsScreen(
        modifier = Modifier.fillMaxSize(),
        dogDetailsState = dogDetailsState,
        handleEvent = viewModel::handleEvent
    )
}


@Composable
internal fun DogDetailsScreen(
    modifier: Modifier = Modifier,
    dogDetailsState: DogDetailsState,
    handleEvent: (event: DogDetailsEvent) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PawCalcTheme.colors.surface),
        contentAlignment = Alignment.Center
    ) {
        when {
            dogDetailsState.isLoading -> {
                CircularProgressIndicator()
                handleEvent(DogDetailsEvent.FetchDogForId)
            }
            else -> {
                DogDetailsContent(
                    modifier = Modifier.fillMaxSize(),
                    dog = dogDetailsState.dog!!,
                    handleEvent = handleEvent
                )
            }
        }
    }
}

@Composable
internal fun DogDetailsContent(
    modifier: Modifier = Modifier,
    dog: Dog,
    handleEvent: (event: DogDetailsEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EditButton(
            onEditDog = {
                handleEvent(DogDetailsEvent.EditDog)
            }
        )
        ProfilePic(image = dog.profilePic)
        Spacer(modifier = Modifier.height(20.dp))
        DogName(name = dog.name)
        Spacer(modifier = Modifier.height(20.dp))
        DogWeight(weight = dog.weight, weightFormat = dog.weightFormat)
        Spacer(modifier = Modifier.height(20.dp))
        BirthDate(birthDate = dog.birthDate)
        Spacer(modifier = Modifier.height(20.dp))
        DogYears(dogYears = dog.dogYears)
        Spacer(modifier = Modifier.height(20.dp))
        HumanYears(humanYears = dog.humanYears)
    }
}

@Composable
fun ColumnScope.EditButton(
    modifier: Modifier = Modifier,
    onEditDog: () -> Unit
) {
    TextButton(
        modifier = modifier.align(Alignment.End),
        onClick = onEditDog
    ) {
        Text("Edit")
    }
}

@Composable
internal fun ProfilePic(
    modifier: Modifier = Modifier,
    image: Uri
) {
    EmptyDogPictureWithCamera()
//    AsyncImage(
//        model = ImageRequest.Builder(LocalContext.current).data(image).build(),
//        modifier = Modifier
//            .clip(CircleShape)
//            .border(2.dp, PawCalcTheme.colors.onPrimarySurface(), CircleShape),
//        contentScale = ContentScale.Crop,
//        contentDescription = null
//    )
}

@Composable
internal fun TextContent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(modifier = modifier) {
        content()
    }
}

@Composable
internal fun DogName(
    modifier: Modifier = Modifier,
    name: String
) {
    TextContent(modifier = modifier) {
        Text(name)
    }
}

@Composable
internal fun DogWeight(
    modifier: Modifier = Modifier,
    weight: Double,
    weightFormat: WeightFormat
) {
    TextContent(modifier = modifier) {
        Text("Weight: ${weight.formattedToString()} ${if (weightFormat == WeightFormat.POUNDS) "lb" else "kg"}")
    }
}

@Composable
internal fun BirthDate(
    modifier: Modifier = Modifier,
    birthDate: String
) {
    TextContent(modifier = modifier) {
        Text("Birth Date: $birthDate")
    }
}

@Composable
fun DogYears(
    modifier: Modifier = Modifier,
    dogYears: Age
) {
    TextContent(modifier = modifier) {
        Text("Dog Years: ${dogYears.years} years ${dogYears.months} months ${dogYears.days} days")
    }
}

@Composable
fun HumanYears(
    modifier: Modifier = Modifier,
    humanYears: Age
) {

}
//--------Preview-----------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewDogDetailsLoading() {
    PawCalcTheme {
        DogDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            dogDetailsState = DogDetailsState(isLoading = true),
            handleEvent = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewDogDetailsContent() {
    PawCalcTheme {
        DogDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            dogDetailsState = DogDetailsState(
                isLoading = false,
                dog = Dog(
                    id = 1,
                    profilePic = Uri.parse(""),
                    name = "Mowgli",
                    weight = 80.0,
                    weightFormat = WeightFormat.POUNDS,
                    birthDate = "7/30/2019",
                    dogYears = "7/30/2019".toDogYears(),
                    dateFormat = DateFormat.AMERICAN,
                    humanYears = "7/30/2019".toHumanYears()
                ),
                navigateEvent = null
            ),
            handleEvent = {}
        )
    }
}
