package com.sidgowda.pawcalc.doginput

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputMode
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.model.DogInputUnit
import com.sidgowda.pawcalc.doginput.ui.*
import com.sidgowda.pawcalc.ui.component.EmptyDogPictureWithCamera
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DogInput(
    modifier: Modifier = Modifier,
    dogInputState: DogInputState,
    dogInputMode: DogInputMode,
    unit: DogInputUnit = DogInputUnit.IMPERIAL,
    handleEvent: (event: DogInputEvent) -> Unit,
    onSaveDog: () -> Unit
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val scope = rememberCoroutineScope()
    var isCameraRequested by remember {
        mutableStateOf(false)
    }
    var isMediaRequested by remember {
        mutableStateOf(false)
    }
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val mediaPermission = rememberPermissionState(permission = mediaPermission())
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetContent = {
            UpdatePhotoBottomSheetContent(
                onTakePhoto = {
                  // take photo from Camera
                  isCameraRequested = true
                },
                onChooseMedia = {
                    // choose photos from media
                    isMediaRequested = true
                },
                onCancel = {
                    scope.launch { bottomSheetState.hide() }
                }
            )
        }
    ) {
        DogInputScreen(
            modifier = modifier,
            bottomSheetState = bottomSheetState,
            coroutineScope = scope,
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
        val requestPermission = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }
        val activity = (context as ComponentActivity)
        val cameraIntent = {
            activity.startActivity(
                Intent(
                    activity,
                    CameraActivity::class.java
                )
            )
            isCameraRequested = false
        }
        if (isCameraRequested) {
            HandlePermission(
                permissionStatus = cameraPermission.status,
                firstTimeRequest = {
                    cameraPermission.launchPermissionRequest()
                },
                successContent = {
                    LaunchedEffect(Unit) {
                        cameraIntent()
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }
                },
                deniedContent = {
                    PermissionDialog(
                        permission = cameraPermission.permission,
                        requestPermission = requestPermission,
                        onCancel = { isCameraRequested = false }
                    )
                }
            )
        }
        if (isMediaRequested) {
            HandlePermission(
                permissionStatus = mediaPermission.status,
                firstTimeRequest = {
                    mediaPermission.launchPermissionRequest()
                },
                successContent = {
                    OpenMedia()
                    LaunchedEffect(Unit) {
                        scope.launch {
                            bottomSheetState.hide()
                        }
                    }
                },
                deniedContent = {
                    PermissionDialog(
                        permission = mediaPermission.permission,
                        requestPermission = requestPermission,
                        onCancel = { isMediaRequested = false }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun DogInputScreen(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    dogInputState: DogInputState,
    dogInputMode: DogInputMode,
    dogInputUnit: DogInputUnit = DogInputUnit.IMPERIAL,
    onPictureChanged: (picUrl: String) -> Unit,
    onNameChanged: (name: String) -> Unit,
    onWeightChanged: (weight: String) -> Unit,
    onBirthDateChanged: (date: String) -> Unit,
    onSaveDog: () -> Unit,
) {
    val weightFocusRequester = FocusRequester()
    val birthDateFocusRequester = FocusRequester()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(PawCalcTheme.colors.background)
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CameraInput(
            bottomSheetState = bottomSheetState,
            coroutineScope = coroutineScope
        )
        NameInput(
            modifier = Modifier.padding(horizontal = 48.dp),
            name = dogInputState.name,
            onNameChanged = onNameChanged,
            weightFocusRequester = weightFocusRequester
        )
        WeightInput(
            modifier = Modifier.padding(horizontal = 48.dp),
            weight = dogInputState.weight,
            onWeightChanged = onWeightChanged,
            weightFocusRequester = weightFocusRequester,
            birthDateFocusRequester = birthDateFocusRequester
        )
        BirthDateInput(
            modifier = Modifier.padding(horizontal = 48.dp),
            date = dogInputState.birthDate,
            birthDateFocusRequester = birthDateFocusRequester
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            PawCalcButton(
                enabled = false,
                text = stringResource(id = R.string.save_input),
                onClick = onSaveDog
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CameraInput(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope
) {
    EmptyDogPictureWithCamera(
        modifier = modifier.clickable {
            // open bottom sheet
            if (!bottomSheetState.isVisible) {
                coroutineScope.launch {
                    bottomSheetState.show()
                }
            }
        }
    )
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
    // todo validations to ensure weight does not have leading zero
    // or more than 500lb
    val isError = weight.isNotEmpty() && weight.length > 4
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
                .height(60.dp),
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
                ),
                isError = isError,
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
        if (isError) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Weight should be between 1 and 500 lb",
                style = PawCalcTheme.typography.error,
                color = MaterialTheme.colors.error
            )
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
            .height(60.dp)
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
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
            onBirthDateChanged = {},
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
            coroutineScope = rememberCoroutineScope()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
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
            onBirthDateChanged = {},
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
            coroutineScope = rememberCoroutineScope()
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
fun PreviewWeightInputError() {
    PawCalcTheme {
        Column(Modifier.fillMaxWidth()) {
            WeightInput(
                weight = "875.0",
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
