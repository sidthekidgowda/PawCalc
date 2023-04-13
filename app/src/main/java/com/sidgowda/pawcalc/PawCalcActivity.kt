package com.sidgowda.pawcalc

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PawCalcActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            com.sidgowda.pawcalc.ui.theme.PawCalcTheme {
                PawCalcApp()
            }
        }
    }
}

@com.sidgowda.pawcalc.ui.theme.LightDarkPreview
@Composable
fun PreviewPawCalcActivity() {
    com.sidgowda.pawcalc.ui.theme.PawCalcTheme {
        PawCalcApp()
    }
}
