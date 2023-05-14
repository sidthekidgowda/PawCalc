package com.sidgowda.pawcalc.dogdetails.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.dogs.model.formattedToString
import com.sidgowda.pawcalc.dogdetails.R
import com.sidgowda.pawcalc.dogdetails.model.DogDetailsEvent
import com.sidgowda.pawcalc.dogdetails.model.NavigateEvent
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
                    onEditDog(navigateEvent.dogId)
                }
            }
            viewModel.handleEvent(DogDetailsEvent.OnNavigated)
        }
    }
    dogDetailsState.dog?.let { dog ->
        DogDetailsScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(PawCalcTheme.colors.background),
            dog = dog,
            handleEvent = viewModel::handleEvent
        )
    }
}

@Composable
internal fun DogDetailsScreen(
    modifier: Modifier = Modifier,
    dog: Dog,
    handleEvent: (event: DogDetailsEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .background(PawCalcTheme.colors.background)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfilePicWithEditButton(
            image = dog.profilePic,
            onEditDog = {
                handleEvent(DogDetailsEvent.EditDog)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        DogName(name = dog.name)
        Spacer(modifier = Modifier.height(10.dp))
        DogWeight(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp),
            weight = dog.weight,
            weightFormat = dog.weightFormat
        )
        Spacer(modifier = Modifier.height(10.dp))
        BirthDate(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp),
            birthDate = dog.birthDate
        )
        Spacer(modifier = Modifier.height(10.dp))
        DogYears(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp),
            dogYears = dog.dogYears
        )
        Spacer(modifier = Modifier.height(10.dp))
        HumanYears(
            humanYears = dog.humanYears,
            shouldAnimate = dog.shouldAnimate,
            onAnimationFinished = {
                handleEvent(DogDetailsEvent.OnAnimationFinished)
            }
        )
    }
}

@Composable
fun ProfilePicWithEditButton(
    modifier: Modifier = Modifier,
    image: Uri,
    onEditDog: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (profilePic, editButton) = createRefs()
        ProfilePic(
            modifier = Modifier
                .constrainAs(profilePic) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
                .padding(top = 26.dp),
            image = image
        )
        EditButton(
            modifier = Modifier.constrainAs(editButton) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            onEditDog = onEditDog
        )
    }
}


@Composable
internal fun EditButton(
    modifier: Modifier = Modifier,
    onEditDog: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onEditDog
    ) {
        Text(
            stringResource(id = R.string.edit_dog_details),
            style = PawCalcTheme.typography.h4.copy(fontSize = 14.sp)
        )
    }
}

@Composable
internal fun ProfilePic(
    modifier: Modifier = Modifier,
    image: Uri
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(image).build(),
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
            .border(2.dp, PawCalcTheme.colors.onPrimarySurface(), CircleShape),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

@Composable
internal fun DogName(
    modifier: Modifier = Modifier,
    name: String
) {
    Text(
        modifier = modifier,
        text = name,
        style = PawCalcTheme.typography.h4,
        color = PawCalcTheme.colors.contentColor()
    )
}

@Composable
internal fun DogWeight(
    modifier: Modifier = Modifier,
    weight: Double,
    weightFormat: WeightFormat
) {
    val weightUnit = if (weightFormat == WeightFormat.POUNDS) "lb" else "kg"
    val weightAsText =
        stringResource(id = R.string.weight_dog_details, weight.formattedToString(), weightUnit)
    Text(
        modifier = modifier,
        text = weightAsText,
        style = PawCalcTheme.typography.body1,
        color = PawCalcTheme.colors.contentColor()
    )
}

@Composable
internal fun BirthDate(
    modifier: Modifier = Modifier,
    birthDate: String
) {
    Text(
        modifier = modifier,
        text = stringResource(id = R.string.birth_date_dog_details, birthDate),
        style = PawCalcTheme.typography.body1,
        color = PawCalcTheme.colors.contentColor()
    )
}

@Composable
internal fun DogYears(
    modifier: Modifier = Modifier,
    dogYears: Age
) {
    Text(
        modifier = modifier,
        text = stringResource(
            id = R.string.dog_years_dog_details,
            dogYears.years,
            dogYears.months,
            dogYears.days
        ),
        style = PawCalcTheme.typography.body1,
        color = PawCalcTheme.colors.contentColor()
    )
}

@Composable
internal fun HumanYears(
    modifier: Modifier = Modifier,
    humanYears: Age,
    shouldAnimate: Boolean,
    onAnimationFinished: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(
                id = R.string.human_years_dog_details
            ),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        HumanYearsChartWithLegend(
            age = humanYears,
            shouldAnimate = shouldAnimate,
            onAnimationFinished = onAnimationFinished
        )
    }
}
//--------Preview-----------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewDogDetailsContent() {
    PawCalcTheme {
        DogDetailsScreen(
            modifier = Modifier.fillMaxSize(),
            dog = Dog(
                id = 1,
                profilePic = Uri.parse(""),
                name = "Mowgli",
                weight = 80.0,
                weightFormat = WeightFormat.POUNDS,
                birthDate = "7/30/2019",
                dogYears = "7/30/2019".toDogYears(),
                dateFormat = DateFormat.AMERICAN,
                humanYears = "7/30/2019".toHumanYears(),
                shouldAnimate = false
            ),
            handleEvent = {}
        )
    }
}
