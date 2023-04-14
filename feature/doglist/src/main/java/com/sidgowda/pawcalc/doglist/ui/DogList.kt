package com.sidgowda.pawcalc.doglist.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingProgress
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.navigation.ONBOARDING_SCREEN_ROUTE
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun DogList(
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    onNavigateToOnboarding: () -> Unit,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    val viewModel: DogListViewModel = hiltViewModel()
    val context = LocalContext.current
    val onboardingProgress: OnboardingProgress =
        savedStateHandle.getLiveData<OnboardingProgress>(ONBOARDING_SCREEN_ROUTE)
            .observeAsState().value ?: OnboardingProgress.NotStarted
    var isOnboarded by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.onboardingState.collect { onboardingState ->
            when (onboardingState) {
                OnboardingState.Onboarded -> {
                    isOnboarded = true
                }
                OnboardingState.NotOnboarded -> {
                    when (onboardingProgress) {
                        OnboardingProgress.NotStarted -> {
                            onNavigateToOnboarding()
                        }
                        OnboardingProgress.Cancelled -> {
                            val activity = context.findActivity()
                            activity.finish()
                        }
                    }
                }
            }
        }
    }
    if (isOnboarded) {
        DogListScreen(
            modifier = modifier.fillMaxSize(),
            viewModel = viewModel,
            onNewDog = onNewDog,
            onDogDetails = onDogDetails
        )
    }
}

@Composable
internal fun DogListScreen(
    modifier: Modifier = Modifier,
    viewModel: DogListViewModel,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        // fetch dogs to get most recent updates
        viewModel.fetchDogs()
    }
    val dogListState: DogListState by viewModel.dogListState.collectAsStateWithLifecycle()
    val lazyColumnState = rememberLazyListState()
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            IconButton(onClick = onNewDog) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            when {
                dogListState.isLoading -> {
                    // todo - use accompanist library to show shimmer
                    CircularProgressIndicator()
                }
                else -> {
                    if (dogListState.dogs.isEmpty()) {
                        //show empty state
                        Text("You have not added any dogs currently. Please add a dog to see how old they are in human years")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = lazyColumnState
                        ) {
                            items(dogListState.dogs, key = { dog -> dog.id }) {
                                DogListItem(dog = it)
                            }
                        }
                    }
                    if (dogListState.isError) {
                        // show error dialog
                    }
                }
            }
        }
    }
}


@Composable
internal fun DogListItem(
    modifier: Modifier = Modifier,
    dog: Dog
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = dog.profilePic,
            modifier = Modifier.size(30.dp),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Column(modifier = Modifier.fillMaxHeight()) {
            Text(dog.name)
            Text(dog.weight.toString())
            Text(dog.birthDate)
        }
    }
}

//-----Preview--------------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewDogListScreen() {
    PawCalcTheme {
        DogListScreen(viewModel = hiltViewModel(), onNewDog = { }, onDogDetails = {})
    }
}

@LightDarkPreview
@Composable
fun PreviewDogListEmptyState() {

}

@LightDarkPreview
@Composable
fun PreviewDogListFullList() {

}

@LightDarkPreview
@Composable
fun PreviewDogListItem() {

}
