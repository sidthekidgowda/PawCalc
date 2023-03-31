package com.sidgowda.pawcalc.doginput.ui

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun cameraPermissions(): PermissionState {
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
internal fun mediaPermissions(): PermissionState {
   return if (
        android.os.Build.VERSION.SDK_INT >=
        android.os.Build.VERSION_CODES.TIRAMISU
    ) {
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = "This permission is important for this app. Please grant this permission.",
    content: @Composable () -> Unit
) {
    val permissionState = rememberPermissionState(permission = permission)
}
