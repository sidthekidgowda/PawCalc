package com.sidgowda.pawcalc.doginput.ui

import android.Manifest
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import com.sidgowda.pawcalc.camera.R
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import java.util.*

@Composable
fun mediaPermission(): String {
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
fun HandlePermission(
    permissionStatus: PermissionStatus,
    successContent: @Composable () -> Unit,
    firstTimeRequest: () -> Unit,
    deniedContent: @Composable () -> Unit
) {
    when {
        permissionStatus == PermissionStatus.Granted -> {
            successContent()
        }
        !permissionStatus.shouldShowRationale -> {
            // first time permissions are being requested
            LaunchedEffect(key1 = Unit) {
                firstTimeRequest()
            }
        }
        else -> {
            deniedContent()
        }
    }
}

@Composable
fun PermissionDialog(
    permission: String,
    requestPermission: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            // don't dismiss
        },
        title = {
            Text(
                text = stringResource(id = R.string.permission_denied_title, permission),
                style = PawCalcTheme.typography.h6.copy(fontSize = 18.sp),
                color = PawCalcTheme.colors.onSurface
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.permission_denied_message),
                style = PawCalcTheme.typography.body1.copy(fontSize = 14.sp),
                color = PawCalcTheme.colors.onSurface
            )
        },
        confirmButton = {
            TextButton(onClick = requestPermission) {
                Text(
                    text = stringResource(id = R.string.go_to_settings),
                    style = PawCalcTheme.typography.h6
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    style = PawCalcTheme.typography.h6
                )
            }
        }
    )
}

//---------Preview----------------------------------------------------------------------------------

@LightDarkPreview
@Composable
fun PreviewPermissionDialog() {
    PawCalcTheme {
        PermissionDialog(
            permission = "Camera",
            requestPermission = {},
            onCancel = {}
        )
    }
}
