package com.sidgowda.pawcalc.doginput.ui

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.launch

@Composable
internal fun OpenCamera(
    modifier: Modifier = Modifier,
    onShutter: () -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val systemUiController = rememberSystemUiController()
        systemUiController.isSystemBarsVisible = false
        systemUiController.isNavigationBarVisible = false
        systemUiController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        CameraPreview(
            modifier = Modifier.fillMaxSize()
        )
        CameraShutterButton(
            modifier = Modifier
                .padding(vertical = 50.dp),
            onShutter = onShutter
        )
        CloseButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClose = onClose
        )
    }
}

@Composable
internal fun CameraPreview(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val scaleType = PreviewView.ScaleType.FILL_CENTER

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = scaleType
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            val previewUseCase = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            coroutineScope.launch {
                val cameraProvider = context.getCameraProvider()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, previewUseCase
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Use case binding failed", e)
                }
            }
            previewView
    })
}
@Composable
internal fun CameraShutterButton(
    modifier: Modifier = Modifier,
    onShutter: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onShutter,
    ) {
        Icon(
            imageVector = Icons.Default.Circle,
            tint = Color.White,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .border(10.dp, Color.White, CircleShape)
                .graphicsLayer {
                    alpha = 0.5f
                }
        )
    }
}

@Composable
internal fun CloseButton(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onClose,
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            tint = Color.White,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    alpha = 0.8f
                }
        )
    }
}


private suspend fun Context.getCameraProvider(): ProcessCameraProvider {
    return ProcessCameraProvider.getInstance(this).await()
}

//---------Preview----------------------------------------------------------------------------------
@LightDarkPreview
@Composable
fun PreviewCameraPreview() {
    PawCalcTheme {
        CameraPreview()
    }
}

@LightDarkPreview
@Composable
fun PreviewCameraShutterButton() {
    PawCalcTheme() {
        CameraShutterButton() {
            
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewCloseButton() {
    PawCalcTheme {
        CloseButton() {
            
        }
    }
}
