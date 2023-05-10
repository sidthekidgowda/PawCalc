package com.sidgowda.pawcalc.dogdetails.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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

@Composable
internal fun HumanYearsChart(
    modifier: Modifier = Modifier
) {
    val arcColor = PawCalcTheme.colors.secondary
    val iconTint = PawCalcTheme.colors.iconTint()
    val pawCalcImageVector = ImageVector.vectorResource(id = com.sidgowda.pawcalc.ui.R.drawable.ic_paw)
    val pawIcon = rememberVectorPainter(
        image = pawCalcImageVector
    )
    Canvas(
        modifier = modifier
            .background(PawCalcTheme.colors.background)
            .size(300.dp)
            .padding(20.dp)
    ) {
        // Background Arc
        drawCircle(
            color = arcColor,
            radius = 115.dp.toPx(),
            alpha = .4f,
            style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
        )
        // Foreground Arc
        drawArc(
            color = arcColor,
            startAngle = 270f,
            sweepAngle = 30f,
            topLeft = Offset(15.dp.toPx(), 15.dp.toPx()),
            useCenter = false,
            style = Stroke(30.dp.toPx(), cap = StrokeCap.Round),
            size = Size(230.dp.toPx(),230.dp.toPx())
        )
        // Paw icon
        translate(
            left = 120.dp.toPx(),
            top = 5.dp.toPx()
        ) {
            with(pawIcon) {
                draw(
                    size = Size(20.dp.toPx(), 20.dp.toPx())
                )
            }
        }
        // calculate how to animate paw icon on the circle
        // text
    }
}

@LightDarkPreview
@Composable
fun PreviewHumanYearsChart() {
    PawCalcTheme {
        HumanYearsChart()
    }
}
