package com.sidgowda.pawcalc.doginput.ui

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
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
@ExperimentalZeroShutterLag
internal class CameraMediaActivity : ComponentActivity() {

    companion object {
        const val INTENT_EXTRA_KEY = "camera_media_key"
        const val TAKE_PHOTO = "take_photo"
        const val CHOOSE_MEDIA = "choose_media"
    }

    class GetPhoto : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String): Intent {
            return Intent(context, CameraMediaActivity::class.java).apply {
                putExtra(INTENT_EXTRA_KEY, input)
            }
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
                intent?.getStringExtra(INTENT_EXTRA_KEY)?.let { intentFlag ->
                    rememberSystemUiController().apply {
                        isSystemBarsVisible = false
                        isNavigationBarVisible = false
                        systemBarsBehavior =
                            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                    val onClose = {
                        setResult(Activity.RESULT_CANCELED)
                        this@CameraMediaActivity.finish()
                    }
                    val onSavePhoto: (Uri) -> Unit = { uri ->
                        // pass image uri back to previous screen
                        val uriIntent = Intent().apply { data = uri }
                        setResult(Activity.RESULT_OK, uriIntent)
                        this@CameraMediaActivity.finish()
                    }
                    if (intentFlag == TAKE_PHOTO) {
                        OpenCamera(
                            onClose = onClose,
                            onSavePhoto = onSavePhoto
                        )
                    } else {
                        OpenMedia(
                            onClose = onClose,
                            onSavePhoto = onSavePhoto
                        )
                    }
                } ?: run {
                    // intent flag is null, return
                    setResult(Activity.RESULT_CANCELED)
                    this@CameraMediaActivity.finish()
                }
            }
        }
    }
}
