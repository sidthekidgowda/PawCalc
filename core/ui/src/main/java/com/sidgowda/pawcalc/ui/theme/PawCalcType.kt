package com.sidgowda.pawcalc.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sidgowda.pawcalc.ui.R

val OpenSans = FontFamily(
    Font(R.font.open_sans_regular),
    Font(R.font.open_sans_semibold, FontWeight.SemiBold),
    Font(R.font.open_sans_bold, FontWeight.Bold),
    Font(R.font.open_sans_light, FontWeight.Light)
)

object PawCalcTypography {
    val h1 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 36.sp,
        fontWeight = FontWeight.Normal
    )

    val h2 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 32.sp,
        fontWeight = FontWeight.SemiBold
    )

    val h3 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold
    )
    val h4 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
    val h5 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal
    )

    val body1 = TextStyle(
        fontFamily = OpenSans,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    )
}
