package com.sidgowda.pawcalc.doglist.ui

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.toAccessibilityText
import com.sidgowda.pawcalc.data.date.toDogYears
import com.sidgowda.pawcalc.data.date.toHumanYears
import com.sidgowda.pawcalc.data.date.toText
import com.sidgowda.pawcalc.data.dogs.model.Dog
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingProgress
import com.sidgowda.pawcalc.data.onboarding.model.OnboardingState
import com.sidgowda.pawcalc.doglist.R
import com.sidgowda.pawcalc.doglist.model.DogListEvent
import com.sidgowda.pawcalc.doglist.model.DogListState
import com.sidgowda.pawcalc.doglist.model.NavigateEvent
import com.sidgowda.pawcalc.navigation.DOG_LIST_SCREEN_ROUTE
import com.sidgowda.pawcalc.navigation.ONBOARDING_SCREEN_ROUTE
import com.sidgowda.pawcalc.test.TestTags
import com.sidgowda.pawcalc.test.TestTags.DogList.TAG_DOG_LIST_CONTENT
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import timber.log.Timber
import com.sidgowda.pawcalc.ui.R as UiR

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
                    Timber.tag("DogList").d("User has been onboarded")
                    isOnboarded = true
                }
                OnboardingState.NotOnboarded -> {
                    when (onboardingProgress) {
                        OnboardingProgress.NotStarted -> {
                            Timber.tag("DogList").d("User has not onboarded yet. Navigating to onboarding")
                            onNavigateToOnboarding()
                        }
                        OnboardingProgress.Cancelled -> {
                            Timber.tag("DogList").d("User did not successfully onboard yet")
                            val activity = context.findActivity()
                            activity.finish()
                        }
                    }
                }
            }
        }
    }
    if (isOnboarded) {
        OnboardedDogList(
            modifier = modifier.fillMaxSize(),
            viewModel = viewModel,
            onNewDog = onNewDog,
            onDogDetails = onDogDetails
        )
    }
}

@Composable
internal fun OnboardedDogList(
    modifier: Modifier = Modifier,
    viewModel: DogListViewModel,
    onNewDog: () -> Unit,
    onDogDetails: (Int) -> Unit
) {
    val dogListState: DogListState by viewModel.dogListState.collectAsStateWithLifecycle()
    dogListState.navigateEvent?.let { navigateEvent ->
        LaunchedEffect(key1 = navigateEvent) {
            when (navigateEvent) {
                is NavigateEvent.DogDetails -> {
                    Timber.tag("DogList").d("Navigating to Dog Details for dog with id: ${navigateEvent.id}")
                    onDogDetails(navigateEvent.id)
                }
                NavigateEvent.AddDog -> {
                    Timber.tag("DogList").d("Navigating to New Dog")
                    onNewDog()
                }
            }
            Timber.tag("DogList").d("Navigation event has been handled")
            viewModel.handleEvent(DogListEvent.OnNavigated)
        }
    }
    DogListScreen(
        modifier = modifier.testTag(DOG_LIST_SCREEN_ROUTE),
        dogListState = dogListState,
        handleEvent = viewModel::handleEvent
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DogListScreen(
    modifier: Modifier = Modifier,
    dogListState: DogListState,
    handleEvent: (event: DogListEvent) -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .testTag(TestTags.DogList.TAG_ADD_DOG_BUTTON)
                    .padding(end = 8.dp, bottom = 8.dp)
                    .size(64.dp),
                onClick = {
                    handleEvent(DogListEvent.AddDog)
                }
            ) {
                Icon(
                    modifier = Modifier.size(34.dp),
                    imageVector = Icons.Default.Add,
                    tint = Color.Black,
                    contentDescription = stringResource(id = R.string.cd_dog_list_navigate_new_dog)
                )
            }
        }
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                dogListState.isLoading -> {
                    Shimmer(contentPadding = contentPadding)
                }
                else -> {
                    if (dogListState.dogs.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.dog_list_empty),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            style = PawCalcTheme.typography.h5.copy(fontSize = 30.sp),
                            color = PawCalcTheme.colors.onSurface,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val lazyColumnState = rememberLazyListState()
                        val deleteDogAccessibilityLabel = stringResource(id = R.string.cd_delete_dog)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag(TAG_DOG_LIST_CONTENT),
                            contentPadding = contentPadding,
                            state = lazyColumnState
                        ) {
                            // Make sure ids are unique since these dogs can be added and removed
                            // Ex: if there are 4 dogs and we remove dog of index 2 with id = 3
                            // and add a new dog. Ensure next dog added will have id = 5 since
                            // the previous item will have id = 4 with index = 2 now.
                            items(dogListState.dogs, key = { dog -> dog.id }) { dog ->
                                var isDogItemDismissed by remember {
                                    mutableStateOf(false)
                                }
                                val dogItemHeightAnimation by animateDpAsState(
                                    targetValue = if (!isDogItemDismissed) 120.dp else 0.dp,
                                    animationSpec = tween(delayMillis = 300),
                                    finishedListener = {
                                        Timber.tag("DogList").d("Deleting dog with id: ${dog.id}")
                                        handleEvent(DogListEvent.DeleteDog(dog))
                                    }
                                )
                                val dismissState = rememberDismissState(
                                    confirmStateChange = {
                                        if (it == DismissValue.DismissedToEnd) {
                                            isDogItemDismissed = true
                                        }
                                        true
                                    }
                                )
                                SwipeToDismiss(
                                    modifier = Modifier.semantics {
                                        customActions = listOf(
                                            CustomAccessibilityAction(deleteDogAccessibilityLabel) {
                                                handleEvent(DogListEvent.DeleteDog(dog))
                                                true
                                            }
                                        )
                                    },
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
                                                    Timber.tag("DogList")
                                                        .d("Dog with id: ${dog.id} clicked")
                                                    handleEvent(DogListEvent.DogDetails(dog.id))
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
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun Shimmer(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(50) {
            ListItem(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Circle,
                        modifier = Modifier
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                                shape = PawCalcTheme.shapes.mediumRoundedCornerShape.copy(
                                    CornerSize(50)
                                )
                            )
                            .clip(CircleShape)
                            .size(80.dp),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        "",
                        modifier = Modifier
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                            )
                            .height(60.dp)
                            .fillMaxWidth()
                    )
                }
            )
        }
    }
}

