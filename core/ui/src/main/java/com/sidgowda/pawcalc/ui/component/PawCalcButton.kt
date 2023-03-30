package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.layout.RowScope
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
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        shape = PawCalcTheme.shapes.mediumRoundedCornerShape,
        onClick = onClick,
        colors = buttonColors(),
        enabled = enabled
    ) {
        content()
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
        PawCalcButton(onClick = {}) {
            Text("Save")
        }
    }
}
