package com.sidgowda.pawcalc.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        elevation = 4.dp,
        contentPadding = PaddingValues(horizontal = 12.dp),
        backgroundColor = PawCalcTheme.colors.primarySurface()
    ) {
        navigationIcon?.let {
            it()
        }
        Spacer(modifier = Modifier.weight(1.0f))
        title()
        Spacer(modifier = Modifier.weight(1.0f))
        action()
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
            navigationIcon = null
        )
    }
}

