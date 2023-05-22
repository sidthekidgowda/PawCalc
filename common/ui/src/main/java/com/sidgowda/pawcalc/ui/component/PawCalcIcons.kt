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
import androidx.compose.material.Surface
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
import com.sidgowda.pawcalc.ui.theme.*

@Composable
fun PawCalcLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    Icon(
        modifier = modifier
            .size(100.dp)
            .circleShapeWithBorderAndBackground(
                borderColor = PawCalcTheme.colors.onPrimarySurface(),
                backgroundColor = Blue400
            )
            .wrapContentSize(),
        imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
        contentDescription = contentDescription,
        tint = Color.Black,
    )
}

fun Modifier.circleShapeWithBorderAndBackground(
    borderColor: Color,
    backgroundColor: Color
): Modifier {
    return clip(CircleShape)
        .border(2.dp, borderColor, CircleShape)
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
            .circleShapeWithBorderAndBackground(
                backgroundColor = Green500,
                borderColor = PawCalcTheme.colors.iconTint()
            )
            .wrapContentSize(),
        imageVector = Icons.Default.AddAPhoto,
        contentDescription = contentDescription,
        tint = Color.Black,
    )
}

@Composable
fun PictureWithCameraIcon(
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        image()
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
    PictureWithCameraIcon(modifier = modifier) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .border(2.dp, PawCalcTheme.colors.onPrimarySurface(), CircleShape)
                .background(Grey200)
                .padding(30.dp),
            tint = Color.Black
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
                modifier = Modifier
                    .clip(CircleShape)
                    .border(2.dp, PawCalcTheme.colors.onPrimarySurface(), CircleShape),
                contentDescription = null
            )
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewEmptyDogPictureWithCamera() {
    PawCalcTheme {
        Surface {
            EmptyDogPictureWithCamera()
        }
    }
}
