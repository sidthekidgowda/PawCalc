package com.sidgowda.pawcalc.doginput.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
internal fun MediaButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String? = null,
    onAction: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onAction,
    ) {
        Icon(
            imageVector = imageVector,
            tint = Color.White,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(4.dp)
        )
    }
}


//-------Preview------------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewBackButton() {
    PawCalcTheme {
        MediaButton(imageVector = Icons.Default.ArrowBack) {

        }
    }
}

@LightDarkPreview
@Composable
fun PreviewCloseButton() {
    PawCalcTheme {
       MediaButton(imageVector = Icons.Default.Close) {

       }
    }
}
