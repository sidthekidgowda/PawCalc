package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme

@Composable
fun PawCalcTopAppBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    title: @Composable () -> Unit,
    action: @Composable () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        backgroundColor = PawCalcTheme.colors.primarySurface(),
        contentColor = PawCalcTheme.colors.onPrimarySurface()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            navigationIcon?.let { icon ->
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    icon()
                }
            }
            Row(modifier = Modifier.align(Alignment.Center)) {
                title()
            }
            Row(modifier = Modifier.align(Alignment.CenterEnd)) {
                action()
            }
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewPawCalcTopAppBar() {
    PawCalcTheme {
        PawCalcTopAppBar(
            title = {
                Text(
                    text = "Hello",
                    style = PawCalcTheme.typography.h1,
                    color = PawCalcTheme.colors.onPrimarySurface()
                )
            },
            action = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PawCalcTheme.colors.onPrimarySurface()
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = {

                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = PawCalcTheme.colors.onPrimarySurface()
                    )
                }
            }
        )
    }
}

@LightDarkPreview
@Composable
fun PreviewPawCalcTopAppBarWithoutNavigateBack() {
    PawCalcTheme {
        PawCalcTopAppBar(
            title = {
                Text(
                    text = "Hello",
                    style = PawCalcTheme.typography.h2,
                    color = PawCalcTheme.colors.onPrimarySurface()
                )
            },
            action = {
                IconButton(
                    onClick = {  }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PawCalcTheme.colors.onPrimarySurface()
                    )
                }
            },
            navigationIcon = null
        )
    }
}

