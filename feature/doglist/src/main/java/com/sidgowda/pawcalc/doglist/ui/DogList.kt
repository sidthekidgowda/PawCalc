package com.sidgowda.pawcalc.doglist.ui

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingProgress
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.navigation.ONBOARDING_SCREEN_ROUTE
import com.sidgowda.pawcalc.ui.R
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun DogList(
    modifier: Modifier = Modifier,
    savedStateHandle: SavedStateHandle,
    onNavigateToOnboarding: () -> Unit,
    onNewDog: () -> Unit,
    onEditDog: (Int) -> Unit,
    onDogDetails: () -> Unit
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
            onEditDog = onEditDog,
            onDogDetails = onDogDetails,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DogListScreen(
    modifier: Modifier = Modifier,
    viewModel: DogListViewModel,
    onNewDog: () -> Unit,
    onEditDog: (Int) -> Unit,
    onDogDetails: () -> Unit
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
            FloatingActionButton(
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp).size(64.dp),
                onClick = onNewDog
            ) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Default.Add,
                    tint = Color.Black,
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
//                    CircularProgressIndicator()
                }
                else -> {
                    if (dogListState.dogs.isEmpty()) {
                        //show empty state
                        //todo update
                        Text("You have not added any dogs currently. Please add a dog to see how old they are in human years")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = lazyColumnState
                        ) {
                            items(dogListState.dogs, key = { dog -> dog.id }) { dog ->
                                var isDogItemDismissed by remember {
                                    mutableStateOf(false)
                                }
                                val dogItemHeightAnimation by animateDpAsState(
                                    targetValue = if (!isDogItemDismissed) 120.dp else 0.dp,
                                    animationSpec = tween(delayMillis = 300),
                                    finishedListener = {
                                        viewModel.deleteDog(dog)
                                    }
                                )
                                var dismissState = rememberDismissState(
                                    confirmStateChange = {
                                        if (it == DismissValue.DismissedToEnd) {
                                            isDogItemDismissed = true
                                        }
                                        true
                                    }
                                )
                                SwipeToDismiss(
                                    directions = setOf(DismissDirection.StartToEnd),
                                    state = dismissState,
                                    dismissThresholds = {
                                        FractionalThreshold(.10f)
                                    },
                                    background = {
                                        DogListItemBackground(
                                            modifier = Modifier
                                                .height(dogItemHeightAnimation)
                                                .fillMaxWidth(),
                                            dismissState = dismissState
                                        )
                                    },
                                    dismissContent = {
                                        DogListItem(
                                            modifier = Modifier
                                                .height(dogItemHeightAnimation)
                                                .fillMaxWidth()
                                                .clickable {
                                                    onEditDog(dog.id)
                                                },
                                            dog = dog
                                        )
                                    }
                                )
                                val dividerVisibilityAnimation by animateFloatAsState(
                                    targetValue = if (dismissState.targetValue == DismissValue.Default) {
                                        1.0f
                                    } else 0f,
                                    animationSpec = tween(delayMillis = 300)
                                )
                                Divider(
                                    modifier = Modifier.alpha(dividerVisibilityAnimation),
                                    color = PawCalcTheme.colors.onBackground
                                )
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
    Card(
        modifier = modifier,
        shape = RectangleShape
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp)
        ) {
            val (image, name, birthdate, weight, dogYears, humanYears) = createRefs()
            AsyncImage(
                model = dog.profilePic,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
                    .constrainAs(image) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, top = 12.dp, end = 16.dp)
                    .constrainAs(name) {
                        start.linkTo(image.end)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                text = dog.name,
                textAlign = TextAlign.Start,
                style = PawCalcTheme.typography.h4,
                color = PawCalcTheme.colors.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconText(
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                constraintName = weight,
                constrainBlock = {
                    start.linkTo(image.end)
                    top.linkTo(name.bottom)
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Scale,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = "${dog.weight} lbs"
                    )
                }
            )
            IconText(
                modifier = Modifier.padding(end = 20.dp, top = 10.dp),
                constraintName = birthdate,
                constrainBlock = {
                    start.linkTo(humanYears.start)
                    top.linkTo(name.bottom)
                    baseline.linkTo(weight.baseline)
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = dog.birthDate
                    )
                }
            )
            IconText(
                modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                constraintName = dogYears,
                constrainBlock = {
                    start.linkTo(image.end)
                    top.linkTo(birthdate.bottom)
                },
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = "3y 6m 20d"
                    )
                }
            )
            IconText(
                modifier = Modifier.padding(end = 20.dp, top = 10.dp),
                constraintName = humanYears,
                constrainBlock = {
                    end.linkTo(parent.end)
                    top.linkTo(birthdate.bottom)
                    baseline.linkTo(dogYears.baseline)
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = "28y 4m 20d"
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DogListItemBackground(
    modifier: Modifier = Modifier,
    dismissState: DismissState
) {
    val backgroundColor by animateColorAsState(
        targetValue = when(dismissState.targetValue) {
            DismissValue.DismissedToEnd ->  MaterialTheme.colors.error
            else -> PawCalcTheme.colors.surface
        },
        animationSpec = tween()
    )
    val iconColor by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            DismissValue.DismissedToEnd -> MaterialTheme.colors.onError
            else -> MaterialTheme.colors.onSurface
        },
        animationSpec = tween()
    )
    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) {
            1f
        } else .75f
    )

    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = 12.dp)
    ) {
        if (dismissState.currentValue == DismissValue.Default) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .scale(scale),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete dog",
                tint = iconColor
            )
        }
    }
}

@Composable
internal fun ConstraintLayoutScope.IconText(
    modifier: Modifier = Modifier,
    constraintName: ConstrainedLayoutReference,
    constrainBlock: ConstrainScope.() -> Unit,
    icon: @Composable () -> Unit,
    text: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .constrainAs(
                ref = constraintName,
                constrainBlock = constrainBlock
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        text()
    }
}

//-----Preview--------------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewDogListItemNotLoading() {
    PawCalcTheme {
        DogListItem(
            dog = Dog(
                id = 0,
                name = "Mowgli",
                weight = 80.0,
                birthDate = "7/30/2019",
                profilePic = Uri.EMPTY,
                dogYears = "",
                humanYears = "",
                isLoading = false
            )
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewDogListItemLoading() {
    PawCalcTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
                .background(PawCalcTheme.colors.surface())
                .padding(start = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_puppy),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp)
                    .border(2.dp, PawCalcTheme.colors.onPrimarySurface(), CircleShape),
                contentDescription = null
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .padding(10.dp)
                    .weight(1.0f)
            ) {
                Text("Mowgli")
                Text("87 lb")
                Text("7/30/2019")
                Text("3 years 10 months 20 days")
                Text("28 years 10 months 20 days")
            }
        }
    }
}

