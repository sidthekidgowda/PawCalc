package com.sidgowda.pawcalc

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DogListScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(Color.Green)) {
        Text("Dog 1")
        Spacer(Modifier.height(20.dp))
        Text("Dog 2")
    }
}
