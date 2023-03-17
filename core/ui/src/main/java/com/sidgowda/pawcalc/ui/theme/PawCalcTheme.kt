package com.sidgowda.pawcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

val LocalPawCalcColor: ProvidableCompositionLocal<PawCalcColorScheme> =
    staticCompositionLocalOf { error("no provided") }

val LocalPawCalTypography: ProvidableCompositionLocal<PawCalcTypography> =
    staticCompositionLocalOf { error("no provided") }

val LocalPawCalcShape: ProvidableCompositionLocal<PawCalcShape> =
    staticCompositionLocalOf { error("no provided") }

object PawCalcTheme {
    val colors: PawCalcColorScheme
        @Composable
        get() = LocalPawCalcColor.current

    val typography: PawCalcTypography
        @Composable
        get() = LocalPawCalTypography.current

    val shapes: PawCalcShape
        @Composable
        get() = LocalPawCalcShape.current
}

fun mapMaterialColors(
    darkTheme: Boolean,
    pawCalcColors: PawCalcColorScheme
) = if (darkTheme) {
    darkColors(
        primary = pawCalcColors.primary,
        onPrimary = pawCalcColors.onPrimary,
        secondary = pawCalcColors.secondary,
        onSecondary = pawCalcColors.onSecondary,
        background = pawCalcColors.background,
        onBackground = pawCalcColors.onBackground,
        surface = pawCalcColors.surface,
        onSurface = pawCalcColors.onSurface
    )
} else {
    lightColors(
        primary = pawCalcColors.primary,
        onPrimary = pawCalcColors.onPrimary,
        secondary = pawCalcColors.secondary,
        onSecondary = pawCalcColors.onSecondary,
        background = pawCalcColors.background,
        onBackground = pawCalcColors.onBackground,
        surface = pawCalcColors.surface,
        onSurface = pawCalcColors.onSurface
    )
}

@Composable
fun PawCalcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val localPawCalcColors: PawCalcColorScheme = if (darkTheme) {
        PawCalcColorDarkScheme
    } else {
        PawCalcColorLightScheme
    }
    CompositionLocalProvider(
        LocalPawCalcColor provides localPawCalcColors,
        LocalPawCalTypography provides PawCalcTypography,
        LocalPawCalcShape provides PawCalcShape
    ) {
        MaterialTheme(
            colors = mapMaterialColors(darkTheme, localPawCalcColors)
        ) {
            content()
        }
    }
}
