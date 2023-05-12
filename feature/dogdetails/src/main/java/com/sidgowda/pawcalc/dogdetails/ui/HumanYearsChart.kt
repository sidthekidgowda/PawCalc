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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.Orange500
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun HumanYearsChart(
    modifier: Modifier = Modifier
) {
    val yearsColor = PawCalcTheme.colors.secondary
    val monthsColor = PawCalcTheme.colors.primary
    val daysColor = Orange500
    val pawCalcImageVector = ImageVector.vectorResource(id = com.sidgowda.pawcalc.ui.R.drawable.ic_paw)
    val pawIcon = rememberVectorPainter(
        image = pawCalcImageVector
    )
    // only animate for first session
    // start at last seen position
    val start = 1f
    // calculate where end is
    val yearsEnd = 270f
    val monthsEnd = 180f
    val daysEnd = 90f
    val yearsAngle = remember {
       Animatable(initialValue = start)
    }
    val monthsAngle = remember {
        Animatable(initialValue = start)
    }
    val daysAngle = remember {
        Animatable(initialValue = start)
    }
    LaunchedEffect(key1 = yearsAngle, key2 = monthsAngle, key3 = daysAngle) {
        // add delay to allow compose to load ui
        delay(300)
        launch {
            yearsAngle.animateTo(yearsEnd, animationSpec = tween(1_000))
        }
        launch {
            monthsAngle.animateTo(monthsEnd, animationSpec = tween(1_000))
        }
        launch {
            daysAngle.animateTo(daysEnd, animationSpec = tween(1_000))
        }
    }
    Canvas(
        modifier = modifier
            .background(PawCalcTheme.colors.background)
            .size(300.dp)
            .padding(20.dp)
    ) {
        // define variables
        val strokeWidth = 30
        val arcStroke = Stroke(strokeWidth.dp.toPx(), cap = StrokeCap.Round)
        val startAngle = 270f
        val radius = 115
        val alpha = .3f
        val yearsRadius = radius.dp.toPx()
        val monthsRadius = (radius - strokeWidth).dp.toPx()
        val daysRadius = (radius - strokeWidth*2).dp.toPx()
        val yearsArcSize = Size(yearsRadius*2,yearsRadius*2)
        val monthsArcSize = Size(monthsRadius*2, monthsRadius*2)
        val daysArcSize = Size(daysRadius*2, daysRadius*2)
        val yearsStartOffset = 15
        val monthsStartOffset = yearsStartOffset + strokeWidth
        val daysStartOffset = monthsStartOffset + strokeWidth
        val startPawIconOffset = 5

        // Years Progress Circle
        createProgressCircle(
            arcColor = yearsColor,
            radius = yearsRadius,
            stroke = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
            alpha = alpha,
            startAngle = startAngle,
            arcSize = yearsArcSize,
            sweepAngle = yearsAngle.value,
            arcOffset = yearsStartOffset.dp.toPx()
        )
        // Paw icon
        drawPawIcon(
            pawIcon = pawIcon,
            sweepAngle = yearsAngle.value,
            radius = yearsRadius,
            offset = startPawIconOffset.dp.toPx()
        )

        // Months Background Arc
        createProgressCircle(
            arcColor = monthsColor,
            radius = monthsRadius,
            stroke = arcStroke,
            alpha = alpha,
            startAngle = startAngle,
            arcSize = monthsArcSize,
            sweepAngle = monthsAngle.value,
            arcOffset = monthsStartOffset.dp.toPx()
        )
        drawPawIcon(
            pawIcon = pawIcon,
            sweepAngle = monthsAngle.value,
            radius = monthsRadius,
            offset = (startPawIconOffset+strokeWidth).dp.toPx()
        )

        // Days Background Arc
        createProgressCircle(
            arcColor = daysColor,
            radius = daysRadius,
            stroke = arcStroke,
            alpha = alpha,
            startAngle = startAngle,
            arcSize = daysArcSize,
            sweepAngle = daysAngle.value,
            arcOffset = daysStartOffset.dp.toPx()
        )
        drawPawIcon(
            pawIcon = pawIcon,
            sweepAngle = daysAngle.value,
            radius = daysRadius,
            offset = (startPawIconOffset+strokeWidth*2).dp.toPx()
        )
    }
}

internal fun DrawScope.createProgressCircle(
    arcColor: Color,
    radius: Float,
    alpha: Float,
    stroke: Stroke,
    startAngle: Float,
    sweepAngle: Float,
    arcSize: Size,
    arcOffset: Float
) {
    drawCircle(
        color = arcColor,
        radius = radius,
        alpha = alpha,
        style = stroke,
    )
    // Foreground Arc
    drawArc(
        color = arcColor,
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        topLeft = Offset(arcOffset, arcOffset),
        useCenter = false,
        style = stroke,
        size = arcSize
    )
}

internal fun DrawScope.drawPawIcon(pawIcon: VectorPainter, sweepAngle: Float, radius: Float, offset: Float) {
    val convertSweepAngle = convertedSweepAngle(sweepAngle).toDouble()
    val x = convertedRadiusX(radius, convertSweepAngle) + offset
    val y = convertedRadiusY(radius, convertSweepAngle) + offset
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

@LightDarkPreview
@Composable
internal fun PreviewHumanYearsChart() {
    PawCalcTheme {
        HumanYearsChart()
    }
}
