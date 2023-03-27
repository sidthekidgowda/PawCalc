package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.R
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun PawCalcLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val backgroundColor = PawCalcTheme.colors.onBackground
    Box(
        modifier = modifier.drawBehind {
            drawCircle(backgroundColor)
        },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_paw),
            contentDescription = contentDescription,
            tint = PawCalcTheme.colors.background
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewPawCalcLogo() {
    PawCalcTheme {
        PawCalcLogo(modifier = Modifier.padding(10.dp).size(100.dp), contentDescription = null)
    }
}
