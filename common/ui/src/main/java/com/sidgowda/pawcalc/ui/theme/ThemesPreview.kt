package com.sidgowda.pawcalc.ui.theme

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    showBackground = true,
    name = "Light Mode",
    group = "UI Mode",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    name = "Dark Mode",
    group = "UI Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
annotation class LightDarkPreview
