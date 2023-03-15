package com.sidgowda.pawcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

//-----Light Colors
val Green500 = Color(0xFF30C77E) // primary
val Blue400 = Color(0xFF30C4C7) // secondary
val Green700 = Color(0xFF009F46) // status
val Grey50 = Color(0xFFF9F4F4) // surface
val Grey700 = Color(0xFF555555) // onSurface

//------Dark Colors
val Green200 = Color(0xFF63D68F) // primary
val Blue200 = Color(0xFF30C4C7) // secondary
val Grey200 = Color(0xFF646868) // surface
val Grey500 = Color(0xFF8C9090) // background
val Grey900 = Color(0xFF121212) // background
val White = Color(0xFFFFFFFF) // onSurface
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
    fun primarySurface(): Color =
        if (isSystemInDarkTheme()) background else primary

    @Composable
    fun onPrimarySurface(): Color =
        if (isSystemInDarkTheme()) onBackground else onPrimary
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
