package com.sidgowda.pawcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PawCalcActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PawCalcTheme {
                PawCalcApp(
                    onActivityFinish = { this@PawCalcActivity.finish() }
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewPawCalcActivity() {
    PawCalcTheme {
        PawCalcApp(onActivityFinish = {})
    }
}
