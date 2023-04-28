package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun PawCalcButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(.8f),
        shape = PawCalcTheme.shapes.mediumRoundedCornerShape,
        onClick = onClick,
        colors = buttonColors(),
        enabled = enabled
    ) {
        Text(
            text = text,
            style = PawCalcTheme.typography.h3,
            color = PawCalcTheme.colors.onPrimary
        )
    }
}

@Composable
fun buttonColors() = ButtonDefaults.buttonColors(
    backgroundColor = PawCalcTheme.colors.primary,
    contentColor = contentColorFor(backgroundColor = PawCalcTheme.colors.primary),
    disabledBackgroundColor = PawCalcTheme.colors.primary.copy(alpha = 0.5f)
        .compositeOver(PawCalcTheme.colors.background),
    disabledContentColor = PawCalcTheme.colors.onPrimary
)

@LightDarkPreview
@Composable
fun PreviewPawCalcButton() {
    PawCalcTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            PawCalcButton(text = "Save", onClick = {}, )
        }
    }
}
