package com.sidgowda.pawcalc.doginput

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.core.view.WindowCompat
import com.sidgowda.pawcalc.doginput.ui.OpenCamera

class CameraActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            OpenCamera(
                onClose = {
                    this@CameraActivity.finish()
                }
            )
        }
    }
}
