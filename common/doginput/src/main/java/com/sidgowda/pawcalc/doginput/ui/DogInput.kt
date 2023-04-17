package com.sidgowda.pawcalc.doginput

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.sidgowda.pawcalc.camera.CameraMediaActivity
import com.sidgowda.pawcalc.date.DatePickerDialogFragment
import com.sidgowda.pawcalc.date.DatePickerListener
import com.sidgowda.pawcalc.date.dateToLong
import com.sidgowda.pawcalc.doginput.databinding.DatePickerDialogBinding
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.model.DogInputUnit
import com.sidgowda.pawcalc.doginput.ui.*
import com.sidgowda.pawcalc.ui.component.EmptyDogPictureWithCamera
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.component.PictureWithCameraIcon
import com.sidgowda.pawcalc.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DogInput(
    modifier: Modifier = Modifier,
    dogInputState: DogInputState,
    unit: DogInputUnit = DogInputUnit.IMPERIAL,
    handleEvent: (event: DogInputEvent) -> Unit,
    onSaveDog: () -> Unit
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    )
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var isCameraRequested by remember {
        mutableStateOf(false)
    }
    var isMediaRequested by remember {
        mutableStateOf(false)
    }
    var isDatePickerRequested by remember {
        mutableStateOf(false)
    }
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val mediaPermission = rememberPermissionState(permission = mediaPermission())
    // keep a reference of imageUri since taking a photo from camera and
    // choosing media can be cancelled
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val cameraMediaImageResult = rememberLauncherForActivityResult(
        contract = CameraMediaActivity.GetPhoto(),
        onResult = { uri ->
            if (uri != null) {
                // dismiss bottom sheet only when we update UI with a new picture
                scope.launch { bottomSheetState.hide() }
                imageUri = uri
                // notify listeners image has possibly updated
                handleEvent(DogInputEvent.PicChanged(uri))
            }
        }
    )
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
            scrollState = scrollState,
            bottomSheetState = bottomSheetState,
            coroutineScope = scope,
            dogInputState = dogInputState,
            dogInputUnit = unit,
            onNameChanged = { name ->
                handleEvent(DogInputEvent.NameChanged(name))
            },
            onWeightChanged = { weight ->
                handleEvent(DogInputEvent.WeightChanged(weight))
            },
            onDatePickerRequest = {
                isDatePickerRequested = true
            },
            onSaveDog = {
                handleEvent(DogInputEvent.SavingInfo)
                onSaveDog()
            },
        )
        val requestPermission = {
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }
        if (isCameraRequested) {
            HandlePermission(
                permissionStatus = cameraPermission.status,
                firstTimeRequest = {
                    cameraPermission.launchPermissionRequest()
                },
                successContent = {
                    cameraMediaImageResult.launch(CameraMediaActivity.TAKE_PHOTO)
                    isCameraRequested = false
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
                    cameraMediaImageResult.launch(CameraMediaActivity.CHOOSE_MEDIA)
                    isMediaRequested = false
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
        if (isDatePickerRequested) {
            OpenDatePicker(
                date = dogInputState.birthDate,
                coroutineScope = scope,
                onDateSelected = { date ->
                    handleEvent(DogInputEvent.BirthDateChanged(date))
                },
                onDatePickerDismissed = {
                    handleEvent(DogInputEvent.BirthDateDialogShown)
                    isDatePickerRequested = false
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
    scrollState: ScrollState,
    coroutineScope: CoroutineScope,
    dogInputState: DogInputState,
    dogInputUnit: DogInputUnit = DogInputUnit.IMPERIAL,
    onNameChanged: (name: String) -> Unit,
    onWeightChanged: (weight: String) -> Unit,
    onDatePickerRequest: () -> Unit,
    onSaveDog: () -> Unit
) {
    val weightFocusRequester = FocusRequester()
    val birthDateFocusRequester = FocusRequester()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(PawCalcTheme.colors.background)
            .padding(top = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CameraInput(
            bottomSheetState = bottomSheetState,
            coroutineScope = coroutineScope,
            imageUri = dogInputState.profilePic
        )
        NameInput(
            modifier = Modifier.padding(horizontal = 48.dp),
            name = dogInputState.name,
            isNameError = !dogInputState.isNameValid,
            onNameChanged = onNameChanged,
            weightFocusRequester = weightFocusRequester
        )
        WeightInput(
            modifier = Modifier.padding(horizontal = 48.dp),
            weight = dogInputState.weight,
            isWeightError = !dogInputState.isWeightValid,
            onWeightChanged = onWeightChanged,
            weightFocusRequester = weightFocusRequester,
            birthDateFocusRequester = birthDateFocusRequester
        )
        BirthDateInput(
            modifier = Modifier.padding(horizontal = 48.dp),
            birthDate = dogInputState.birthDate,
            isBirthDateError = !dogInputState.isBirthDateValid,
            birthDateFocusRequester = birthDateFocusRequester,
            onDatePickerRequest = onDatePickerRequest
        )
        SaveButton(
            modifier = Modifier.fillMaxWidth(),
            isEnabled = dogInputState.isInputValid(),
            onSaveDog = onSaveDog
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CameraInput(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    imageUri: Uri?
) {
    if (imageUri == null) {
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
    } else {
        PictureWithCameraIcon(
            modifier = modifier.clickable {
                // open bottom sheet
                if (!bottomSheetState.isVisible) {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
            }
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(imageUri).build(),
                modifier = Modifier
                    .clip(CircleShape)
                    .border(2.dp, PawCalcTheme.colors.onPrimarySurface(), CircleShape),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
    }
}

@Composable
internal fun NameInput(
    modifier: Modifier = Modifier,
    name: String,
    isNameError: Boolean,
    onNameChanged: (name: String) -> Unit,
    weightFocusRequester: FocusRequester
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.name_text_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(52.dp),
            value = name,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.name_input_placeholder),
                    textAlign = TextAlign.Start,
                    style = PawCalcTheme.typography.h7,
                )
            },
            onValueChange = {
                onNameChanged(it)
            },
            textStyle = PawCalcTheme.typography.h5,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = PawCalcTheme.colors.surface(),
                textColor = PawCalcTheme.colors.onSurface(),
                placeholderColor = Grey500
            ),
            isError = isNameError,
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
        if (isNameError) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Name must be at least 1 character and at most 50 characters",
                style = PawCalcTheme.typography.error,
                color = MaterialTheme.colors.error
            )
        }
    }
}

@Composable
internal fun WeightInput(
    modifier: Modifier = Modifier,
    weight: String,
    isWeightError: Boolean,
    onWeightChanged: (weight: String) -> Unit,
    weightFocusRequester: FocusRequester,
    birthDateFocusRequester: FocusRequester
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.weight_text_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = weight,
            onValueChange = {
                onWeightChanged(it)
            },
            placeholder = {
                Text(
                    stringResource(id = R.string.weight_input_placeholder),
                    textAlign = TextAlign.Start,
                    style = PawCalcTheme.typography.h7
                )
            },
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(.6f)
                .focusRequester(weightFocusRequester),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = PawCalcTheme.colors.surface(),
                textColor = Color.Black,
                placeholderColor = Grey500
            ),
            textStyle = PawCalcTheme.typography.h7.copy(textAlign = TextAlign.Start),
            shape = PawCalcTheme.shapes.mediumRoundedCornerShape.copy(
                bottomStart = ZeroCornerSize,
                bottomEnd = ZeroCornerSize
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
            isError = isWeightError,
            trailingIcon = {
                Text(
                    modifier = Modifier
                        .height(60.dp)
                        .width(48.dp)
                        .background(
                            color = Grey200,
                            shape = PawCalcTheme.shapes.mediumRoundedCornerShape.copy(
                                bottomStart = ZeroCornerSize,
                                bottomEnd = ZeroCornerSize,
                                topStart = ZeroCornerSize
                            )
                        )
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                    text = "lb",
                    style = PawCalcTheme.typography.h5,
                    color = Color.Black
                )
            }
        )
        if (isWeightError) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Weight must be a number and more than 0 lbs",
                style = PawCalcTheme.typography.error,
                color = MaterialTheme.colors.error
            )
        }
    }
}
@Composable
internal fun BirthDateInput(
    modifier: Modifier = Modifier,
    birthDate: String,
    isBirthDateError: Boolean,
    birthDateFocusRequester: FocusRequester,
    onDatePickerRequest: () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(id = R.string.birth_date_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = birthDate,
            enabled = false,
            onValueChange = {},
            placeholder = {
                Text(
                    text = "mm/dd/yyyy",
                    textAlign = TextAlign.Start,
                    style = PawCalcTheme.typography.h7,
                )
            },
            readOnly = true,
            modifier = Modifier
                .height(60.dp)
                .fillMaxWidth(.6f)
                .clickable {
                    onDatePickerRequest()
                }
                .focusRequester(birthDateFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        onDatePickerRequest()
                    }
                }
                .focusable(),
            shape = PawCalcTheme.shapes.mediumRoundedCornerShape.copy(
                bottomStart = ZeroCornerSize,
                bottomEnd = ZeroCornerSize
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = PawCalcTheme.colors.surface(),
                disabledTextColor = Color.Black,
                textColor = Color.Black,
                placeholderColor = Grey500,
                disabledPlaceholderColor = Grey500
            ),
            textStyle = PawCalcTheme.typography.h7.copy(textAlign = TextAlign.Start),
            trailingIcon = {
                IconButton(
                    modifier = Modifier
                        .height(60.dp)
                        .width(42.dp)
                        .background(
                            color = Grey200,
                            shape = PawCalcTheme.shapes.mediumRoundedCornerShape.copy(
                                bottomStart = ZeroCornerSize,
                                bottomEnd = ZeroCornerSize,
                                topStart = ZeroCornerSize
                            )
                        ),
                    onClick = {
                        onDatePickerRequest()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Decimal
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    // send focus to save button
                }
            )
        )
        if (isBirthDateError) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Please select a birth date",
                style = PawCalcTheme.typography.error,
                color = MaterialTheme.colors.error
            )
        }
    }
}
@Composable
internal fun OpenDatePicker(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    date: String,
    onDateSelected: (String) -> Unit = {},
    onDatePickerDismissed: () -> Unit
) {
    var isDateReadyToShow by remember {
        mutableStateOf(false)
    }
    var dateAsLong: Long by remember {
        mutableStateOf(0L)
    }
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch(Dispatchers.Default) {
            dateAsLong = dateToLong(date)
            isDateReadyToShow = true
        }
    }
    if (isDateReadyToShow) {
        AndroidViewBinding(
            DatePickerDialogBinding::inflate,
            modifier = modifier.fillMaxSize()
        ) {
            val fragment = datePickerDialog.getFragment<DatePickerDialogFragment>()
            fragment.arguments = Bundle().apply {
                putLong(DatePickerDialogFragment.BUNDLE_DATE_KEY, dateAsLong)
            }
            fragment.datePickerListener = object : DatePickerListener {
                override fun dateSelected(date: String) {
                    // update date
                    onDateSelected(date)
                    onDatePickerDismissed()
                }

                override fun onCancel() {
                    onDatePickerDismissed()
                }

                override fun onDismiss() {
                    onDatePickerDismissed()
                }
            }
        }
    }
}

