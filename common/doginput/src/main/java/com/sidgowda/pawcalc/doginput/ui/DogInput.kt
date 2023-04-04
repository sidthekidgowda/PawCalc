package com.sidgowda.pawcalc.doginput

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.sidgowda.pawcalc.doginput.databinding.DatePickerDialogBinding
import com.sidgowda.pawcalc.doginput.model.DogInputEvent
import com.sidgowda.pawcalc.doginput.model.DogInputMode
import com.sidgowda.pawcalc.doginput.model.DogInputState
import com.sidgowda.pawcalc.doginput.model.DogInputUnit
import com.sidgowda.pawcalc.doginput.ui.*
import com.sidgowda.pawcalc.ui.component.EmptyDogPictureWithCamera
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.component.PictureWithCameraIcon
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalZeroShutterLag
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
    var isDatePickerRequested by remember {
        mutableStateOf(false)
    }
    val cameraPermission = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val mediaPermission = rememberPermissionState(permission = mediaPermission())
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    val cameraMediaImageResult = rememberLauncherForActivityResult(
        contract = CameraMediaActivity.GetPhoto(),
        onResult = { uri ->
            imageUri = uri ?: imageUri
            if (imageUri != null) {
                scope.launch { bottomSheetState.hide() }
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
            bottomSheetState = bottomSheetState,
            coroutineScope = scope,
            dogInputState = dogInputState,
            dogInputMode = dogInputMode,
            dogInputUnit = unit,
            imageUri = imageUri,
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
            onDatePickerRequest = {
                isDatePickerRequested = true
            },
            onSaveDog = onSaveDog,
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
            OpenDatePicker()
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
    imageUri: Uri?,
    onPictureChanged: (picUrl: String) -> Unit,
    onNameChanged: (name: String) -> Unit,
    onWeightChanged: (weight: String) -> Unit,
    onBirthDateChanged: (date: String) -> Unit,
    onDatePickerRequest: () -> Unit,
    onSaveDog: () -> Unit
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
            coroutineScope = coroutineScope,
            imageUri = imageUri
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
            birthDateFocusRequester = birthDateFocusRequester,
            onDatePickerRequest = onDatePickerRequest
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
            onValueChange = {
                onNameChanged(it)
            },
            textStyle = PawCalcTheme.typography.h5,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = PawCalcTheme.colors.surface(),
                textColor = PawCalcTheme.colors.onSurface()
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
            color = PawCalcTheme.colors.contentColor()
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
                    backgroundColor = PawCalcTheme.colors.surface(),
                    textColor = PawCalcTheme.colors.onSurface()
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
                    color = Color.Black
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
        Box(
            modifier = Modifier
                .fillMaxWidth(.6f)
                .height(52.dp)
                .clickable {
                    onDatePickerRequest()
                },
            contentAlignment = Alignment.CenterStart
        ) {
//            TextField(
//                value = "",
//                onValueChange = {},
//                modifier = Modifier
//                    .fillMaxWidth(.9f),
//                textStyle = PawCalcTheme.typography.h5,
//                colors = TextFieldDefaults.textFieldColors(
//                    backgroundColor = PawCalcTheme.colors.surface(),
//                    textColor = PawCalcTheme.colors.onSurface()
//                )
//            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .background(color = Color.White, shape = RoundedCornerShape(topStart = 4.dp))
                    .padding(start = 10.dp)
                    .height(52.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text("mm/dd/yyyy",
                    style = PawCalcTheme.typography.body1,
                    color = PawCalcTheme.colors.onSurface()
                    )
            }
            GreyBox(
                modifier = Modifier.align(Alignment.CenterEnd),
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "",
                    tint = Color.Black
                )
            }
        }
    }
}

@Composable
internal fun OpenDatePicker(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AndroidViewBinding(DatePickerDialogBinding::inflate) {
        val fragment = datePickerDialog.getFragment<DatePickerDialogFragment>()
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
            imageUri = null,
            onSaveDog = {},
            onWeightChanged = {},
            onNameChanged = {},
            onPictureChanged = {},
            onBirthDateChanged = {},
            onDatePickerRequest = {},
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
            imageUri = null,
            onSaveDog = {},
            onWeightChanged = {},
            onNameChanged = {},
            onPictureChanged = {},
            onBirthDateChanged = {},
            onDatePickerRequest = {},
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
                birthDateFocusRequester = FocusRequester(),
                onDatePickerRequest = {}
            )
        }
    }
}
