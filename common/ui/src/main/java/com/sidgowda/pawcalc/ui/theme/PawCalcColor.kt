package com.sidgowda.pawcalc.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White


val Green500 = Color(0xFF30C77E) // primary
val Blue400 = Color(0xFF30C4C7) // secondary
val Green700 = Color(0xFF009F46) // status
val Grey50 = Color(0xFFF9F4F4) // surface
val Grey700 = Color(0xFF555555) // onSurface
val Grey200 = Color(0xFFD9D9D9) // text field component color

val Green200 = Color(0xFF63D68F) // dark primary
val Blue200 = Color(0xFF30C4C7) // dark secondary
val Grey600 = Color(0xFF646868)
val Grey500 = Color(0xFF8C9090)
val Grey900 = Color(0xFF121212) // dark background and surface

val Orange500 = Color(0xFFEA8426)

interface PawCalcColorScheme {
    val primary: Color
    val onPrimary: Color
    val secondary: Color
    val onSecondary: Color
    val surface: Color
    val onSurface: Color
    val background: Color
    val onBackground: Color

    @Composable
    fun onPrimarySurface(): Color =
        if (MaterialTheme.colors.isLight) {
            onPrimary
        } else {
            onBackground
        }


    @Composable
    fun contentColor(): Color =
        if (MaterialTheme.colors.isLight) {
            onBackground
        } else {
            Grey200
        }

    @Composable
    fun iconTint(): Color =
        if (MaterialTheme.colors.isLight) {
            Black
        } else {
            White
        }

    @Composable
    fun surface(): Color =
        if (MaterialTheme.colors.isLight) {
            surface
        } else {
            White
        }

    @Composable
    fun onSurface(): Color =
        if (MaterialTheme.colors.isLight) {
            onSurface
        } else {
            Black
        }
}

object PawCalcColorLightScheme : PawCalcColorScheme {
    override val primary: Color
        get() = Green500
    override val onPrimary: Color
        get() = Black
    override val secondary: Color
        get() = Blue400
    override val onSecondary: Color
        get() = Black
    override val surface: Color
        get() = White
    override val onSurface: Color
        get() = Grey700
    override val background: Color
        get() = Grey50
    override val onBackground: Color
        get() = Grey700
}

object PawCalcColorDarkScheme : PawCalcColorScheme {
    override val primary: Color
        get() = Green200
    override val onPrimary: Color
        get() = Black
    override val secondary: Color
        get() = Blue200
    override val onSecondary: Color
        get() = Black
    override val surface: Color
        get() = Grey900
    override val onSurface: Color
        get() = White
    override val background: Color
        get() = Grey900
    override val onBackground: Color
        get() = White
}
