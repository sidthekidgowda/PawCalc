package com.sidgowda.pawcalc.doginput.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.doginput.R
import com.sidgowda.pawcalc.ui.component.PawCalcButton
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun UpdatePhotoBottomSheetContent(
    modifier: Modifier = Modifier,
    onTakePhoto: () -> Unit,
    onChooseMedia: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PawCalcTheme.colors.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        PictureItem(
            onAction = onTakePhoto,
            content = {
                TakePhotoFromCamera()
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PictureItem(
            onAction = onChooseMedia,
            content = {
                ChoosePhotoFromMedia()
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        PawCalcButton(
            text = stringResource(id = R.string.cancel),
            onClick = onCancel
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
internal fun PictureItem(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
    onAction: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(PawCalcTheme.colors.surface)
            .clickable { onAction() }
            .padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
internal fun TakePhotoFromCamera() {
    Icon(
        imageVector = Icons.Default.PhotoCamera,
        contentDescription = null,
        tint = PawCalcTheme.colors.onSurface
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = stringResource(id = R.string.bottom_sheet_take_photo),
        style = PawCalcTheme.typography.body1,
        color = PawCalcTheme.colors.onSurface
    )
}

@Composable
fun ChoosePhotoFromMedia() {
    Icon(
        imageVector = Icons.Default.Photo,
        contentDescription = null,
        tint = PawCalcTheme.colors.onSurface
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
        text = stringResource(id = R.string.bottom_sheet_choose_photo),
        style = PawCalcTheme.typography.body1,
        color = PawCalcTheme.colors.onSurface
    )
}

//------Preview-------------------------------------------------------------------------------------
@LightDarkPreview
@Composable
fun PreviewChangePictureBottomSheet() {
    PawCalcTheme {
        UpdatePhotoBottomSheetContent(
            onTakePhoto = {},
            onChooseMedia = {},
            onCancel = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewTakePhotoFromCamera() {
    PawCalcTheme {
        PictureItem(
            onAction = {  },
            content = {
                TakePhotoFromCamera()
            }
        )
    }
}


@LightDarkPreview
@Composable
fun PreviewChoosePhotoFromMedia() {
    PawCalcTheme {
        PictureItem(
            onAction = {  },
            content = {
                ChoosePhotoFromMedia()
            }
        )
    }
}

