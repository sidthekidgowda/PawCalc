package com.sidgowda.pawcalc.doginput

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.core.view.WindowCompat
import com.sidgowda.pawcalc.doginput.ui.OpenCamera
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
@ExperimentalZeroShutterLag
class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PawCalcTheme {
                OpenCamera(
                    onClose = {
                        this@CameraActivity.finish()
                    }
                )
            }
        }
    }
}
