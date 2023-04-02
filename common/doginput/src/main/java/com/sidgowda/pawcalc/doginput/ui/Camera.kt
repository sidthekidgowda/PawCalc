package com.sidgowda.pawcalc.doginput.ui

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ExperimentalZeroShutterLag
@Composable
internal fun OpenCamera(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val systemUiController = rememberSystemUiController()
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        systemUiController.isSystemBarsVisible = false
        systemUiController.isNavigationBarVisible = false
        systemUiController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
        val imageCaptureUseCase =
            ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG)
                .build()
        var imageCaptured by remember { mutableStateOf<ImageProxy?>(null) }

        if (imageCaptured == null) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                useCase = { previewUseCase = it }
            )
            CameraShutterButton(
                modifier = Modifier
                    .padding(vertical = 50.dp),
                onShutter = {
                    coroutineScope.launch {
                        imageCaptureUseCase.takePicture(
                            ContextCompat.getMainExecutor(context),
                            onSuccess = { image ->
                                // trigger state changes
                                imageCaptured = image
                            },
                            onFailure = {
                                Log.e("CameraPreview", "Failed to take image")
                            }
                        )
                    }
                }
            )
            CloseButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                onClose = onClose
            )
            LaunchedEffect(key1 = previewUseCase) {
                coroutineScope.launch {
                    val cameraProvider = context.getCameraProvider()
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, previewUseCase, imageCaptureUseCase
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", e)
                    }
                }
            }
        } else {
//            CapturedImage(modifier = Modifier.fillMaxSize())
            Text("Image Captured", modifier = Modifier.align(Alignment.Center))
        }
    }
}

@ExperimentalZeroShutterLag
@Composable
internal fun CameraPreview(
    modifier: Modifier = Modifier,
    useCase: (UseCase) -> Unit
) {
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
            useCase(
                Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
            )
            previewView
    })
}

@ExperimentalZeroShutterLag
@Composable
internal fun BoxScope.CapturedImage(
    modifier: Modifier = Modifier
) {

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

private suspend fun ImageCapture.takePicture(
    executor: Executor,
    onSuccess: (ImageProxy) -> Unit,
    onFailure: () -> Unit
) {
    suspendCoroutine { continuation ->
        takePicture(
            executor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onSuccess(image)
                    continuation.resume(Unit)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    onFailure()
                    continuation.resumeWithException(exception)
                }
            }
        )
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider {
    return ProcessCameraProvider.getInstance(this).await()
}

//---------Preview----------------------------------------------------------------------------------
@ExperimentalZeroShutterLag
@LightDarkPreview
@Composable
fun PreviewCameraPreview() {
    PawCalcTheme {
        CameraPreview(useCase = {})
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
