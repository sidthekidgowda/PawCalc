package com.sidgowda.pawcalc.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sidgowda.pawcalc.doginput.ui.OpenCamera
import com.sidgowda.pawcalc.doginput.ui.OpenMedia
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import timber.log.Timber

class CameraMediaActivity : ComponentActivity() {

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
                        Timber.d("Cancelled taking image or picking image from gallery")
                        setResult(RESULT_CANCELED)
                        this@CameraMediaActivity.finish()
                    }
                    val onSavePhoto: (Uri) -> Unit = { uri ->
                        // pass image uri back to previous screen
                        Timber.d("Successfully took image or picked image from gallery")
                        val uriIntent = Intent().apply { data = uri }
                        setResult(RESULT_OK, uriIntent)
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
                    setResult(RESULT_CANCELED)
                    this@CameraMediaActivity.finish()
                }
            }
        }
    }
}
