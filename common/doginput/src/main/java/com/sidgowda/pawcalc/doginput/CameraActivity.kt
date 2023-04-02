package com.sidgowda.pawcalc.doginput

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.core.view.WindowCompat
import com.sidgowda.pawcalc.doginput.ui.OpenCamera
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
@ExperimentalZeroShutterLag
class CameraActivity : ComponentActivity() {

    class TakePhoto : ActivityResultContract<Unit, Uri?>() {

        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, CameraActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) {
                intent?.data
            } else {
                null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PawCalcTheme {
                OpenCamera(
                    onClose = {
                        setResult(Activity.RESULT_CANCELED)
                        this@CameraActivity.finish()
                    },
                    onSavePhoto = { uri ->
                        // pass image uri back to previous screen
                        val uriIntent = Intent().apply { data = uri }
                        setResult(Activity.RESULT_OK, uriIntent)
                        this@CameraActivity.finish()
                    }
                )
            }
        }
    }
}
