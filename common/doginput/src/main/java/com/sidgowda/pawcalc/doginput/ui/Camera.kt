package com.sidgowda.pawcalc.doginput.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.Surface.ROTATION_0
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
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
import androidx.core.net.toUri
import com.sidgowda.pawcalc.ui.R
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executor

@ExperimentalZeroShutterLag
@Composable
internal fun OpenCamera(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onSavePhoto: (Uri) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.BottomCenter
    ) {
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
        val imageCaptureUseCase =
            ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG)
                .setTargetRotation(ROTATION_0)
                .build()
        var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

        if (capturedImageUri == null) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                useCase = { previewUseCase = it },
                onShutter = {
                    imageCaptureUseCase.takePicture(
                        executor = Dispatchers.IO.asExecutor(),
                        coroutineScope = coroutineScope,
                        onSuccess = { uri ->
                            capturedImageUri = uri
                        },
                        onFailure = {
                            Log.e("CameraPreview", "Failed to take image")
                        }
                    )
                },
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
            ExpandedImageContainer(
                modifier = modifier.fillMaxSize(),
                image = capturedImageUri!!,
                onBack =  {
                    capturedImageUri = null
                },
                onSavePhoto = {
                    onSavePhoto(capturedImageUri!!)
                },
                fallback = R.drawable.ic_paw
            )
        }
    }
}

@ExperimentalZeroShutterLag
@Composable
internal fun BoxScope.CameraPreview(
    modifier: Modifier = Modifier,
    useCase: (UseCase) -> Unit,
    onShutter: () -> Unit,
    onClose: () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val previewView = PreviewView(context).apply {
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
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
    CameraShutterButton(
        modifier = Modifier
            .padding(vertical = 50.dp),
        onShutter = onShutter
    )
    MediaButton(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(16.dp),
        imageVector = Icons.Default.Close,
        onAction = onClose
    )
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

/**
 * Executor will run through Dispatchers.IO
 */
private fun ImageCapture.takePicture(
    executor: Executor,
    coroutineScope: CoroutineScope,
    onSuccess: (Uri) -> Unit,
    onFailure: () -> Unit
) {
    val tempFile = File.createTempFile("image", "jpeg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()
    takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            coroutineScope.launch {
                onSuccess(outputFileResults.savedUri ?: tempFile.toUri())
            }
        }
        override fun onError(exception: ImageCaptureException) {
            coroutineScope.launch {
                onFailure()
            }
        }
    })
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider {
    return ProcessCameraProvider.getInstance(this).await()
}

//---------Preview----------------------------------------------------------------------------------
@ExperimentalZeroShutterLag
@LightDarkPreview
@Composable
fun PreviewCamera() {
    PawCalcTheme {
        OpenCamera(
            onClose = {},
            onSavePhoto = {}
        )
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
