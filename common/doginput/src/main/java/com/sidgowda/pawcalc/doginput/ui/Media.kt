package com.sidgowda.pawcalc.doginput.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
internal fun OpenMedia(
    modifier: Modifier = Modifier,
    onClose: () -> Unit,
    onSavePhoto: (Uri) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize().background(Color.Red),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Media Open")
    }
}
