package com.sidgowda.pawcalc.dogdetails.model


import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.sidgowda.pawcalc.dogdetails.R

enum class LegendType(
    @StringRes val stringId: Int,
    val darkThemeColor: Color,
    val lightThemeColor: Color,
) {
    DAYS(
        stringId= R.string.legend_days,
        darkThemeColor = Color(0xFF30C4C7),
        lightThemeColor = Color(0xFF30C4C7)
    ),
    MONTHS(
        stringId = R.string.legend_months,
        darkThemeColor = Color(0xFF63D68F),
        lightThemeColor = Color(0xFF30C77E)
    ),
    YEARS(
        stringId = R.string.legend_years,
        darkThemeColor = Color(0xFFEA8426),
        lightThemeColor = Color(0xFFEA8426)
    )
}