@Composable
internal fun DogListItem(
    modifier: Modifier = Modifier,
    dog: Dog
) {
    val dogItemContentDescription = dogContentDescription(dog)
    Card(
        modifier = modifier,
        shape = RectangleShape
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .semantics {
                    contentDescription = dogItemContentDescription
                }
                .padding(start = 16.dp)
        ) {
            val (image, name, birthdate, weight, dogYears, humanYears) = createRefs()
            val endBarrier = createEndBarrier(weight, dogYears)
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
                    .constrainAs(name) {
                        start.linkTo(image.end, 10.dp)
                        top.linkTo(parent.top, 12.dp)
                        end.linkTo(parent.end, 16.dp)
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
                constraintName = weight,
                constrainBlock = {
                    start.linkTo(image.end, 10.dp)
                    top.linkTo(name.bottom, 10.dp)
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
                        text = if (dog.weightFormat == WeightFormat.POUNDS) {
                            stringResource(id = R.string.dog_list_lb, dog.weight)
                        } else {
                            stringResource(id = R.string.dog_list_kg, dog.weight)
                        },
                        style = PawCalcTheme.typography.body3,
                        color = PawCalcTheme.colors.onBackground
                    )
                }
            )
            IconText(
                constraintName = birthdate,
                constrainBlock = {
                    start.linkTo(humanYears.start)
                    top.linkTo(name.bottom, 10.dp)
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
                        text = dog.birthDate,
                        textAlign = TextAlign.Center,
                        style = PawCalcTheme.typography.body3,
                        color = PawCalcTheme.colors.onBackground
                    )
                }
            )
            IconText(
                constraintName = dogYears,
                constrainBlock = {
                    start.linkTo(image.end, 10.dp)
                    top.linkTo(birthdate.bottom, 10.dp)
                },
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(18.dp),
                        imageVector = ImageVector.vectorResource(id = UiR.drawable.ic_paw),
                        contentDescription = null
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = dog.dogYears.toText(),
                        style = PawCalcTheme.typography.body3,
                        color = PawCalcTheme.colors.onBackground
                    )
                }
            )
            IconText(
                constraintName = humanYears,
                constrainBlock = {
                    start.linkTo(endBarrier)
                    top.linkTo(birthdate.bottom, 10.dp)
                    end.linkTo(parent.end)
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
                        modifier = Modifier.padding(start = 2.dp, end = 6.dp),
                        text = dog.humanYears.toText(),
                        style = PawCalcTheme.typography.body3,
                        color = PawCalcTheme.colors.onBackground
                    )
                }
            )
        }
    }
}

@Composable
private fun dogContentDescription(dog: Dog): String {
    val context = LocalContext.current
    val weight = stringResource(id = R.string.cd_dog_weight, dog.weight, dog.weightFormat.toString())
    val birthDate = stringResource(id = R.string.cd_born_on, dog.birthDate)
    val dogYears =
        stringResource(id = R.string.cd_in_dog_years, dog.dogYears.toAccessibilityText(context))
    val humanYears =
        stringResource(id = R.string.cd_in_human_years, dog.humanYears.toAccessibilityText(context))
    return "${dog.name}. $weight. $birthDate. $dogYears. $humanYears"
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
                dogYears = "7/30/2019".toDogYears(),
                humanYears = "7/30/2019".toHumanYears(),
                weightFormat = WeightFormat.POUNDS,
                dateFormat = DateFormat.AMERICAN,
                shouldAnimate = true
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
                painter = painterResource(id = UiR.drawable.dog_puppy),
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

