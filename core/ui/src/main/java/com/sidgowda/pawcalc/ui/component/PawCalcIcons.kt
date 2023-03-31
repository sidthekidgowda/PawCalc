package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.R
import com.sidgowda.pawcalc.ui.theme.Blue400
import com.sidgowda.pawcalc.ui.theme.Green500
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun PawCalcLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        modifier = modifier
            .size(100.dp)
            .circleShapeWithWhiteBorderAndBackground(Blue400)
            .wrapContentSize(),
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
        contentDescription = contentDescription,
        tint = Color.Black,
    )
}

fun Modifier.circleShapeWithWhiteBorderAndBackground(backgroundColor: Color): Modifier {
    return clip(CircleShape)
        .border(2.dp, Color.White, CircleShape)
        .background(backgroundColor)
}

@Composable
fun EmptyCameraButton(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        modifier = modifier
            .size(60.dp)
            .circleShapeWithWhiteBorderAndBackground(Green500)
            .wrapContentSize(),
        imageVector = Icons.Default.AddAPhoto,
        contentDescription = contentDescription,
        tint = Color.Black,
    )
}

@Composable
fun PictureWithCameraIcon(
    modifier: Modifier = Modifier,
    dogImage: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        dogImage()
        EmptyCameraButton(
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun EmptyDogPictureWithCamera(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    PictureWithCameraIcon() {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(200.dp)
                .border(2.dp, Color.Black, CircleShape)
                .padding(30.dp),
            tint = Color.Black
        )
    }
}

@Composable
fun SampleDogPictureWithCamera(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(200.dp)
            .wrapContentSize(Alignment.Center)
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog_puppy),
            contentScale = ContentScale.Crop,
            modifier = Modifier.clip(CircleShape),
            contentDescription = null
        )
        Icon(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(60.dp)
                .circleShapeWithWhiteBorderAndBackground(Green500)
                .wrapContentSize(),
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = contentDescription,
            tint = Color.Black,
        )
    }
}

//------Preview-------------------------------------------------------------------------------------
@LightDarkPreview
@Composable
fun PreviewPawCalcLogo() {
    PawCalcTheme {
        PawCalcLogo(
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp),
            contentDescription = null
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewEmptyCameraLogo() {
    PawCalcTheme {
        EmptyCameraButton(
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp),
            contentDescription = null
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewDogPictureWithCamera() {
    PawCalcTheme {
        PictureWithCameraIcon() {
            Image(
                painter = painterResource(id = R.drawable.dog_puppy),
                contentScale = ContentScale.Crop,
                modifier = Modifier.clip(CircleShape),
                contentDescription = null
            )
        }
        EmptyDogPictureWithCamera()
    }
}

@LightDarkPreview
@Composable
fun PreviewEmptyDogPictureWithCamera() {
    PawCalcTheme {
        PictureWithCameraIcon() {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .border(2.dp, Color.Black, CircleShape)
                    .padding(30.dp),
                tint = Color.Black
            )
        }
    }
}
