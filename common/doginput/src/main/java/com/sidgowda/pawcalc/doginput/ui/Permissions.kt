package com.sidgowda.pawcalc.doginput.ui

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.*

@Composable
internal fun mediaPermission(): String {
   return if (
        android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.TIRAMISU
    ) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun rememberCameraAndMediaPermissions(): MultiplePermissionsState {
    return rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.CAMERA, mediaPermission())
    )
}
