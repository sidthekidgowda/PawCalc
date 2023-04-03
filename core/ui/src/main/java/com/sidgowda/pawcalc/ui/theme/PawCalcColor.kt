package com.sidgowda.pawcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
        if (isSystemInDarkTheme()) onBackground else onPrimary

    @Composable
    fun contentColor(): Color =
        if (isSystemInDarkTheme()) Grey200 else onBackground

    @Composable
    fun iconTint(): Color =
        if (isSystemInDarkTheme()) Color.White else Color.Black

    @Composable
    fun surface(): Color =
        if (isSystemInDarkTheme()) Color.White else surface

    @Composable
    fun onSurface(): Color =
        if (isSystemInDarkTheme()) Color.Black else onSurface
}

object PawCalcColorLightScheme : PawCalcColorScheme {
    override val primary: Color
        get() = Green500
    override val onPrimary: Color
        get() = Color.Black
    override val secondary: Color
        get() = Blue400
    override val onSecondary: Color
        get() = Color.Black
    override val surface: Color
        get() = Color.White
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
        get() = Color.Black
    override val secondary: Color
        get() = Blue200
    override val onSecondary: Color
        get() = Color.Black
    override val surface: Color
        get() = Grey900
    override val onSurface: Color
        get() = Color.White
    override val background: Color
        get() = Grey900
    override val onBackground: Color
        get() = White
}
