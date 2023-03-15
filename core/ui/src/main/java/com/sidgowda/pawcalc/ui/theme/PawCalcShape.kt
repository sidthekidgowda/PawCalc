package com.sidgowda.pawcalc.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object PawCalcShape {
    val smallRoundedCornerShape: RoundedCornerShape
        @Composable
        get() = RoundedCornerShape(4.dp)
    val mediumRoundedCornerShape: RoundedCornerShape
        @Composable
        get() = RoundedCornerShape(8.dp)
    val largeRoundedCornerShape: RoundedCornerShape
        @Composable
        get() = RoundedCornerShape(12.dp)
}
