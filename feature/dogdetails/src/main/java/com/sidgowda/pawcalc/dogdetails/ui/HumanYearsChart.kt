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
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.daysInMonthToday
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.Orange500
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun HumanYearsChart(
    modifier: Modifier = Modifier,
    age: Age
) {
    val textMeasurer = rememberTextMeasurer()
    val daysColor = PawCalcTheme.colors.secondary
    val monthsColor = PawCalcTheme.colors.primary
    val yearsColor = Orange500
    val textStyle = PawCalcTheme.typography.error
    val yearsRange = age.getRangeForYears()
    val monthsRange = IntRange(0, 11)
    val daysRange = IntRange(0, daysInMonthToday(days = age.days))
    val startYears = 1f
    val startMonths = startYears
    val startDays = startMonths
    val animateToYearsEnd =  if (age.years == yearsRange.first) 1f else (age.years - yearsRange.first)/ 7.toFloat() * 360
    val animateToMonthsEnd = if (age.months == monthsRange.first) 1f else (age.months / 12.toFloat()) * 360
    val animateToDaysEnd = if (age.days == daysRange.first) 1f else (age.days / (daysRange.endInclusive + 1).toFloat()) * 360
    val daysAngle = remember {
       Animatable(initialValue = startYears)
    }
    val monthsAngle = remember {
        Animatable(initialValue = startMonths)
    }
    val yearsAngle = remember {
        Animatable(initialValue = startDays)
    }
    LaunchedEffect(key1 = daysAngle, key2 = monthsAngle, key3 = yearsAngle) {
        // add delay to allow compose to load ui
        delay(300)
        val animateJobs = listOf(
            launch {
                daysAngle.animateTo(animateToDaysEnd, animationSpec = tween(1_000))
            },
            launch {
                monthsAngle.animateTo(animateToMonthsEnd, animationSpec = tween(1_000))
            },
            launch {
                yearsAngle.animateTo(animateToYearsEnd, animationSpec = tween(1_000))
            }
        )
        animateJobs.joinAll()
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
        val daysRadius = radius.dp.toPx()
        val monthsRadius = (radius - strokeWidth).dp.toPx()
        val yearsRadius = (radius - strokeWidth*2).dp.toPx()
        val daysArcSize = Size(daysRadius*2,daysRadius*2)
        val monthsArcSize = Size(monthsRadius*2, monthsRadius*2)
        val yearsArcSize = Size(yearsRadius*2, yearsRadius*2)
        val daysStartOffset = 15
        val monthsStartOffset = daysStartOffset + strokeWidth
        val yearsStartOffset = monthsStartOffset + strokeWidth
        val digitOffsetX = 10
        val digitOffsetY = 7.5

        // Days Progress Circle
        createProgressCircle(
            arcColor = daysColor,
            radius = daysRadius,
            stroke = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
            alpha = alpha,
            startAngle = startAngle,
            arcSize = daysArcSize,
            sweepAngle = daysAngle.value,
            arcOffset = daysStartOffset.dp.toPx()
        )
        drawTextOnCircle(
            range = daysRange,
            textMeasurer = textMeasurer,
            radius = daysRadius,
            offsetX = digitOffsetX.dp.toPx(),
            offsetY = digitOffsetY.dp.toPx(),
            textStyle = textStyle
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
        drawTextOnCircle(
            range = monthsRange,
            textMeasurer = textMeasurer,
            radius = monthsRadius,
            offsetX = (digitOffsetX + strokeWidth).dp.toPx(),
            offsetY = (digitOffsetY + strokeWidth).dp.toPx(),
            textStyle = textStyle
        )
        // Years Background Arc
        createProgressCircle(
            arcColor = yearsColor,
            radius = yearsRadius,
            stroke = arcStroke,
            alpha = alpha,
            startAngle = startAngle,
            arcSize = yearsArcSize,
            sweepAngle = yearsAngle.value,
            arcOffset = yearsStartOffset.dp.toPx()
        )
        drawTextOnCircle(
            range = yearsRange,
            textMeasurer = textMeasurer,
            radius = yearsRadius,
            offsetX = (digitOffsetX + strokeWidth*2).dp.toPx(),
            offsetY = (digitOffsetY + strokeWidth*2).dp.toPx(),
            textStyle = textStyle
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

@OptIn(ExperimentalTextApi::class)
internal fun DrawScope.drawTextOnCircle(
    range: IntRange,
    textMeasurer: TextMeasurer,
    radius: Float,
    offsetX: Float,
    offsetY: Float,
    textStyle: TextStyle
) {
    val max = range.endInclusive - range.first + 1
    range.forEach {
        val angle = if (it == range.first) 360.toFloat() else ((it-range.first)/max.toFloat()) * 360
        val convertSweepAngle = convertedSweepAngle(angle).toDouble()
        val x = convertedRadiusX(radius, convertSweepAngle) + offsetX
        val y = convertedRadiusY(radius, convertSweepAngle) + offsetY
        drawText(
            textMeasurer = textMeasurer,
            text = it.toString(),
            topLeft = Offset(x, y),
            style = textStyle
        )
    }
}

@LightDarkPreview
@Composable
internal fun PreviewHumanYearsChart() {
    PawCalcTheme {
        HumanYearsChart(
            age = Age(
                years = 18,
                months = 5,
                days = 28
            )
        )
    }
}
