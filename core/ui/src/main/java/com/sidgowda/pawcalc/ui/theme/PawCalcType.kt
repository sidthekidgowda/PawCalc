package com.sidgowda.pawcalc.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sidgowda.pawcalc.ui.R

val OpenSans = FontFamily(
    Font(R.font.open_sans_bold),
    Font(R.font.open_sans_light),
    Font(R.font.open_sans_regular),
    Font(R.font.open_sans_semibold)
)

object PawCalcTypography {
    val h1 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 30.sp,
        fontWeight = FontWeight.SemiBold
    )
    val h2 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    )
    val h3 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
    val body = TextStyle(
        fontFamily = OpenSans,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
}
