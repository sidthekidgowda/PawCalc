package com.sidgowda.pawcalc.doginput.ui

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun cameraPermissions(): PermissionState {
   return if (
        android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.TIRAMISU
    ) {
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    } else {
        rememberPermissionState(permission = Manifest.permission.CAMERA)
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun mediaPermissions(): PermissionState {
   return if (
        android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.TIRAMISU
    ) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
