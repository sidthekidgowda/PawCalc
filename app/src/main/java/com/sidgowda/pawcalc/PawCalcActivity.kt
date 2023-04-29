package com.sidgowda.pawcalc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sidgowda.pawcalc.db.settings.ThemeFormat
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PawCalcActivity : AppCompatActivity() {
    val viewModel: PawCalcViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState: PawCalcActivityState by mutableStateOf(PawCalcActivityState.Loading)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState
                    .collect {
                        uiState = it
                    }
            }
        }
        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                PawCalcActivityState.Loading -> true
                is PawCalcActivityState.Initialized -> false
            }
        }
        setContent {
            PawCalcTheme(
                darkTheme = shouldUseDarkTheme(uiState = uiState)
            ) {
                PawCalc()
            }
        }
    }
}

@Composable
private fun shouldUseDarkTheme(
    uiState: PawCalcActivityState,
): Boolean = when (uiState) {
    PawCalcActivityState.Loading -> isSystemInDarkTheme()
    is PawCalcActivityState.Initialized ->
        when (uiState.settings.themeFormat) {
            ThemeFormat.SYSTEM -> isSystemInDarkTheme()
            ThemeFormat.DARK -> true
            ThemeFormat.LIGHT -> false
    }
}

@LightDarkPreview
@Composable
fun PreviewPawCalcActivity() {
    PawCalcTheme {
        PawCalc()
    }
}
