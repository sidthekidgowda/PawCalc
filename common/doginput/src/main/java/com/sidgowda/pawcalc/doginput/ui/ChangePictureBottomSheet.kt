package com.sidgowda.pawcalc.doginput.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Surface
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
    onChoosePhoto: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PawCalcTheme.colors.surface)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        PictureItem {
            TakePhotoFromCamera(onTakePhoto = onTakePhoto)
        }
        Spacer(modifier = Modifier.height(10.dp))
        PictureItem {
            ChoosePhotoFromMedia(onChoosePhoto = onChoosePhoto)
        }
        Spacer(modifier = Modifier.height(20.dp))
        PawCalcButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(id = R.string.cancel),
            onClick = onCancel
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun PictureItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        content()
    }
}

@Composable
fun TakePhotoFromCamera(
    modifier: Modifier = Modifier,
    onTakePhoto: () -> Unit
) {
    PictureItem(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable(
                    onClickLabel = stringResource(id = R.string.cd_bottom_sheet_take_photo)
                ) {
                    onTakePhoto()
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
    ) {
            Icon(
                imageVector = Icons.Default.PhotoCamera,
                contentDescription = null,
                tint = PawCalcTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = stringResource(id = R.string.bottom_sheet_take_photo),
                style = PawCalcTheme.typography.body1,
                color = PawCalcTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun ChoosePhotoFromMedia(
    modifier: Modifier = Modifier,
    onChoosePhoto: () -> Unit
) {
    PictureItem(modifier = modifier) {
        Row(
            modifier = Modifier
                .clickable(
                    onClickLabel = stringResource(id = R.string.cd_bottom_sheet_choose_photo)
                ) {
                    onChoosePhoto()
                }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = null,
                tint = PawCalcTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = stringResource(id = R.string.bottom_sheet_choose_photo),
                style = PawCalcTheme.typography.body1,
                color = PawCalcTheme.colors.onSurface
            )
        }
    }
}

//------Preview-------------------------------------------------------------------------------------
@LightDarkPreview
@Composable
fun PreviewChangePictureBottomSheet() {
    PawCalcTheme {
        UpdatePhotoBottomSheetContent(
            onTakePhoto = {},
            onChoosePhoto = {},
            onCancel = {}
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewTakePhotoFromCamera() {
    PawCalcTheme {
        TakePhotoFromCamera(
            onTakePhoto = {}
        )
    }
}


@LightDarkPreview
@Composable
fun PreviewChoosePhotoFromMedia() {
    PawCalcTheme {
        ChoosePhotoFromMedia(
            onChoosePhoto = {}
        )
    }
}