@Composable
internal fun SaveButton(
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    onSaveDog: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
        PawCalcButton(
            enabled = isEnabled,
            text = stringResource(id = R.string.save_input),
            onClick = onSaveDog
        )
    }
}

//-----Preview--------------------------------------------------------------------------------------

@OptIn(ExperimentalMaterialApi::class)
@LightDarkPreview
@Composable
fun PreviewNewDogScreen() {
    PawCalcTheme {
        DogInputScreen(
            modifier = Modifier.fillMaxSize(),
            dogInputState = DogInputState(),
            onSaveDog = {},
            onWeightChanged = {},
            onNameChanged = {},
            onDatePickerRequest = {},
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
            coroutineScope = rememberCoroutineScope(),
            scrollState = rememberScrollState()
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@LightDarkPreview
@Composable
fun PreviewEditDogScreen() {
    PawCalcTheme {
        DogInputScreen(
            modifier = Modifier.fillMaxSize(),
            dogInputState = DogInputState(),
            onSaveDog = {},
            onWeightChanged = {},
            onNameChanged = {},
            onDatePickerRequest = {},
            bottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
            coroutineScope = rememberCoroutineScope(),
            scrollState = rememberScrollState()
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
                weightFocusRequester = FocusRequester(),
                isNameError = false
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
                isWeightError = false,
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
                birthDateFocusRequester = FocusRequester(),
                isWeightError = false
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
                birthDate = "07/30/2019",
                birthDateFocusRequester = FocusRequester(),
                onDatePickerRequest = {},
                isBirthDateError = false
            )
        }
    }
}
