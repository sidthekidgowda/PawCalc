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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.sidgowda.pawcalc.camera.ui.CameraMediaActivity
import com.sidgowda.pawcalc.common.settings.DateFormat
import com.sidgowda.pawcalc.common.settings.WeightFormat
import com.sidgowda.pawcalc.data.date.dateToLong
import com.sidgowda.pawcalc.doginput.databinding.DatePickerDialogBinding
import com.sidgowda.pawcalc.doginput.date.DatePickerDialogFragment
import com.sidgowda.pawcalc.doginput.date.DatePickerListener
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.ui.*
import com.sidgowda.pawcalc.test.TestTags.DogInput.TAG_SAVE_BUTTON
import com.sidgowda.pawcalc.ui.component.EmptyDogPictureWithCamera
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.component.PictureWithCameraIcon
import com.sidgowda.pawcalc.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class, ExperimentalPermissionsApi::class)
@Composable
fun DogInput(
    modifier: Modifier = Modifier,
    dogInputState: DogInputState,
    handleEvent: (event: DogInputEvent) -> Unit,
    onSaveDog: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
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
            // dismiss bottom sheet only when we update UI with a new picture
            if (uri != null) {
                Timber.tag("DogInput").d("New picture arrived, dismiss bottom sheet")
                scope.launch { bottomSheetState.hide() }
                imageUri = uri
                // notify listeners image has possibly updated
                handleEvent(DogInputEvent.PicChanged(uri))
            } else {
                Timber.tag("DogInput").d("No new picture arrived, keep bottom sheet")
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
            dogInputState = dogInputState,
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
            showBottomSheet = {
                scope.launch {
                    focusManager.clearFocus()
                    bottomSheetState.show()
                }
            }
        )
        val requestPermission = {
            Timber.tag("DogInput").d("Requesting permission in Settings.")
            context.startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:${context.packageName}")
                )
            )
        }
        if (isCameraRequested) {
            HandlePermission(
                permission = cameraPermission.permission,
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
                permission = mediaPermission.permission,
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
                isDateFormatInternational = dogInputState.dateFormat == DateFormat.INTERNATIONAL,
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
    dogInputState: DogInputState,
    showBottomSheet: () -> Unit,
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
            .background(PawCalcTheme.colors.background),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        CameraInput(
            bottomSheetState = bottomSheetState,
            imageUri = dogInputState.profilePic,
            name = dogInputState.name,
            showBottomSheet = showBottomSheet
        )
        NameInput(
            name = dogInputState.name,
            isNameError = !dogInputState.isNameValid,
            onNameChanged = onNameChanged,
            weightFocusRequester = weightFocusRequester
        )
        WeightInput(
            weight = dogInputState.weight,
            weightFormat = dogInputState.weightFormat,
            isWeightError = !dogInputState.isWeightValid,
            onWeightChanged = onWeightChanged,
            weightFocusRequester = weightFocusRequester,
            birthDateFocusRequester = birthDateFocusRequester
        )
        BirthDateInput(
            birthDate = dogInputState.birthDate,
            dateFormat = dogInputState.dateFormat,
            isBirthDateError = !dogInputState.isBirthDateValid,
            birthDateFocusRequester = birthDateFocusRequester,
            onDatePickerRequest = onDatePickerRequest
        )
        PawCalcButton(
            modifier = Modifier
                .testTag(TAG_SAVE_BUTTON)
                .padding(top = 10.dp),
            enabled = dogInputState.isInputValid(),
            text = stringResource(id = R.string.save_input),
            onClick = onSaveDog
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun CameraInput(
    modifier: Modifier = Modifier,
    bottomSheetState: ModalBottomSheetState,
    name: String,
    imageUri: Uri?,
    showBottomSheet: () -> Unit
) {
    val imageDoesNotExist = imageUri == null
    val nameLabel = name.ifEmpty { stringResource(id = R.string.cd_your_dog) }
    val clickLabel = if (imageDoesNotExist) {
        stringResource(id = R.string.cd_camera_icon_empty, nameLabel)
    } else {
        stringResource(id = R.string.cd_camera_icon_update, nameLabel)
    }
    if (imageDoesNotExist) {
        EmptyDogPictureWithCamera(
            modifier = modifier
                .clickable {
                    // open bottom sheet
                    if (!bottomSheetState.isVisible) {
                        showBottomSheet()
                    }
                }
                .semantics {
                    contentDescription = clickLabel
                }
        )
    } else {
        PictureWithCameraIcon(
            modifier = modifier
                .clickable {
                    // open bottom sheet
                    if (!bottomSheetState.isVisible) {
                        showBottomSheet()
                    }
                }
                .semantics {
                    contentDescription = clickLabel
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
    val nameLabel = name.ifEmpty {
        stringResource(id = R.string.cd_name_input)
    }
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (header, textInput, errorText) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(header) {
                    start.linkTo(textInput.start)
                    top.linkTo(parent.top)
                }
                .padding(start = 44.dp, bottom = 10.dp),
            text = stringResource(id = R.string.name_text_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        TextField(
            modifier = Modifier
                .constrainAs(textInput) {
                    start.linkTo(parent.start)
                    top.linkTo(header.bottom)
                    end.linkTo(parent.end)
                }
                .clearAndSetSemantics {
                    contentDescription = nameLabel
                }
                .padding(horizontal = 44.dp)
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
            shape = PawCalcTheme.shapes.mediumRoundedCornerShape.copy(
                bottomStart = ZeroCornerSize,
                bottomEnd = ZeroCornerSize
            ),
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
            Text(
                modifier = Modifier
                    .constrainAs(errorText) {
                        start.linkTo(textInput.start, 44.dp)
                        top.linkTo(textInput.bottom, 2.dp)
                        end.linkTo(textInput.end, 44.dp)
                        width = Dimension.fillToConstraints
                    },
                text = stringResource(id = R.string.name_input_error),
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
    weightFormat: WeightFormat,
    isWeightError: Boolean,
    onWeightChanged: (weight: String) -> Unit,
    weightFocusRequester: FocusRequester,
    birthDateFocusRequester: FocusRequester
) {
    val isWeightFormatInPounds = weightFormat == WeightFormat.POUNDS
    val weightInput = weight.ifEmpty {
        stringResource(id = R.string.cd_weight_input)
    }
    val weightFormatLabel = if (isWeightFormatInPounds) {
        stringResource(id = R.string.cd_weight_format_lb)
    } else {
        stringResource(id = R.string.cd_weight_format_kg)
    }
    val weightLabel = "$weightInput$weightFormatLabel"
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (header, spacer, textInput, errorText) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(header) {
                    start.linkTo(textInput.start)
                    top.linkTo(parent.top)
                }
                .padding(bottom = 10.dp),
            text = stringResource(id = R.string.weight_text_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        Spacer(
            modifier = Modifier
                .constrainAs(spacer) {
                    start.linkTo(parent.start)
                    top.linkTo(header.bottom)
                }
                .width(44.dp)
        )
        TextField(
            modifier = Modifier
                .constrainAs(textInput) {
                    start.linkTo(spacer.end)
                    top.linkTo(header.bottom)
                }
                .clearAndSetSemantics {
                    contentDescription = weightLabel
                }
                .height(60.dp)
                .fillMaxWidth(.6f)
                .focusRequester(weightFocusRequester),
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
                        .semantics {
                            contentDescription = weightFormatLabel
                        }
                        .wrapContentSize(),
                    textAlign = TextAlign.Center,
                    text = stringResource(
                        id = if (isWeightFormatInPounds)
                            R.string.weight_input_unit_lb
                        else R.string.weight_input_unit_kg
                    ),
                    style = PawCalcTheme.typography.h5,
                    color = Color.Black
                )
            }
        )
        if (isWeightError) {
            Text(
                modifier = Modifier
                    .constrainAs(errorText) {
                        start.linkTo(textInput.start)
                        top.linkTo(textInput.bottom, 2.dp)
                        end.linkTo(textInput.end)
                        width = Dimension.fillToConstraints
                    },
                textAlign = TextAlign.Start,
                text = stringResource(
                    id = if (isWeightFormatInPounds) R.string.weight_input_error else R.string.weight_input_error_kg
                ),
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
    dateFormat: DateFormat,
    isBirthDateError: Boolean,
    birthDateFocusRequester: FocusRequester,
    onDatePickerRequest: () -> Unit
) {
    val datePickerLabel = birthDate.ifEmpty {
        stringResource(id = R.string.cd_date_picker)
    }
    val isDateFormatAmerican = dateFormat == DateFormat.AMERICAN
    val dateFormatLabel = if (isDateFormatAmerican) {
        stringResource(id = R.string.cd_months_days_years)
    } else {
        stringResource(id = R.string.cd_days_months_years)
    }
    val birthDateLabel = "$datePickerLabel$dateFormatLabel"
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (header, spacer, textInput, errorText) = createRefs()
        Text(
            modifier = Modifier
                .constrainAs(header) {
                    start.linkTo(textInput.start)
                    top.linkTo(parent.top)
                }
                .padding(bottom = 10.dp),
            text = stringResource(id = R.string.birth_date_input),
            style = PawCalcTheme.typography.h4,
            color = PawCalcTheme.colors.contentColor()
        )
        Spacer(
            modifier = Modifier
                .constrainAs(spacer) {
                    start.linkTo(parent.start)
                    top.linkTo(header.bottom)
                }
                .width(44.dp)
        )
        TextField(
            modifier = Modifier
                .constrainAs(textInput) {
                    start.linkTo(spacer.end)
                    top.linkTo(header.bottom)
                }
                .height(60.dp)
                .fillMaxWidth(.6f)
                .clickable {
                    Timber.tag("DogInput").d("Date picker requested")
                    onDatePickerRequest()
                }
                .clearAndSetSemantics {
                    contentDescription = birthDateLabel
                }
                .focusRequester(birthDateFocusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        Timber.tag("DogInput").d("Date picker requested")
                        onDatePickerRequest()
                    }
                }
                .focusable(),
            value = birthDate,
            enabled = false,
            onValueChange = {},
            placeholder = {
                Text(
                    text = stringResource(
                        id = if (isDateFormatAmerican) {
                            R.string.birth_date_american_placeholder
                        } else {
                            R.string.birth_date_international_placeholder
                        }
                    ),
                    textAlign = TextAlign.Start,
                    style = PawCalcTheme.typography.h7,
                )
            },
            readOnly = true,
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
                        contentDescription = null,
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
            Text(
                modifier = Modifier
                    .constrainAs(errorText) {
                        start.linkTo(textInput.start)
                        top.linkTo(textInput.bottom)
                        end.linkTo(textInput.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(
                        top = 2.dp
                    ),
                textAlign = TextAlign.Start,
                text = stringResource(id = R.string.birth_date_input_error),
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
    isDateFormatInternational: Boolean,
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
            Timber.tag("DogInput").d("Start converting Date to long")
            dateAsLong = dateToLong(date, isDateFormatInternational)
            isDateReadyToShow = true
            Timber.tag("DogInput").d("Finished converting Date to long")
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
                putBoolean(
                    DatePickerDialogFragment.BUNDLE_IS_DATE_FORMAT_INTERNATIONAL, isDateFormatInternational
                )
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
            scrollState = rememberScrollState(),
            showBottomSheet = {}
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
            scrollState = rememberScrollState(),
            showBottomSheet = {}
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
                birthDateFocusRequester = FocusRequester(),
                weightFormat = WeightFormat.POUNDS
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
                isWeightError = false,
                weightFormat = WeightFormat.POUNDS
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
                dateFormat = DateFormat.AMERICAN,
                birthDateFocusRequester = FocusRequester(),
                onDatePickerRequest = {},
                isBirthDateError = false
            )
        }
    }
}
