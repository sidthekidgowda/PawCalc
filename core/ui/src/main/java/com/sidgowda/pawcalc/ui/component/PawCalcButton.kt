package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun PawCalcButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = PawCalcTheme.shapes.mediumRoundedCornerShape,
        onClick = onClick
    ) {
        content()
    }
}
