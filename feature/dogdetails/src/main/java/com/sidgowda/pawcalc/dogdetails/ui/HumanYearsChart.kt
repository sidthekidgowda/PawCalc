package com.sidgowda.pawcalc.dogdetails.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.launch

@Composable
internal fun HumanYearsChart(
    modifier: Modifier = Modifier
) {
    val arcColor = PawCalcTheme.colors.secondary
    val pawCalcImageVector = ImageVector.vectorResource(id = com.sidgowda.pawcalc.ui.R.drawable.ic_paw)
    val pawIcon = rememberVectorPainter(
        image = pawCalcImageVector
    )
    // only animate for first session
    // start at 1 to wrap paw icon
    val start = 1f
    // calculate where end is
    val end = 270f
    val angle = remember {
       Animatable(initialValue = start)
    }
    LaunchedEffect(key1 = angle) {
        launch {
            angle.animateTo(end, animationSpec = tween(1_000))
        }
    }
    Canvas(
        modifier = modifier
            .background(PawCalcTheme.colors.background)
            .size(300.dp)
            .padding(20.dp)
    ) {
        // Background Arc
        val radius = 115.dp.toPx()
        drawCircle(
            color = arcColor,
            radius = radius,
            alpha = .3f,
            style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
        )
        // Foreground Arc
        drawArc(
            color = arcColor,
            startAngle = 270f,
            sweepAngle = angle.value,
            topLeft = Offset(15.dp.toPx(), 15.dp.toPx()),
            useCenter = false,
            style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
            size = Size(230.dp.toPx(),230.dp.toPx())
        )
        // Paw icon
        val convertSweepAngle = convertedSweepAngle(angle.value).toDouble()
        val x = convertedRadiusX(radius, convertSweepAngle) + 5.dp.toPx()
        val y = convertedRadiusY(radius, convertSweepAngle) + 5.dp.toPx()
        translate(
            left = x,
            top = y
        ) {
            with(pawIcon) {
                draw(
                    size = Size(20.dp.toPx(), 20.dp.toPx())
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
fun PreviewHumanYearsChart() {
    PawCalcTheme {
        HumanYearsChart()
    }
}
