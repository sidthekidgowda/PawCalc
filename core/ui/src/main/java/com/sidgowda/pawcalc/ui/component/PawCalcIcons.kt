package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ControlCamera
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.R
import com.sidgowda.pawcalc.ui.theme.Blue400
import com.sidgowda.pawcalc.ui.theme.Grey200
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun PawCalcLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val backgroundColor = Blue400
    CircleShape(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
            contentDescription = contentDescription,
            tint = Color.Black
        )
    }
}

@Composable
fun EmptyCameraLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val backgroundColor = Grey200
    CircleShape(
        modifier = modifier,
        backgroundColor = backgroundColor
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = contentDescription,
            modifier = Modifier.size(40.dp),
            tint = Color.Black
        )
    }
}

@Composable
fun CircleShape(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        content()
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
        EmptyCameraLogo(
            modifier = Modifier
                .padding(10.dp)
                .size(100.dp),
            contentDescription = null
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewCircleShape() {
    PawCalcTheme {
        CircleShape(
            modifier = Modifier.size(100.dp),
            backgroundColor = Color.Red,
            content = {
                Icon(
                    imageVector = Icons.Default.ControlCamera,
                    contentDescription = null
                )
            }
        )
    }
}
