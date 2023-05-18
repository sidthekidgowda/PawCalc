package com.sidgowda.pawcalc.dogdetails.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.*
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sidgowda.pawcalc.data.date.Age
import com.sidgowda.pawcalc.data.date.daysInMonthToday
import com.sidgowda.pawcalc.dogdetails.model.LegendType
import com.sidgowda.pawcalc.ui.theme.LightDarkPreview
import com.sidgowda.pawcalc.ui.theme.Orange500
import com.sidgowda.pawcalc.ui.theme.PawCalcTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@Composable
internal fun HumanYearsChartWithLegend(
    modifier: Modifier = Modifier,
    age: Age,
    shouldAnimate: Boolean,
    onAnimationFinished: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .background(PawCalcTheme.colors.background)
            .fillMaxWidth()
    ) {
        val (legend, chart) =  createRefs()
        HumanYearsChart(
            modifier = Modifier
                .constrainAs(chart) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            age = age,
            shouldAnimate = shouldAnimate,
            onAnimationFinished = onAnimationFinished
        )
        Legend(
            modifier = Modifier
                .constrainAs(legend) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
                .padding(top = 10.dp, start = 10.dp)
        )
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun HumanYearsChart(
    modifier: Modifier = Modifier,
    age: Age,
    shouldAnimate: Boolean,
    onAnimationFinished: () -> Unit
) {
    val textMeasurer = rememberTextMeasurer()
    val daysColor = PawCalcTheme.colors.secondary
    val monthsColor = PawCalcTheme.colors.primary
    val yearsColor = Orange500
    val textStyle = PawCalcTheme.typography.error
    val yearsRange = age.getRangeForYears()
    val monthsRange = IntRange(0, 11)
    val daysRange = IntRange(0, daysInMonthToday(days = age.days))
    val startYears = if (shouldAnimate || age.years == yearsRange.first) {
        1f
    } else {
        (age.years - yearsRange.first)/ 7.toFloat() * 360
    }
    val startMonths = if (shouldAnimate || age.months == monthsRange.first) {
        1f
    } else {
        (age.months / 12.toFloat()) * 360
    }
    val startDays = if (shouldAnimate || age.days == daysRange.first) {
        1f
    } else {
        (age.days / (daysRange.endInclusive + 1).toFloat()) * 360
    }
    val animateToYearsEnd =  if (age.years == yearsRange.first) startYears else (age.years - yearsRange.first)/ 7.toFloat() * 360
    val animateToMonthsEnd = if (age.months == monthsRange.first) startMonths else (age.months / 12.toFloat()) * 360
    val animateToDaysEnd = if (age.days == daysRange.first) startDays else (age.days / (daysRange.endInclusive + 1).toFloat()) * 360
    val daysAngle = remember {
       Animatable(initialValue = startDays)
    }
    val monthsAngle = remember {
        Animatable(initialValue = startMonths)
    }
    val yearsAngle = remember {
        Animatable(initialValue = startYears)
    }
    if (shouldAnimate) {
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
            // notify animation has finished
            onAnimationFinished()
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
        val convertedRadius = convertedRadiusXY(radius = radius, sweepAngle = angle)
        val x = convertedRadius.first + offsetX
        val y = convertedRadius.second + offsetY
        drawText(
            textMeasurer = textMeasurer,
            text = it.toString(),
            topLeft = Offset(x, y),
            style = textStyle
        )
    }
}

@Composable
internal fun Legend(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        LegendType.values().forEach { legend ->
            LegendItem(legendType = legend)
        }
    }
}

@Composable
internal fun LegendItem(
    modifier: Modifier = Modifier,
    legendType: LegendType
) {
    Row(
        modifier = modifier
            .padding(2.dp)
            .semantics(mergeDescendants = true) { },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(
                    if (MaterialTheme.colors.isLight)
                        legendType.lightThemeColor else legendType.darkThemeColor
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = legendType.stringId),
            color = PawCalcTheme.colors.iconTint(),
            style = PawCalcTheme.typography.error
        )
    }
}

//-------Preview------------------------------------------------------------------------------------

@LightDarkPreview
@Composable
internal fun PreviewHumanYearsChart() {
    PawCalcTheme {
        HumanYearsChart(
            age = Age(
                years = 18,
                months = 5,
                days = 28
            ),
            shouldAnimate = false,
            onAnimationFinished = {}
        )
    }
}

@LightDarkPreview
@Composable
internal fun PreviewLegendItemYears() {
    PawCalcTheme {
        LegendItem(
            modifier = Modifier.background(PawCalcTheme.colors.surface),
            legendType = LegendType.YEARS
        )
    }
}

@LightDarkPreview
@Composable
internal fun PreviewLegendItemDays() {
    PawCalcTheme {
        LegendItem(
            modifier = Modifier.background(PawCalcTheme.colors.surface),
            legendType = LegendType.DAYS
        )
    }
}

@LightDarkPreview
@Composable
internal fun PreviewLegendItemMonths() {
    PawCalcTheme {
        LegendItem(
            modifier = Modifier.background(PawCalcTheme.colors.surface),
            legendType = LegendType.MONTHS
        )
    }
}

@LightDarkPreview
@Composable
internal fun PreviewLegend() {
    PawCalcTheme {
        Legend(
            modifier = Modifier.background(PawCalcTheme.colors.surface)
        )
    }
}

@LightDarkPreview
@Composable
internal fun PreviewHumanChartWithLegend() {
    PawCalcTheme {
        HumanYearsChartWithLegend(
            modifier = Modifier.fillMaxWidth(),
            age = Age(
                years = 18,
                months = 5,
                days = 28
            ),
            shouldAnimate = false,
            onAnimationFinished = {}
        )
    }
}
